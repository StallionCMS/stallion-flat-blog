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
        booter.setPluginRegistry(PluginRegistry.instance());
        booter.boot();
    }


    @Test
    public void testRenderCommentsAndForm() {
        MockResponse response = client.get("/random-thoughts");
        assertResponseContains(response, "comments-header");
        assertResponseContains(response, "<form class=\"st-comment-form ");
    }
}
