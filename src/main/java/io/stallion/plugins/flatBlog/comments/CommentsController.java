package io.stallion.plugins.flatBlog.comments;

import io.stallion.Context;
import io.stallion.dal.base.*;
import io.stallion.dal.file.JsonFilePersister;
import io.stallion.exceptions.NotFoundException;
import io.stallion.plugins.flatBlog.contacts.*;
import io.stallion.services.Log;
import io.stallion.utils.Sanitize;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.stallion.utils.Literals.*;

public class CommentsController extends StandardModelController<Comment> {

    private static Set<String> commentKeyFields;

    static {
        commentKeyFields = new HashSet<String>();
        commentKeyFields.add("threadId");
    }


    public CommentsController() {
        super();
    }


    public static CommentsController instance() {
        return (CommentsController) Context.dal().get("comments");
    }


    public void postCommentApproved(Comment newComment) {

        // We only register notifications once per comment, don't double email
        if (!empty(newComment.getNotificationsRegisteredAt())) {
            return;
        }
        newComment.setNotificationsRegisteredAt(mils());
        save(newComment);


        //sendNotifyEmailsForComment(comment, SubscriptionFrequency.INSTANT);
        List<Comment> threadComments = listForKey("threadId", newComment.getThreadId());
        Set<Long> seenContactIds = new HashSet<Long>();
        for(Comment threadComment: threadComments) {
            if (threadComment.getAuthorEmail().equals("testing+one@stallion.io") && newComment.getAuthorEmail().equals("testing+seven@stallion.io")) {
                Log.info("Notify {0}?", threadComment.getAuthorEmail());
            }
            // Skip the current comment
            if (threadComment.getId().equals(newComment.getId())) {
                continue;
            }
            // Don't notify commenters who never had their comment approved
            if (!threadComment.getIsApproved()) {
                continue;
            }
            // Don't notify people of their own comment, obviously
            if (newComment.getContactId().equals(threadComment.getContactId())) {
                continue;
            }
            // Only notify once per contact
            if (seenContactIds.contains(threadComment.getContactId())) {
                continue;
            }
            seenContactIds.add(threadComment.getContactId());

            Contact contact = ContactsController.instance().forId(threadComment.getContactId());
            if (contact == null) {
                Log.warn("The comment did not have a valid contact: comment={0} email={1} contactId={2}",
                        threadComment.getId(), threadComment.getAuthorEmail(), threadComment.getContactId()
                );
                continue;
            }


            Subscription subcriptionToUse = null;

            // Get the notfication frequency for the reply subscription
            Subscription replySub = SubscriptionController.instance().forUniqueKey("ownerKey",
                    getOwnerKeyForReplySubscription(threadComment, contact));
            if (wasMentionedInComment(contact, newComment)) {
                Log.info("Contact {0} was mentioned in comment {1}", contact.getDisplayName(), newComment.getBodyHtml());
            }
            if (replySub != null
                    && replySub.isEnabled()
                    && !replySub.getFrequency().equals(SubscriptionFrequency.NEVER)
                    && wasMentionedInComment(contact, newComment)) {
                subcriptionToUse = replySub;
            }
            Subscription threadSub = SubscriptionController.instance().forUniqueKey(
                    "ownerKey", getOwnerKeyForThreadSubscription(threadComment, contact)
            );

            // Get the nofication for the thread subscription, if more frequent, use that
            if (threadSub != null
                        && threadSub.isEnabled()
                        && !threadSub.getFrequency().equals(SubscriptionFrequency.NEVER)) {
                if (subcriptionToUse == null ||
                        threadSub.getFrequency().ordinal() < subcriptionToUse.getFrequency().ordinal()) {
                    subcriptionToUse = threadSub;
                }
            }
            // frequency is not null if the new comment matches a valid subscription
            if (subcriptionToUse != null) {
                Log.info("Register notification for comment={0} contact={1} subscriptionFreq={2}",
                        newComment.getAuthorDisplayName(), contact.getEmail(), subcriptionToUse.getFrequency());
                registerNotification(newComment, contact, subcriptionToUse);
            } else {
                Log.info("Contact {0} was not subscribed, no notification sent.", contact.getEmail());
            }
        }

    }

    private void registerNotification(Comment newComment, Contact notifyContact, Subscription subscription) {
        CommentNotificationCallback handler = new CommentNotificationCallback()
                .setCommentId(newComment.getId());
        Notification notification = new Notification()
                .setHandler(handler)
                .setCallbackPlugin("comments")
                .setFrequency(subscription.getFrequency())
                .setContactId(notifyContact.getId())
                .setSubscriptionId(subscription.getId().toString())
                .setKey("comment-notify---" + newComment.getId().toString() + "---" + notifyContact.getId().toString() );
                ;
        NotificationController.instance().submitNotification(notification);


    }



    private Boolean wasMentionedInComment(Contact contact, Comment comment) {
        return comment.getBodyHtml().contains("data-mentioned-contact=\"" + contact.getId() + "\"");
    }




