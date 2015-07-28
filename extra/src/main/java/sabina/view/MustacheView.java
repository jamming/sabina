/*
 * Copyright © 2014 Juan José Aguililla. All rights reserved.
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

package sabina.view;

import static com.samskivert.mustache.Mustache.compiler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.samskivert.mustache.Template;
import sabina.util.Io;

public class MustacheView {
    public static String renderMustache (String templateName, Object model) {
        InputStream resourceStream = Io.classLoader().getResourceAsStream (templateName);
        InputStreamReader resourceReader = new InputStreamReader (resourceStream);
        BufferedReader reader = new BufferedReader (resourceReader);
        Template template = compiler ().compile (reader);
        return template.execute (model);
    }
}
