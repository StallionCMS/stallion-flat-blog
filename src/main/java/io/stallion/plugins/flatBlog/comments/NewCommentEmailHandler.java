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

package io.stallion.plugins.flatBlog.comments;

import io.stallion.asyncTasks.AsyncCoordinator;
import io.stallion.asyncTasks.AsyncTaskHandlerBase;
import io.stallion.dataAccess.Model;
import io.stallion.plugins.flatBlog.FlatBlogSettings;
import io.stallion.services.Log;
import io.stallion.settings.Settings;
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
        Comment comment = CommentsController.instance().forIdOrNotFound(commentId);
        List<String> moderatorEmails = FlatBlogSettings.getInstance().getModeratorEmails();
        if (empty(moderatorEmails)) {
            moderatorEmails = FlatBlogSettings.getInstance().getNotifyEmails();
        }
        if (empty(moderatorEmails)) {
            moderatorEmails = Settings.instance().getEmail().getAdminEmails();
        }

        Log.info("Mail comment to moderators commentId={0} moderators={1}", commentId, moderatorEmails);
        for(String email: moderatorEmails) {
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
