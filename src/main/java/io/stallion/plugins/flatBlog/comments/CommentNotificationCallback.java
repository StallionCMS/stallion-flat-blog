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
