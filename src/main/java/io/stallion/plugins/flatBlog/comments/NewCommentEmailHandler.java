package io.stallion.plugins.flatBlog.comments;

import io.stallion.asyncTasks.AsyncCoordinator;
import io.stallion.asyncTasks.AsyncTaskHandlerBase;
import io.stallion.dal.base.Model;
import io.stallion.plugins.flatBlog.FlatBlogSettings;
import io.stallion.services.Log;
import io.stallion.testing.Stubbing;
import io.stallion.users.User;
import io.stallion.users.UserController;

import java.util.List;

import static io.stallion.utils.Literals.*;


public class NewCommentEmailHandler extends AsyncTaskHandlerBase {
    private Long commentId;

    public static void enqueue(Comment comment) {
        try {
            Stubbing.checkExecuteStub(comment);
        } catch (Stubbing.StubbedOut stubbedOut) {
            return;
        }
        NewCommentEmailHandler handler = new NewCommentEmailHandler().setCommentId(comment.getId());
        AsyncCoordinator.instance().enqueue(handler, "new-comment-emails-" + comment.getId(), 0);
    }


    public void process() {
        Comment comment = CommentsController.instance().hardGet(commentId);
        List<User> moderators = list();
        Log.info("Mail comment to moderators commentId={0} moderators={1}", commentId, FlatBlogSettings.getInstance().getModeratorEmails());
        for(String email: FlatBlogSettings.getInstance().getModeratorEmails()) {
            Model m = null;
            if (UserController.instance() != null) {
                m = UserController.instance().forUniqueKey("email", email);
            }
            User user;
            if (m == null) {
                user = new User().setEmail(email);
            } else {
                user = (User)m;
            }
            AdminNotifyCommentEmailer emailer = new AdminNotifyCommentEmailer(user, comment);
            Log.info("Send moderation email. commentId={0} moderator={0}", comment.getId(), user.getEmail());
            emailer.sendEmail();
        }
    }

    public Object getCommentId() {
        return commentId;
    }

    public NewCommentEmailHandler setCommentId(Long commentId) {
        this.commentId = commentId;
        return this;
    }
}
