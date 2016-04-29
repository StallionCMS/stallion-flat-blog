package io.stallion.plugins.flatBlog.comments.tests;

import io.stallion.email.EmailSender;
import io.stallion.plugins.PluginRegistry;
import io.stallion.plugins.flatBlog.comments.Comment;
import io.stallion.plugins.flatBlog.comments.AdminNotifyCommentEmailer;
import io.stallion.plugins.flatBlog.FlatBlogPlugin;
import io.stallion.testing.AppIntegrationCaseBase;
import io.stallion.testing.Stubbing;
import io.stallion.users.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;


public class TestCommentEmailer extends AppIntegrationCaseBase {

    @BeforeClass
    public static void setUpClass() throws Exception {
        startApp("/blog_plugin_site");
        FlatBlogPlugin booter = new FlatBlogPlugin();
        booter.setPluginRegistry(PluginRegistry.instance());
        booter.boot();
    }

    @Test
    public void testEmails() {
        Stubbing.stub(EmailSender.class, "executeSend");

        Comment comment = new Comment();
        comment.setAuthorDisplayName("Emperor Nero");
        comment.setAuthorWebSite("http://emperor.com");
        comment.setBodyHtml("You will submit my submit, you will submit to me! " + new Date().getTime());
        comment.setAuthorEmail(RandomStringUtils.randomAlphanumeric(20) + "@stallion.io");
        comment.setThreadId(340L);
        comment.setId(350L);
        comment.setParentPermalink("http://localhost:8090/random-thoughts");
        comment.setParentId(360L);
        comment.setParentTitle("A Post for the Ages");
        User user = new User().setEmail("pat+stallion@pfitz.net");

        AdminNotifyCommentEmailer emailer = new AdminNotifyCommentEmailer(user, comment);
        emailer.sendEmail();

    }
}
