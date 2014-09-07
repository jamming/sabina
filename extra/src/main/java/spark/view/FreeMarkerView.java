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

package spark.view;

import java.io.IOException;
import java.io.StringWriter;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreeMarkerView {
    private static Configuration configuration = createFreemarkerConfiguration ();

    private static Configuration createFreemarkerConfiguration () {
        Configuration retVal = new Configuration ();
        retVal.setClassForTemplateLoading (FreeMarkerView.class, "freemarker");
        return retVal;
    }

    public static String renderFreeMarker (String viewName, Object model) {
        try {
            StringWriter stringWriter = new StringWriter ();

            Template template = configuration.getTemplate (viewName);
            template.process (model, stringWriter);

            return stringWriter.toString ();
        }
        catch (IOException | TemplateException e) {
            throw new IllegalArgumentException (e);
        }
    }
}