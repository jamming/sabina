/*
 * Copyright © 2011 Per Wendel. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package sabina.it.undertow;

import static java.lang.System.getProperty;
import static java.lang.System.out;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static sabina.Sabina.*;
import static sabina.util.TestUtil.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import sabina.Request;
import sabina.util.TestUtil;

@Test public class GenericIT {

    private static TestUtil testUtil = new TestUtil ();
    private static File tmpExternalFile;

    @AfterClass public static void cleanup () {
        stop ();
        testUtil.waitForShutdown ();
        if (tmpExternalFile != null)
            if (!tmpExternalFile.delete ())
                throw new IllegalStateException ();
    }

    @BeforeClass public static void setup () throws IOException, InterruptedException {
        setBackend ("undertow");

        tmpExternalFile = new File (getProperty ("java.io.tmpdir"), "externalFile.html");

        FileWriter writer = new FileWriter (tmpExternalFile);
        writer.write ("Content of external file");
        writer.flush ();
        writer.close ();

        before ("/protected/*", it -> it.halt (401, "Go Away!"));

        before ("/protected/*", "application/json", it ->
            it.halt (401, "{\"message\": \"Go Away!\"}")
        );

        get ("/request/data", it -> {
            it.response.body (it.url ());

            it.cookie ("method", it.requestMethod ());
            it.cookie ("host", it.ip ());
            it.cookie ("uri", it.uri ());
            it.cookie ("params", String.valueOf (it.params ().size ()));

            it.header ("agent", it.userAgent ());
            it.header ("protocol", it.protocol ());
            it.header ("scheme", it.scheme ());
            it.header ("host", it.host ());
            it.header ("query", it.queryString ());
            it.header ("port", String.valueOf (it.port ()));

            return it.response.body () + "!!!";
        });

        exception (
            UnsupportedOperationException.class,
            (ex, req) -> req.response.header ("error", ex.getMessage ())
        );

        get ("/exception", it -> { throw new UnsupportedOperationException ("error message"); });

        get ("/hi", "application/json", it -> "{\"message\": \"Hello World\"}");

        get ("/hi", it -> "Hello World!");

        get ("/param/:param", it -> "echo: " + it.params (":param"));

        get ("/paramandwild/:param/stuff/*", it ->
            "paramandwild: " + it.params (":param") + it.splat ()[0]
        );

        get ("/paramwithmaj/:paramWithMaj", it -> "echo: " + it.params (":paramWithMaj"));

        get ("/", it -> "Hello Root!");

        post ("/poster", it -> {
            String body = it.body ();
            it.response.status (201); // created
            return "Body was: " + body;
        });

        patch ("/patcher", it -> {
            String body = it.body ();
            it.response.status (200);
            return "Body was: " + body;
        });

        delete ("/method", Request::requestMethod);
        options ("/method", Request::requestMethod);
        get ("/method", Request::requestMethod);
        patch ("/method", Request::requestMethod);
        post ("/method", Request::requestMethod);
        put ("/method", Request::requestMethod);
        trace ("/method", Request::requestMethod);
        head ("/method", it -> {
            it.header ("header", it.requestMethod ());
        });

        after ("/hi", it -> it.response.header ("after", "foobar"));

        staticFileLocation ("/public");
        externalStaticFileLocation (getProperty ("java.io.tmpdir"));
        start (testUtil.getPort ());

        testUtil.waitForStartup ();
        resetBackend ();
    }

    public void filtersShouldBeAcceptTypeAware () throws Exception {
        UrlResponse response =
            testUtil.doMethod ("GET", "/protected/resource", null, "application/json");
        assertTrue (response.status == 401);
        assertEquals ("{\"message\": \"Go Away!\"}", response.body);
    }

    public void routesShouldBeAcceptTypeAware () throws Exception {
        UrlResponse response = testUtil.doMethod ("GET", "/hi", null, "application/json");
        assertEquals (200, response.status);
        assertEquals ("{\"message\": \"Hello World\"}", response.body);
    }

    public void getHi () {
        UrlResponse response = testUtil.doMethod ("GET", "/hi");
        assertEquals (200, response.status);
        assertEquals ("Hello World!", response.body);
    }

    public void hiHead () {
        UrlResponse response = testUtil.doMethod ("HEAD", "/hi");
        assertEquals (200, response.status);
        assertEquals ("", response.body);
    }

    public void getHiAfterFilter () {
        UrlResponse response = testUtil.doMethod ("GET", "/hi");
        assertTrue (response.headers.get ("after").contains ("foobar"));
    }

    public void getRoot () {
        UrlResponse response = testUtil.doMethod ("GET", "/");
        assertEquals (200, response.status);
        assertEquals ("Hello Root!", response.body);
    }

    public void paramAndWild () {
        UrlResponse response =
            testUtil.doMethod ("GET", "/paramandwild/thedude/stuff/andits");
        assertEquals (200, response.status);
        assertEquals ("paramandwild: thedudeandits", response.body);
    }

    public void echoParam1 () {
        UrlResponse response = testUtil.doMethod ("GET", "/param/shizzy");
        assertEquals (200, response.status);
        assertEquals ("echo: shizzy", response.body);
    }

    public void echoParam2 () {
        UrlResponse response = testUtil.doMethod ("GET", "/param/gunit");
        assertEquals (200, response.status);
        assertEquals ("echo: gunit", response.body);
    }

    public void echoParamWithUpperCaseInValue () {
        final String camelCased = "ThisIsAValueAndSabinaShouldRetainItsUpperCasedCharacters";
        UrlResponse response = testUtil.doMethod ("GET", "/param/" + camelCased);
        assertEquals (200, response.status);
        assertEquals ("echo: " + camelCased, response.body);
    }

    @Test (enabled=false) public void twoRoutesWithDifferentCaseButSameName () {
        String lowerCasedRoutePart = "param";
        String uppperCasedRoutePart = "PARAM";

        registerEchoRoute (lowerCasedRoutePart);
        registerEchoRoute (uppperCasedRoutePart);
        assertEchoRoute (lowerCasedRoutePart);
        assertEchoRoute (uppperCasedRoutePart);
    }

    private static void registerEchoRoute (final String routePart) {
        get ("/tworoutes/" + routePart + "/:param", it ->
            routePart + " route: " + it.params (":param")
        );
    }

    private static void assertEchoRoute (String routePart) {
        final String expected = "expected";
        UrlResponse response =
            testUtil.doMethod ("GET", "/tworoutes/" + routePart + "/" + expected);
        assertEquals (200, response.status);
        assertEquals (routePart + " route: " + expected, response.body);
    }

    public void echoParamWithMaj () {
        UrlResponse response = testUtil.doMethod ("GET", "/paramwithmaj/plop");
        assertEquals (200, response.status);
        assertEquals ("echo: plop", response.body);
    }

    public void unauthorized () throws Exception {
        UrlResponse response = testUtil.doMethod ("GET", "/protected/resource");
        assertTrue (response.status == 401);
    }

    @Test (enabled = false) public void notFound () throws Exception {
        UrlResponse response = testUtil.doMethod ("GET", "/no/resource");
        assertTrue (response.status == 404);
    }

	public void fileNotFound () throws Exception {
		UrlResponse response = testUtil.doMethod ("GET", "/resource.html");
		assertTrue (response.status == 404);
	}

    public void postOk () {
        UrlResponse response = testUtil.doMethod ("POST", "/poster", "Fo shizzy");
        out.println (response.body);
        assertEquals (201, response.status);
        assertTrue (response.body.contains ("Fo shizzy"));
    }

    public void patchOk () {
        UrlResponse response = testUtil.doMethod ("PATCH", "/patcher", "Fo shizzy");
        out.println (response.body);
        assertEquals (200, response.status);
        assertTrue (response.body.contains ("Fo shizzy"));
    }

    public void staticFile () throws Exception {
        UrlResponse response = testUtil.doMethod ("GET", "/css/style.css");
        assertEquals (200, response.status);
        assertEquals ("/*\n * Content of css file\n */\n", response.body);
    }

    public void externalStaticFile () throws Exception {
        UrlResponse response = testUtil.doMethod ("GET", "/externalFile.html");
        assertEquals (200, response.status);
        assertEquals ("Content of external file", response.body);
    }

    // TODO Check with asserts
    public void requestData () throws Exception {
        UrlResponse response = testUtil.doMethod ("GET", "/request/data");

//        assertEquals ("error message", response.cookies.get ("method"));
//        assertEquals ("error message", response.cookies.get ("host"));
//        assertEquals ("error message", response.cookies.get ("uri"));
//        assertEquals ("error message", response.cookies.get ("params"));

//        assertEquals ("Apache-HttpClient/4.3.3 (java 1.5)", response.headers.get ("agent"));
//        assertEquals ("HTTP/1.1", response.headers.get ("protocol"));
//        assertEquals ("http", response.headers.get ("scheme"));
//        assertEquals ("localhost:4567", response.headers.get ("host"));
//        assertEquals ("error message", response.headers.get ("query"));
//        assertEquals ("4567", response.headers.get ("port"));

//        assertEquals (response.body, "http://localhost:4567/request/data!!!");
        assertEquals (200, response.status);
    }

    public void handleException () {
        UrlResponse response = testUtil.doMethod ("GET", "/exception");
        assertEquals ("error message", response.headers.get ("error"));
    }

    public void methods () {
        checkMethod ("HEAD", "header"); // Head does not support body message
        checkMethod ("DELETE");
        checkMethod ("OPTIONS");
        checkMethod ("GET");
        checkMethod ("PATCH");
        checkMethod ("POST");
        checkMethod ("PUT");
        checkMethod ("TRACE");
    }

    private void checkMethod (String methodName) {
        checkMethod (methodName, null);
    }

    private void checkMethod (String methodName, String headerName) {
        UrlResponse res = testUtil.doMethod (methodName, "/method");

        assertNotNull (res);
        assertNotNull (res.body);
        assertEquals (headerName == null? res.body : res.headers.get (headerName), methodName);
//        assertEquals (200, res.status);
    }
}