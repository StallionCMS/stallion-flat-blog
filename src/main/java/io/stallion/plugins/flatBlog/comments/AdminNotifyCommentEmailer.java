/*
 * Stallion Flat-file Blog: A simple blog-engine
 *
 * Copyright (C) 2015 - 2016 Patrick Fitzsimmons.
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
