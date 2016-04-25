package io.stallion.plugins.flatBlog.comments.tests;


import io.stallion.asyncTasks.AsyncCoordinator;
import io.stallion.dal.DalRegistry;
import io.stallion.email.EmailSender;
import io.stallion.plugins.PluginRegistry;
import io.stallion.plugins.flatBlog.comments.CommentsEndpoints;
import io.stallion.plugins.flatBlog.comments.Comment;
import io.stallion.plugins.flatBlog.comments.CommentSubscriptionInfo;
import io.stallion.plugins.flatBlog.comments.NewCommentEmailHandler;
import io.stallion.plugins.flatBlog.contacts.Notification;
import io.stallion.plugins.flatBlog.contacts.NotificationController;
import io.stallion.plugins.flatBlog.contacts.SubscriptionFrequency;
import io.stallion.plugins.flatBlog.FlatBlogPlugin;
import io.stallion.testing.AppIntegrationCaseBase;
import io.stallion.testing.StubHandler;
import io.stallion.testing.Stubbing;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.util.ArrayList;
import java.util.List;

import static io.stallion.utils.Literals.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class TestCommentNotifications extends AppIntegrationCaseBase {


    @BeforeClass
    public static void setUpClass() throws Exception {
        startApp("/blog_plugin_site");
        PluginRegistry.instance().loadPluginFromBooter(new FlatBlogPlugin());

    }


    //@Test
    public void testNotifications() throws Exception {
        Stubbing.stub(CommentsEndpoints.class, "checkRECaptcha");
        Stubbing.stub(CommentsEndpoints.class, "checkAkismet");
        Stubbing.stub(NewCommentEmailHandler.class, "enqueue");
        MockEmailer mockEmailer = new MockEmailer();
        Stubbing.stub(EmailSender.class, "executeSend", mockEmailer);

        Comment one = new Comment()
                .setAuthorEmail("testing+one@stallion.io")
                .setAuthorDisplayName("One")
                .setBodyHtml("A firsting comment.")
                ;
        CommentSubscriptionInfo oneSub = new CommentSubscriptionInfo()
                .setReplyNotifyFrequency(SubscriptionFrequency.INSTANT)
                .setThreadNotifyFrequency(SubscriptionFrequency.NEVER);

        Comment two = new Comment()
                .setAuthorEmail("testing+two@stallion.io")
                .setAuthorDisplayName("Two")
                .setBodyHtml("A seconding comment.");
        CommentSubscriptionInfo twoSub = new CommentSubscriptionInfo()
                .setReplyNotifyFrequency(SubscriptionFrequency.NEVER)
                .setThreadNotifyFrequency(SubscriptionFrequency.INSTANT);

        Comment three = new Comment()
                .setAuthorEmail("testing+three@stallion.io")
                .setAuthorDisplayName("Three")
                .setBodyHtml("A thirding comment.");
        CommentSubscriptionInfo threeSub = new CommentSubscriptionInfo()
                .setReplyNotifyFrequency(SubscriptionFrequency.DAILY)
                .setThreadNotifyFrequency(SubscriptionFrequency.DAILY);


        Comment four = new Comment()
                .setAuthorEmail("testing+four@stallion.io")
                .setAuthorDisplayName("Four")
                .setBodyHtml("A fourthing comment");
        CommentSubscriptionInfo fourSub = new CommentSubscriptionInfo()
                .setReplyNotifyFrequency(SubscriptionFrequency.NEVER)
                .setThreadNotifyFrequency(SubscriptionFrequency.NEVER);


        Comment five = new Comment()
                .setAuthorEmail("testing+five@stallion.io")
                .setAuthorDisplayName("Five")
                .setBodyHtml("A fifthing comment");
        CommentSubscriptionInfo fiveSub = new CommentSubscriptionInfo()
                .setReplyNotifyFrequency(SubscriptionFrequency.INSTANT)
                .setThreadNotifyFrequency(SubscriptionFrequency.NEVER);
        Comment threeAgain = new Comment()
                .setAuthorEmail("testing+three@stallion.io")
                .setAuthorDisplayName("Three")
                .setBodyHtml("A thirding comment, that happened again");
        Comment six = new Comment()
                .setAuthorEmail("testing+six@stallion.io")
                .setAuthorDisplayName("Six")
                .setBodyHtml("A sixing comment");
        Comment seven = new Comment()
                .setAuthorEmail("testing+seven@stallion.io")
                .setAuthorDisplayName("Seven")
                .setBodyHtml("A sevening comment A mention of @One");

        List<Comment> comments = list(one, two, three, four, five, threeAgain, six, seven);

        String parentId = DalRegistry.instance().getTickets().nextId().toString();
        for (Comment cmt: comments) {
            cmt.setParentTitle("Test Post");
            cmt.setThreadId(parentId);
            cmt.setModeratedAt(mils());
            cmt.setAkismetApproved(true);
            cmt.setParentPermalink("theparentpermalink");
        }
        Long now = mils() - 2000;

        CommentsEndpoints resource = new CommentsEndpoints();

        one = resource.submitComment(one);
        resource.updateCommentSubscriptions(one, oneSub);

        assertEquals(0, AsyncCoordinator.instance().getPendingTaskCount());
        Assert.assertEquals(0, NotificationController.instance().filter("createdAt", now, ">").count());

        two = resource.submitComment(two);
        resource.updateCommentSubscriptions(two, twoSub);
        assertEquals(0, AsyncCoordinator.instance().getPendingTaskCount());
        Assert.assertEquals(0, NotificationController.instance().filter("createdAt", now, ">").count());

        three = resource.submitComment(three);
        resource.updateCommentSubscriptions(three, threeSub);
        // Two gets a task due to notification of comment Three

        assertEquals(1, AsyncCoordinator.instance().getPendingTaskCount());
        Assert.assertEquals(1, NotificationController.instance().filter("createdAt", now, ">").count());
        Notification notifyTwo = NotificationController.instance().filter("createdAt", now, ">").first();

        four = resource.submitComment(four);
        resource.updateCommentSubscriptions(four, fourSub);
        // Three gets a task due to comment Four
        //assertEquals(2, AsyncTaskController.instance().all().size());

        Notification notifyThree = NotificationController.instance().filter("contactId", three.getContactId().toString()).filter("createdAt", now, ">").first();
        // Two also gets a notification of comment four
        Assert.assertEquals(3, NotificationController.instance().filter("createdAt", now, ">").count());

        five = resource.submitComment(five);
        resource.updateCommentSubscriptions(five, fiveSub);
        // No new notifications, since four does not subscribe
        //assertEquals(2, AsyncTaskController.instance().all().size());

        // Two and Three get notifications, but no new task
        Assert.assertEquals(5, NotificationController.instance().filter("createdAt", now, ">").count());

        threeAgain = resource.submitComment(threeAgain);
        // No new tasks
        //assertEquals(2, AsyncTaskController.instance().all().size());
        assertEquals(6, NotificationController.instance().filter("createdAt", now, ">").count());

        six = resource.submitComment(six);
        // No new tasks
        //assertEquals(2, AsyncTaskController.instance().all().size());
        assertEquals(8, NotificationController.instance().filter("createdAt", now, ">").count());

        seven = resource.submitComment(seven);
        // One gets notified of comment Seven
        //assertEquals(3, AsyncTaskController.instance().all().size());
        assertEquals(11, NotificationController.instance().filter("createdAt", now, ">").count());



        // Now try running the task for 'three', the user with a daily email
        // Should get 1 email with 5 comments in it
        //AsyncTask threeTask = AsyncTaskController.instance().forUniqueKey("customKey", "notification---" + notifyThree.getSendAt() + "---" + notifyThree.getContactId());
        //AsyncTaskExecuteRunnable runnable = new AsyncTaskExecuteRunnable(threeTask);
        //runnable.run();

        /*
        assertEquals(1, mockEmailer.getEmails().size());
        assertEquals("testing+three@stallion.io", mockEmailer.getEmails().get(0).getAddress());
        Log.info("Notify email body: {0}", mockEmailer.getEmails().get(0).getBodyHtml());
        assertEquals(4, StringUtils.countMatches(mockEmailer.getEmails().get(0).getBodyHtml(), "class=\"comment-body\""));


        // Reset the mock emailer
        mockEmailer = new MockEmailer();
        Stubbing.stub(Emailer.class, "executeSend", mockEmailer);


        // Now try running one of the task for 'two', the commenter with an instant email
        // Should get 1 email with 5 comments in it
        //AsyncTask twoTask = AsyncTaskController.instance().forUniqueKey("customKey", "notification---" + notifyTwo.getSendAt() + "---" + notifyTwo.getContactId());
        //runnable = new AsyncTaskExecuteRunnable(twoTask);
        //runnable.run();

        assertEquals(1, mockEmailer.getEmails().size());
        assertEquals("testing+two@stallion.io", mockEmailer.getEmails().get(0).getAddress());
        Log.info("Notify email body: {0}", mockEmailer.getEmails().get(0).getBodyHtml());
        assertEquals(6, StringUtils.countMatches(mockEmailer.getEmails().get(0).getBodyHtml(), "class=\"comment-body\""));
         */
    }

    static class EmailInfo {
        private String address;
        private String body;

        public String getAddress() {
            return address;
        }

        public EmailInfo setAddress(String address) {
            this.address = address;
            return this;
        }

        public String getBody() {
            return body;
        }

        public EmailInfo setBody(String body) {
            this.body = body;
            return this;
        }
    }


    static class MockEmailer implements StubHandler {
        private List<EmailInfo> emails = new ArrayList<EmailInfo>();


        public Object execute(Object... params) throws Exception {
            EmailSender emailer = (EmailSender)params[0];
            getEmails().add(new EmailInfo().setAddress(emailer.getTos().get(0)).setBody(emailer.getHtml()));
            return null;
        }

        public List<EmailInfo> getEmails() {
            return emails;
        }

        public void setEmails(List<EmailInfo> emails) {
            this.emails = emails;
        }
    }
}
