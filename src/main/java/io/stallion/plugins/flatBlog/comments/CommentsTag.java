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

import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.lib.tag.Tag;
import com.hubspot.jinjava.tree.TagNode;
import io.stallion.Context;
import io.stallion.assets.AssetsController;
import io.stallion.dal.base.Displayable;
import io.stallion.dal.file.TextItem;
import io.stallion.plugins.flatBlog.FlatBlogSettings;
import io.stallion.services.Log;
import io.stallion.templating.TemplateRenderer;

import java.util.List;
import java.util.Map;

import static io.stallion.utils.Literals.*;
import static io.stallion.Context.*;


public class CommentsTag implements Tag {
    public String interpret(TagNode tagNode, JinjavaInterpreter jinjavaInterpreter) {
        //jinjavaInterpreter.getContext().put()
        try {



            Context.getResponse().getPageFooterLiterals().addDefinedBundle("flatBlog:public.js");


            jinjavaInterpreter.enterScope();
            Map<String, Object> context = jinjavaInterpreter.getContext();
            TextItem post = (TextItem) context.get("post");
            context.put("commentThreadId", post.getId());

            CommentThreadContext commentsContext = new CommentThreadContext();
            commentsContext
                    .setThreadId(post.getId())
                    .setParentPermalink(post.getPermalink())
                    .setParentTitle(post.getTitle());

            List<CommentWrapper> comments = list();
            for (Comment comment : CommentsController.instance()
                    .filterByKey("threadId", post.getId())
                    .filter("deleted", false)
                    .sort("createdTicks", "asc")
                    .all()) {
                if (comment.isApproved() || comment.isAdminable() || comment.isEditable()) {
                    commentsContext.getComments().add(comment.toWrapper());
                }
            }

            context.put("commentsContext", commentsContext);
            context.put("reCaptchaSiteKey", FlatBlogSettings.getInstance().getReCaptchaSiteKey());

            return TemplateRenderer.instance().renderTemplate("flatBlog:comments-section-for-post.jinja", context);
        } catch (RuntimeException e) {
            Log.exception(e, "Error rendering comment thread tag.");
            throw e;
        } finally {
            jinjavaInterpreter.leaveScope();
        }

    }

    public String getEndTagName() {
        return null;
    }

    public String getName() {
        return "comments_section";
    }
}
