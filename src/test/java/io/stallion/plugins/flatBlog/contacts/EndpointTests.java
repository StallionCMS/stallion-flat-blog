/*
 * Stallion Flat-file Blog: A simple blog-engine
 *
 * Copyright (C) 2015 - 2016 Stallion Software LLC
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 2 of
 * the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl-2.0.html>.
 *
 */

package io.stallion.plugins.flatBlog.contacts;

import io.stallion.email.EmailSender;
import io.stallion.plugins.PluginRegistry;
import io.stallion.plugins.flatBlog.FlatBlogPlugin;
import io.stallion.services.Log;
import io.stallion.testing.AppIntegrationCaseBase;
import io.stallion.testing.MockResponse;
import io.stallion.testing.Stubbing;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.Map;

import static io.stallion.utils.Literals.*;
import static org.junit.Assert.assertEquals;


public class EndpointTests extends AppIntegrationCaseBase {
    @BeforeClass
    public static void setUpClass() throws Exception {
        //URL url = EndpointTests.class.getResource("/blog_plugin_site/app-data");
        //if (url != null && url.toString().length() > 10 && new File(url.toString()).isDirectory()) {
        //    FileUtils.deleteDirectory(new File(url.toString()));
        //}
        startApp("/blog_plugin_site");
        FlatBlogPlugin booter = new FlatBlogPlugin();
        PluginRegistry.instance().loadPluginFromBooter(booter);
        booter.boot();



    }

    @Test
    public void testSubmit() {
        Stubbing.stub(EmailSender.class, "executeSend");

        Map<String, Object> data = map(
                val("email", (Object)"tester@stallion.io"),
                val("pageTitle", (Object)"Contact us"),
                val("pageUrl", (Object)"http://localhost/contact-us"),
                val("data", (Object)map(
                        val("givenName", "Petr"),
                        val("familyName", "Parker"),
                        val("city", "Metropolis")
                ))
        );

        MockResponse response = client.post("/_stx/flatBlog/contacts/submit-form", data);
        Log.info("Response: {0} {1} {2}", response, response.getStatus(), response.getContent());
        assertEquals(200, response.getStatus());
    }
}
