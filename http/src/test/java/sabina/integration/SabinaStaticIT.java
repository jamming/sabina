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

package sabina.integration;

import static java.lang.System.getProperty;
import static sabina.Sabina.*;
import static sabina.integration.TestScenario.*;

import java.io.IOException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import sabina.Request;
import sabina.Route.VoidHandler;

@Test public class SabinaStaticIT {
    private static TestScenario testScenario = new TestScenario ("undertow", 4567, true, true);
    private static String part = "param";

    @BeforeClass public static void setupFile () throws IOException { SabinaIT.setupFile (); }

    @BeforeClass public static void setup () {
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

        get ("/exception", (VoidHandler)it -> {
            throw new UnsupportedOperationException ("error message");
        });

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

        get ("/halt", it -> {
            it.halt (500, "halted");
        });

        get ("/tworoutes/" + part + "/:param", it ->
                part + " route: " + it.params (":param")
        );

        get ("/tworoutes/" + part.toUpperCase () + "/:param", it ->
                part.toUpperCase () + " route: " + it.params (":param")
        );

        after ("/hi", it -> it.response.header ("after", "foobar"));

        secure (getKeyStoreLocation (), getKeystorePassword ());
        filesLocation ("/public", getProperty ("java.io.tmpdir"));

        start (testScenario.port);
        testScenario.waitForStartup ();
    }

    @AfterClass public static void cleanup () {
        stop ();
        testScenario.waitForShutdown ();
    }

    @AfterClass public static void cleanupFile () { SabinaIT.cleanupFile (); }

    public void filtersShouldBeAcceptTypeAware () {
        Generic.filtersShouldBeAcceptTypeAware (testScenario);
    }

    public void routesShouldBeAcceptTypeAware () {
        Generic.routesShouldBeAcceptTypeAware (testScenario);
    }

    public void echoParamWithUpperCaseInValue () {
        Generic.echoParamWithUpperCaseInValue (testScenario);
    }

    public void twoRoutesWithDifferentCase () { Generic.twoRoutesWithDifferentCase (testScenario); }
    public void getHi () { Generic.getHi (testScenario); }
    public void hiHead () { Generic.hiHead (testScenario); }
    public void getHiAfterFilter () { Generic.getHiAfterFilter (testScenario); }
    public void getRoot () { Generic.getRoot (testScenario); }
    public void paramAndWild () { Generic.paramAndWild (testScenario); }
    public void echoParam1 () { Generic.echoParam1 (testScenario); }
    public void echoParam2 () { Generic.echoParam2 (testScenario); }
    public void echoParamWithMaj () { Generic.echoParamWithMaj (testScenario); }
    public void unauthorized () { Generic.unauthorized (testScenario); }
    public void notFound () { Generic.notFound (testScenario); }
	public void fileNotFound () { Generic.fileNotFound (testScenario); }
    public void postOk () { Generic.postOk (testScenario); }
    public void patchOk () { Generic.patchOk (testScenario); }
    public void staticFile () { Generic.staticFile (testScenario); }
    public void externalStaticFile () { Generic.externalStaticFile (testScenario); }
    public void halt () { Generic.halt (testScenario); }
    public void requestData () { Generic.requestData (testScenario); }
    public void handleException () { Generic.handleException (testScenario); }
    public void methods () { Generic.methods (testScenario); }
}
