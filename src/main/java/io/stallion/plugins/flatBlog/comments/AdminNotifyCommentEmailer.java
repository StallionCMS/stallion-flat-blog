package io.stallion.plugins.flatBlog.comments;

import io.stallion.email.ContactableEmailer;
import io.stallion.users.User;


public class AdminNotifyCommentEmailer extends ContactableEmailer {
    private Comment comment;

    public AdminNotifyCommentEmailer(User user, Comment comment) {
        super(user);
        this.comment = comment;
        put("comment", comment);
        put("rejectPermalink", comment.permalinkWithQuery("stLogin=true&stModerateAction=reject&stModerateId=" + comment.getId()));
        put("approvePermalink", comment.permalinkWithQuery("stLogin=true&stModerateAction=approve&stModerateId=" + comment.getId()));

    }

    @Override
    public boolean isTransactional() {
        return true;
    }

    @Override
    public String getTemplate() {
        return getClass().getResource("/templates/new-comment-admin-email.jinja").toString();
    }

    @Override
    public String getSubject() {
        return "New comment on \"{comment.parentTitle}\" by {comment.authorDisplayName}" ;
    }

}
