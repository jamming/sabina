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

package spark.examples;

import static spark.Spark.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Example showing a very simple (and stupid) autentication filter that is executed before all
 * other resources.
 * <p>
 * When requesting the resource with e.g. http://localhost:4567/hello?user=some&password=guy
 * the filter will stop the execution and the client will get a 401 UNAUTHORIZED with the
 * content 'You are not welcome here'
 * <p>
 * When requesting the resource with e.g. http://localhost:4567/hello?user=foo&password=bar the
 * filter will accept the request and the request will continue to the /hello route.
 * <p>
 * Note: There is also an "after filter" that adds a header to the response
 *
 * @author Per Wendel
 */
class FilterExample {
    private static Map<String, String> usernamePasswords = new HashMap<> ();

    public static void main (String[] args) {

        usernamePasswords.put ("foo", "bar");
        usernamePasswords.put ("admin", "admin");

        before (it -> {
            String user = it.queryParams ("user");
            String password = it.queryParams ("password");

            String dbPassword = usernamePasswords.get (user);
            if (!(password != null && password.equals (dbPassword)))
                it.halt (401, "You are not welcome here!!!");
        });

        before ("/hello", it -> it.header ("Foo", "Set by second before filter"));

        get ("/hello", it -> "Hello World!");

        after ("/hello", it -> it.header ("spark", "added by after-filter"));
    }
}
