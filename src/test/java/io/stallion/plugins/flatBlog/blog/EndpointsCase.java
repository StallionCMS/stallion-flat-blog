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

package io.stallion.plugins.flatBlog.blog;

import io.stallion.plugins.PluginRegistry;
import io.stallion.plugins.flatBlog.FlatBlogPlugin;
import io.stallion.services.Log;
import io.stallion.testing.AppIntegrationCaseBase;
import io.stallion.testing.MockResponse;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class EndpointsCase  extends AppIntegrationCaseBase {


    @BeforeClass
    public static void setUpClass() throws Exception {
        startApp("/blog_plugin_site");
        FlatBlogPlugin booter = new FlatBlogPlugin();
        PluginRegistry.instance().loadPluginFromBooter(booter);
        booter.boot();

    }


    @Test
    public void testOnePost()
    {

        MockResponse response = client.get("/justice-at-sunrise");
        assertResponseContains(response, "At sunrise he summoned all hands");

        response = client.get("/a-draft-post");
        assertResponseDoesNotContain(response, "This post is still in draft form and should not be visible.", 404);

        response = client.get("/future-dated");
        assertResponseDoesNotContain(response, "We still have not colonized Mars :-(", 404);

    }

    @Test
    public void testPostListing()
    {
        MockResponse response = client.get("/blog/");
        Assert.assertEquals(200, response.getStatus());
        Log.finer("post root listing result: {0} ", response.getContent());
        Assert.assertTrue(response.getContent().contains("post-title"));
        Assert.assertTrue(!response.getContent().contains("This post is still in draft form and should not be visible."));
        Assert.assertTrue(!response.getContent().contains("We still have not colonized Mars :-("));
    }

    @Test
    public void testPostsByTag()
    {
        MockResponse response = client.get("/blog/by-tag/sci-fi");
        Assert.assertEquals(200, response.getStatus());
        Log.finer("flatBlog sci-fi tag result: {0} ", response.getContent());
        Assert.assertTrue(response.getContent().contains("post-container"));
        Assert.assertTrue(response.getContent().contains("Then, by the light of their own destruction, I saw them staggering and falling, and their supporters turning to run."));

    }

    @Test
    public void testRss()
    {
        MockResponse response = client.get("/blog/rss.xml");
        Assert.assertEquals(200, response.getStatus());
        Log.finer("rss result: {0} ", response.getContent());
        Assert.assertTrue(!response.getContent().contains("post-container"));
        Assert.assertTrue(response.getContent().contains("Then, by the light of their own destruction, I saw them staggering and falling, and their supporters turning to run."));

    }

}

