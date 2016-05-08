package io.stallion.plugins.flatBlog.comments.tests;

import io.stallion.Context;
import io.stallion.asyncTasks.SimpleAsyncRunner;
import io.stallion.email.EmailSender;
import io.stallion.plugins.PluginRegistry;
import io.stallion.plugins.flatBlog.comments.Comment;
import io.stallion.plugins.flatBlog.comments.CommentWrapper;
import io.stallion.plugins.flatBlog.comments.CommentsController;
import io.stallion.plugins.flatBlog.comments.Constants;
import io.stallion.plugins.flatBlog.FlatBlogPlugin;
import io.stallion.services.Log;
import io.stallion.testing.AppIntegrationCaseBase;
import io.stallion.testing.MockRequest;
import io.stallion.testing.MockResponse;
import io.stallion.testing.Stubbing;
import io.stallion.utils.json.JSON;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestEndpoints extends AppIntegrationCaseBase {

    @BeforeClass
    public static void setUpClass() throws Exception {
        startApp("/blog_plugin_site");
        FlatBlogPlugin booter = new FlatBlogPlugin();
        PluginRegistry.instance().loadPluginFromBooter(booter);
        booter.boot();



    }

    /*
    public class IdMaker implements Runnable {
        public void run() {
            for(int x = 0; x<2000;x++ ) {
                Log.info("Ticket: {0}", Context.dal().getTickets().nextId());
            }
        }
    }


    @Test
    public void testIds() {
        SimpleAsyncRunner.load();
        SimpleAsyncRunner.instance().submit(new IdMaker());
        SimpleAsyncRunner.instance().submit(new IdMaker());
        SimpleAsyncRunner.instance().submit(new IdMaker());
        SimpleAsyncRunner.instance().submit(new IdMaker());
        SimpleAsyncRunner.shutdown();
    }
    */

    @Test
    public void testCommentController() throws IOException {
        Comment comment = new Comment();
        comment.setAuthorDisplayName("Emperor Nero");
        comment.setAuthorWebSite("http://emperor.com");
        comment.setBodyHtml("You will submit my submit, you will submit to me! " + new Date().getTime());
        comment.setAuthorEmail(RandomStringUtils.randomAlphanumeric(20) + "@stallion.io");
        comment.setThreadId(910L);
        CommentsController.instance().save(comment);
        Log.info("New comment:        {0} {1}", comment.getId(), comment.getAuthorEmail());
        Comment retComment = CommentsController.instance().forId(comment.getId());
        Log.info("Ret comment:        {0} {1}", retComment.getId(), retComment.getAuthorEmail());
        CommentsController.instance().reset();
        retComment = CommentsController.instance().forId(comment.getId());
        Log.info("Post-reset comment: {0} {1} {2}", retComment.getId(), retComment.getAuthorEmail(), retComment.getFilePath());

        Comment comment2 = CommentsController.instance().filter("authorEmail", comment.getAuthorEmail()).first();
        Assert.assertNotNull(comment2);

        CommentsController.instance().softDelete(comment2);
        comment2 = CommentsController.instance().filter("authorEmail", comment.getAuthorEmail()).first();
        Assert.assertNull(comment2);
        CommentsController.instance().reset();
        comment2 = CommentsController.instance().filter("authorEmail", comment.getAuthorEmail()).first();
        Assert.assertNull(comment2);

        comment2 = CommentsController.instance().forIdWithDeleted(comment.getId());
        Assert.assertEquals(comment2.getDeleted(), true);
        comment2 = CommentsController.instance().forId(comment.getId());
        Assert.assertNull(comment2);


    }


    @Test
    public void testCommentCrud() throws IOException {

        Stubbing.stub(EmailSender.class, "executeSend");

        MockResponse response;

        // Create a comment
        Map<String, Object> params = new HashMap<String, Object>();
        Comment comment = new Comment();
        comment.setAuthorDisplayName("Emperor Nero");
        comment.setAuthorWebSite("http://emperor.com");
        comment.setBodyMarkdown("You will submit my submit, you will submit to me! " + new Date().getTime());
        comment.setAuthorEmail("nero@stallion.io");
        comment.setThreadId(910L);
        comment.setParentPermalink("http://localhost:8090/randoms");
        comment.setParentTitle("The random stuff");
        //params.put("author", author);
        //params.put("webSite", webSite);
        //params.put("body", body);
        //params.put("email", email);
        response = client.post("/_stx/flatBlog/comments/submit", comment);
        assertResponseContains(response, comment.getBodyHtml());
        String authorSecret = response.getCookie("stCommentAuthorKey").getValue();

        Comment resultComment = JSON.parse(response.getContent(), Comment.class);
        String newBody = "New body";
        resultComment.setBodyMarkdown(newBody);

        // Update a comment
        MockRequest request = new MockRequest("/_stx/flatBlog/comments/" + resultComment.getId() + "/revise", "POST");
        request.setDataObject(resultComment);
        request.setCookies(new Cookie(Constants.AUTHOR_SECRET_COOKIE, authorSecret));
        response = client.request(request);
        Log.finer("Revise response: {0}", response.getContent());
        Assert.assertEquals(200, response.getStatus());

        Comment retrieved = CommentsController.instance().forId(resultComment.getId());
        Assert.assertEquals("<p>" + newBody + "</p>", retrieved.getBodyHtml().trim());

        // Disapprove

        // Approve

        Stubbing.verifyAndReset();


    }

    @Test
    public void testCommentAvatar() {
        Comment comment = new Comment();
        comment.setAuthorDisplayName("emperor");
        CommentWrapper wrapper = comment.toWrapper();
        Assert.assertEquals("E", wrapper.getAvatarLetter());
        Assert.assertEquals("#774433", wrapper.getAvatarColor());
    }

    @Test
    public void testCommentHtml() {
        // Test comment form generation

        // Test comment results regeneration
    }


}
