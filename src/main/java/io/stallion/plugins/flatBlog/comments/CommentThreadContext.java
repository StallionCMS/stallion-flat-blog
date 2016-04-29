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

import java.util.List;
import java.util.Map;

import static io.stallion.utils.Literals.*;
import static io.stallion.Context.*;


public class CommentThreadContext {
    private Long threadId = 0L;
    private String parentTitle = "";
    private String parentPermalink = "";
    private List<CommentWrapper> comments = list();
    private Map<String, Object> commentById = map();
    private Map<String, Object> riotTagByCommentId = map();


    public Long getThreadId() {
        return threadId;
    }

    public CommentThreadContext setThreadId(Long threadId) {
        this.threadId = threadId;
        return this;
    }

    public String getParentTitle() {
        return parentTitle;
    }

    public CommentThreadContext setParentTitle(String parentTitle) {
        this.parentTitle = parentTitle;
        return this;
    }

    public String getParentPermalink() {
        return parentPermalink;
    }

    public CommentThreadContext setParentPermalink(String parentPermalink) {
        this.parentPermalink = parentPermalink;
        return this;
    }

    public List<CommentWrapper> getComments() {
        return comments;
    }

    public CommentThreadContext setComments(List<CommentWrapper> comments) {
        this.comments = comments;
        return this;
    }

    public Map<String, Object> getCommentById() {
        return commentById;
    }

    public CommentThreadContext setCommentById(Map<String, Object> commentById) {
        this.commentById = commentById;
        return this;
    }

    public Map<String, Object> getRiotTagByCommentId() {
        return riotTagByCommentId;
    }

    public CommentThreadContext setRiotTagByCommentId(Map<String, Object> riotTagByCommentId) {
        this.riotTagByCommentId = riotTagByCommentId;
        return this;
    }
}
