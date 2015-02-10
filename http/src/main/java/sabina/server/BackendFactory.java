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

package sabina.server;

import static java.lang.System.getProperty;

/**
 * @author Per Wendel
 */
public final class BackendFactory {
    public static final String IMPL =
        getProperty ("sabina.backend") == null? "undertow" : getProperty ("sabina.backend");

    private BackendFactory () {
        throw new IllegalStateException ();
    }

    private static Backend createJetty (boolean hasMultipleHandler) {
        return new JettyServer (createFilter (hasMultipleHandler));
    }

    private static MatcherFilter createFilter (boolean hasMultipleHandler) {
        MatcherFilter matcherFilter = new MatcherFilter (false, hasMultipleHandler);
        matcherFilter.init (null); // init is empty (left here in case is implemented)
        return matcherFilter;
    }

    private static Backend createUndertow (boolean hasMultipleHandler) {
        return new UndertowServer (createFilter (hasMultipleHandler));
    }

    public static Backend create (boolean hasMultipleHandler) {
        switch (IMPL) {
            case "jetty":
                return createJetty (hasMultipleHandler);
            case "undertow":
                return createUndertow (hasMultipleHandler);
            default:
                throw new IllegalStateException ();
        }
    }
}