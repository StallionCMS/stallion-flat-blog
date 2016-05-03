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

