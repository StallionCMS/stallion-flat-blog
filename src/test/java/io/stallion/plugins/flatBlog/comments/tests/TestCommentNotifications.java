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


import io.stallion.asyncTasks.AsyncCoordinator;
import io.stallion.asyncTasks.AsyncTask;
import io.stallion.asyncTasks.AsyncTaskController;
import io.stallion.asyncTasks.AsyncTaskExecuteRunnable;
import io.stallion.dataAccess.DataAccessRegistry;
import io.stallion.email.EmailSender;
import io.stallion.plugins.PluginRegistry;
import io.stallion.plugins.flatBlog.comments.CommentsEndpoints;
import io.stallion.plugins.flatBlog.comments.Comment;
import io.stallion.plugins.flatBlog.comments.CommentSubscriptionInfo;
import io.stallion.plugins.flatBlog.comments.NewCommentEmailHandler;
import io.stallion.plugins.flatBlog.contacts.*;
import io.stallion.plugins.flatBlog.FlatBlogPlugin;
import io.stallion.services.Log;
import io.stallion.testing.AppIntegrationCaseBase;
import io.stallion.testing.StubHandler;
import io.stallion.testing.Stubbing;
import io.stallion.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static io.stallion.utils.Literals.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class TestCommentNotifications extends AppIntegrationCaseBase {
    private static Long parentId = 0L;
    private Contact john = null;
    private CommentsEndpoints resource = null;

    @BeforeClass
    public static void setUpClass() throws Exception {
        startApp("/blog_plugin_site");
        FlatBlogPlugin booter = new FlatBlogPlugin();
        PluginRegistry.instance().loadPluginFromBooter(booter);
        booter.boot();

        Stubbing.stub(CommentsEndpoints.class, "checkRECaptcha");
        Stubbing.stub(CommentsEndpoints.class, "checkAkismet");
        Stubbing.stub(NewCommentEmailHandler.class, "enqueue");


        parentId = DataAccessRegistry.instance().getTickets().nextId();




    }

    @Before
    public void setUp() throws Exception {
        resource = new CommentsEndpoints();
        resource.submitComment(newComment("test.john@stallion.io", "John Doe", "First!"));
        john = ContactsController.instance().forUniqueKeyOrNotFound("email", "test.john@stallion.io");
    }

    @Test
    public void testThreadSubscribe() throws Exception {
        CommentsEndpoints resource = new CommentsEndpoints();
        Long now = mils() - 2000;

        // Submit initial comment from Peter, no notifications yet.
        String peterEmail = "test.peter" + DateUtils.mils() + "@stallion.io";
        resource.submitComment(
                newComment(peterEmail, "Peter", "I eagerly await further thoughts!")
                .setThreadSubscribe(true)
        );
        Contact peter = ContactsController.instance().forUniqueKeyOrNotFound("email", peterEmail);
        Assert.assertEquals(0, NotificationController.instance().filter("createdAt", now, ">").filter("contactId", john.getId()).count());
        Assert.assertEquals(0, NotificationController.instance().filter("createdAt", now, ">").filter("contactId", peter.getId()).count());

        // Add two comments
        resource.submitComment(
                newComment("test.sam@stallion.io", "Sam", "This is an insightful thought.")
        );
        resource.submitComment(
                newComment("test.shannon@stallion.io", "Shannon", "This is an incredible thing.")
        );


        // Assert tasks and notifications created
        assertTrue(AsyncCoordinator.instance().getPendingTaskCount() > 0);
        List<Notification> notifications = NotificationController
                .instance()
                .filter("createdAt", now, ">")
                .filter("contactId", peter.getId())
                .all();
        Assert.assertEquals(2, notifications.size());
        Assert.assertEquals(notifications.get(0).getSendAt(), notifications.get(1).getSendAt());

        Notification notification = notifications.get(0);
        String customKey = "notification---" + notification.getSendAt() + "---" + notification.getContactId();
        Assert.assertTrue(AsyncCoordinator.instance().hasPendingTaskWithCustomKey(customKey));

        // Now try running the task for Peter, the user with a daily email
        // Should get 1 email with 2 comments in it

        MockEmailer mockEmailer = new MockEmailer();
        Stubbing.stub(EmailSender.class, "executeSend", mockEmailer);


        AsyncTask task = AsyncTaskController.instance().forUniqueKey("customKey", customKey);
        AsyncTaskExecuteRunnable runnable = new AsyncTaskExecuteRunnable(task);
        runnable.run(true);

        // Should be an email sent with two comments in it
        assertEquals(1, mockEmailer.getEmails().size());
        assertEquals(peterEmail, mockEmailer.getEmails().get(0).getAddress());
        Log.info("Notify email body: {0}", mockEmailer.getEmails().get(0).getBody());
        String body = mockEmailer.getEmails().get(0).getBody();
        assertTrue(body.contains("/my-subscriptions/"));
        assertTrue("/my-subscriptions/ contact token is empty", !body.contains("/my-subscriptions/\">"));

        assertEquals(2, StringUtils.countMatches(mockEmailer.getEmails().get(0).getBody(), "class=\"comment-bodyHtml\""));



    }


    @Test
    public void testMentionSubscribe() throws Exception {
        CommentsEndpoints resource = new CommentsEndpoints();
        Long now = mils() - 2000;

        resource.submitComment(
                newComment("test.jack@stallion.io", "Jack O'Connor", "I hope someone replies to me and mentions me!")
                        .setMentionSubscribe(true)
        );

        Assert.assertEquals(0, NotificationController.instance().filter("createdAt", now, ">").filter("contactId", john.getId()).count());

        resource.submitComment(
                newComment("test.jamie@stallion.io", "Jamie", "Hey @\"Jack O'Connor\", I will reply to you.")
        );

        resource.submitComment(
                newComment("test.jody@stallion.io", "Jody", "Hey @Jack, not a reply.")
        );

        Contact jack = ContactsController.instance().forUniqueKeyOrNotFound("email", "test.jack@stallion.io");


        List<Notification> notifications = NotificationController.instance().filter("createdAt", now, ">").filter("contactId", jack.getId()).all();
        Assert.assertEquals(1, notifications.size());


        String customKey = "notification---" + notifications.get(0).getSendAt() + "---" + notifications.get(0).getContactId();
        assertTrue(AsyncCoordinator.instance().getPendingTaskCount() > 0);
        Assert.assertTrue(AsyncCoordinator.instance().hasPendingTaskWithCustomKey(customKey));

    }


    @Test
    public void testMentionEmails() throws Exception {
        CommentsEndpoints resource = new CommentsEndpoints();
        Long now = mils() - 2000;

        // Initial comment
        String timEmail = "test.tim." + now + "@stallion.io";
        resource.submitComment(
                newComment(timEmail, "Timothy", "I hope someone replies to me and mentions me!")
                        .setMentionSubscribe(true)
        );
        Contact tim = ContactsController.instance().forUniqueKeyOrNotFound("email", timEmail);
        // No notifications yet
        Assert.assertEquals(0, NotificationController.instance().filter("createdAt", now, ">").filter("contactId", john.getId()).count());
        Assert.assertEquals(0, NotificationController.instance().filter("createdAt", now, ">").filter("contactId", tim.getId()).count());

        // Two people reply to Timothy, one additional comment
        resource.submitComment(
                newComment("test.sherry@stallion.io", "Sherry", "Hey @Timothy, I will reply to you.")
        );
        resource.submitComment(
                newComment("test.travis@stallion.io", "Travis", "Hey @Timothy, have a second reply.")
        );
        resource.submitComment(
                newComment("test.mike@stallion.io", "Mike", "This is an unrelated comment.")
        );

        // Two notifications, with the same send time
        List<Notification> notifications = NotificationController
                .instance()
                .filter("createdAt", now, ">")
                .filter("contactId", tim.getId())
                .all();
        Assert.assertEquals(2, notifications.size());
        Assert.assertEquals(notifications.get(0).getSendAt(), notifications.get(1).getSendAt());

        // Verify pending task
        Notification notification = notifications.get(0);
        String customKey = "notification---" + notification.getSendAt() + "---" + notification.getContactId();
        assertTrue(AsyncCoordinator.instance().getPendingTaskCount() > 0);
        Assert.assertTrue(AsyncCoordinator.instance().hasPendingTaskWithCustomKey(customKey));



        // Now try running the task for 'three', the user with a daily email
        // Should get 1 email with 2 comments in it

        MockEmailer mockEmailer = new MockEmailer();
        Stubbing.stub(EmailSender.class, "executeSend", mockEmailer);


        AsyncTask task = AsyncTaskController.instance().forUniqueKey("customKey", customKey);
        AsyncTaskExecuteRunnable runnable = new AsyncTaskExecuteRunnable(task);
        runnable.run(true);

        // Should be an email sent with two comments in it
        assertEquals(1, mockEmailer.getEmails().size());
        assertEquals(timEmail, mockEmailer.getEmails().get(0).getAddress());
        Log.info("Notify email body: {0}", mockEmailer.getEmails().get(0).getBody());
        assertEquals(2, StringUtils.countMatches(mockEmailer.getEmails().get(0).getBody(), "class=\"comment-bodyHtml\""));


    }



    public Comment newComment(String email, String displayName, String bodyHtml) {
        Comment cmt = new Comment()
                .setAuthorEmail(email)
                .setAuthorDisplayName(displayName)
                .setBodyMarkdown(bodyHtml)
                .setBodyHtml(bodyHtml);
        cmt.setParentTitle("Test Post");
        cmt.setThreadId(parentId);
        cmt.setModeratedAt(mils());
        cmt.setAkismetApproved(true);
        cmt.setParentPermalink("theparentpermalink");
        cmt.setMentionSubscribe(false);
        cmt.setThreadSubscribe(false);
        return cmt;
    }

    /*
    //@Test
    public void testNotifications() throws Exception {
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

        Long parentId = DataAccessRegistry.instance().getTickets().nextId();
        for (Comment cmt: comments) {
            cmt.setParentTitle("Test Post");
            cmt.setThreadId(parentId);
            cmt.setModeratedAt(mils());
            cmt.setAkismetApproved(true);
            cmt.setParentPermalink("theparentpermalink");
        }
        Long now = mils() - 2000;

        CommentsEndpoints resource = new CommentsEndpoints();
        /*
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

    }
    */

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
