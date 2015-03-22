/*
 * Copyright © 2015 Juan José Aguililla. All rights reserved.
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

package sabina.it.jetty;

import static sabina.util.TestUtil.*;

import java.io.IOException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test public class GenericIT extends sabina.it.undertow.GenericIT {

    @BeforeClass public static void setup () throws InterruptedException, IOException {
        resetBackend ();
        setBackend ("jetty");
        sabina.it.undertow.GenericIT.setup ();
    }

    public void notFoundJetty () throws Exception {
        notFound ();
    }
}