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

package io.stallion.plugins.flatBlog.comments.tests;

import io.stallion.plugins.PluginRegistry;
import io.stallion.plugins.flatBlog.FlatBlogPlugin;
import io.stallion.testing.AppIntegrationCaseBase;
import io.stallion.testing.MockResponse;
import org.junit.BeforeClass;
import org.junit.Test;


public class TestCommentHtml extends AppIntegrationCaseBase {


    @BeforeClass
    public static void setUpClass() throws Exception {
        startApp("/blog_plugin_site");
        FlatBlogPlugin booter = new FlatBlogPlugin();
        PluginRegistry.instance().loadPluginFromBooter(booter);
        booter.boot();
    }


    @Test
    public void testRenderCommentsAndForm() {
        MockResponse response = client.get("/random-thoughts");
        assertResponseContains(response, "stFlatCommentsContext");
        assertResponseContains(response, "st-comments-form-wrapper");
    }
}
