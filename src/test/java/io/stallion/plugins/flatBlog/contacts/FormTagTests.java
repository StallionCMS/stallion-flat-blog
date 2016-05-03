package io.stallion.plugins.flatBlog.contacts;

import io.stallion.plugins.PluginRegistry;
import io.stallion.plugins.flatBlog.FlatBlogPlugin;
import io.stallion.plugins.flatBlog.comments.CommentsController;
import io.stallion.services.Log;
import io.stallion.templating.TemplateRenderer;
import io.stallion.testing.AppIntegrationCaseBase;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

import static io.stallion.utils.Literals.*;
import static org.junit.Assert.assertTrue;


public class FormTagTests extends AppIntegrationCaseBase {
    @BeforeClass
    public static void setUpClass() throws Exception {
        startApp("/blog_plugin_site");
        FlatBlogPlugin booter = new FlatBlogPlugin();
        PluginRegistry.instance().loadPluginFromBooter(booter);
        booter.boot();


    }

    @Test
    public void testTags() {
        CommentsController.instance().forId(34L);
        Map<String, Object> context = map(val("itemName", (Object)"gobstopper"));
        String result = TemplateRenderer.instance().renderTemplate("contact-page.jinja", context);
        Log.info("Remplate result: {0}", result);
        assertTrue(result.contains("<input type=\"text\" class=\"form-control\" name=\"givenName\">"));
        assertTrue(result.contains("<form"));
        assertTrue(result.contains("</form>"));
        assertTrue(result.contains("<h2>Edit the item: gobstopper</h2>"));
        assertTrue(result.contains("<h1>This is a contact form!</h1>"));
    }
}