    @Override
    public void onPreSavePrepare(Comment comment) {
        PegDownProcessor pegDownProcessor = new PegDownProcessor(
                Extensions.FENCED_CODE_BLOCKS |
                        Extensions.AUTOLINKS |
                        Extensions.STRIKETHROUGH
        );

        String html = pegDownProcessor.markdownToHtml(comment.getBodyMarkdown());

        html = Sanitize.basicSanitize(html);
        html = html.replace("</p>", "</p>\n\n");
        html = html.replace("<br>", "<br>\n\n");
        html = replaceAtMentions(comment, html);
        Log.info("New body html: {0}", html);
        // make links no-follow
        comment.setBodyHtml(html);
        // Get rid of dangerous tags in the original markdown, also, just to be safe
        comment.setBodyMarkdown(Sanitize.basicSanitize(comment.getBodyMarkdown()));

        comment.setParentTitle(Sanitize.stripAll(comment.getParentTitle()));
        comment.setAuthorFirstName(Sanitize.stripAll(comment.getAuthorFirstName()));
        comment.setAuthorLastName(Sanitize.stripAll(comment.getAuthorLastName()));
        comment.setAuthorDisplayName(Sanitize.stripAll(comment.getAuthorDisplayName()));
        comment.setAuthorEmail(Sanitize.stripAll(comment.getAuthorEmail()));
        comment.setAuthorWebSite(Sanitize.stripAll(comment.getAuthorWebSite()));
    }

    static Pattern atMentionMatcher = Pattern.compile("(@(\\w+)|@\"([^\"]+)\"|@'([^']+)')");

    public String replaceAtMentions(Comment comment, String body) {
        body = body.replace("&#64;", "@");
        StringBuffer output = new StringBuffer();
        Matcher matcher = atMentionMatcher.matcher(body);
        Map<String, Long> nameToContactId = map();
        Map<Long, String> contactIdToCommentId = map();
        for (Comment cmt: listCommentsForThread(comment.getThreadId())) {
            if (cmt.getId().equals(comment.getId())) {
                continue;
            }
            if (cmt.getAuthorEmail().equals(comment.getAuthorEmail())) {
                continue;
            }
            if (empty(cmt.getContactId()) || cmt.getContactId().equals(comment.getContactId())) {
                continue;
            }
            nameToContactId.put(cmt.getAuthorDisplayName().toLowerCase(), cmt.getContactId());
            contactIdToCommentId.put(cmt.getContactId(), cmt.getId().toString());
        }
        while (matcher.find()) {
            String name = firstTruthy(matcher.group(2), matcher.group(3), matcher.group(4));
            if (nameToContactId.containsKey(name.toLowerCase())) {
                Long contactId = nameToContactId.get(name.toLowerCase());
                String replacement = "<a href=\"#stallion-comment-"
                        + contactIdToCommentId.get(contactId) + "\""
                        + " data-mentioned-contact=\""
                        + contactId + "\">@"
                        + name + "</a>";
                matcher.appendReplacement(output, replacement);
            }

        }
        matcher.appendTail(output);
        return output.toString();
    }

    public List<Comment> listCommentsForThread(Object threadId) {
        return filterByKey("threadId", threadId.toString()).all();
    }

    public CommentSubscriptionInfo getCommentSubscriptionInfo(Comment comment, Contact contact) {
        CommentSubscriptionInfo info = new CommentSubscriptionInfo();

        String replyKey = getOwnerKeyForReplySubscription(comment, contact);
        Subscription replySub = SubscriptionController.instance().forUniqueKey("ownerKey", replyKey);

        String threadKey = getOwnerKeyForThreadSubscription(comment, contact);
        Subscription threadSub = SubscriptionController.instance().forUniqueKey("ownerKey", threadKey);

        if (replySub != null) {
            info.setReplyNotifyFrequency(replySub.getFrequency());
        }
        if (threadSub != null) {
            info.setThreadNotifyFrequency(threadSub.getFrequency());
        }
        return info;
    }

    public String getOwnerKeyForReplySubscription(Comment comment, Contact contact) {
        return "comment-reply|" + comment.getThreadIdSlugified() + "|" + contact.getId();
    }

    public String getOwnerKeyForThreadSubscription(Comment comment, Contact contact) {
        return "comment-thread|" + comment.getThreadIdSlugified() + "|" + contact.getId();
    }

    public CommentSubscriptionInfo updateCommentSubscriptionInfo(CommentSubscriptionInfo info, Comment comment, Contact contact) {


        String replyKey = getOwnerKeyForReplySubscription(comment, contact);
        SubscriptionController.instance().saveContactSubscription(
                contact,
                "Direct replies and mentions on thread  " + comment.getParentTitle(),
                replyKey,
                info.getReplyNotifyFrequency()
        );

        String threadKey = getOwnerKeyForThreadSubscription(comment, contact);
        SubscriptionController.instance().saveContactSubscription(
                contact,
                "Comment thread for " + comment.getParentTitle(),
                threadKey,
                info.getThreadNotifyFrequency()
        );




        return info;
    }

    public static void register() {
        DalRegistration registration = new DalRegistration()
                .setModelClass(Comment.class)
                .setControllerClass(CommentsController.class)
                .setShouldWatch(false)
                .setUseDataFolder(true)
                .setNameSpace("")
                .setWritable(true)
                .setPath("comments")
                .setPersisterClass(JsonFilePersister.class);
        Context.dal().registerDal(registration);
    }



    public Comment hardGet(Long id) {
        Comment cmt = forId(id);
        if (cmt == null) {
            throw new NotFoundException("Comment not found: " + id);
        }
        return cmt;
    }



    @Override
    public Set<String> getKeyFields() {
        return commentKeyFields;
    }

}
