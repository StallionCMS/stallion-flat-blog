package io.stallion.plugins.flatBlog.comments;

import io.stallion.plugins.flatBlog.contacts.Notification;
import io.stallion.plugins.flatBlog.contacts.NotificationCallbackHandlerInterface;
import io.stallion.plugins.flatBlog.contacts.NotificationCallbackResult;
import io.stallion.templating.TemplateRenderer;


import java.net.URL;
import java.util.Map;

import static io.stallion.utils.Literals.*;


public class CommentNotificationCallback implements NotificationCallbackHandlerInterface {

    private Long commentId = 0L;


    public NotificationCallbackResult handle(Notification notification) {
        NotificationCallbackResult result = new NotificationCallbackResult();

        result.setThing("comment");
        result.setThingPlural("comments");

        Comment comment = CommentsController.instance().forId(commentId);

        if (comment == null || !comment.getIsApproved()) {
            return null;
        }


        URL url = getClass().getClassLoader().getResource("templates/comment-notify-email-partial.jinja");
        Map<String, Object> ctx = map();
        ctx.put("comment", comment);

        String html = TemplateRenderer.instance().renderTemplate(url.toString(), ctx);
        result.setEmailBody(html);

        return result;
    }

    public Long getCommentId() {
        return commentId;
    }

    public CommentNotificationCallback setCommentId(Long commentId) {
        this.commentId = commentId;
        return this;
    }
}
