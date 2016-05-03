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
