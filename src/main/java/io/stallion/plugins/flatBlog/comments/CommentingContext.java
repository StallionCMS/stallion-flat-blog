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

import io.stallion.Context;
import io.stallion.assets.AssetsController;
import io.stallion.plugins.flatBlog.FlatBlogSettings;
import io.stallion.services.Log;
import io.stallion.templating.TemplateRenderer;
import io.stallion.utils.GeneralUtils;

import java.net.URL;
import java.util.HashMap;

import static io.stallion.utils.Literals.empty;

public class CommentingContext {
    public String renderForm(Object threadId, String parentPermalink, String parentTitle) throws Exception {
        requireAssets();

        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put("parentTitle", parentTitle);
        context.put("parentPermalink", parentPermalink);
        context.put("commentThreadId", threadId);
        context.put("commentThreadIdSlug", GeneralUtils.slugify(threadId.toString()));
        context.put("reCaptchaSiteKey", FlatBlogSettings.getInstance().getReCaptchaSiteKey());
        String html = cascadingRenderTemplate("comment-entry-form", context);
        if (!empty(FlatBlogSettings.getInstance().getReCaptchaSiteKey())) {
            Context.getResponse().getPageFooterLiterals().addString("<script src=\"https://www.google.com/recaptcha/api.js?\" async defer></script>");
        }
        return html;
    }

    public int commentCountForThread(Object threadId) {
        return 0;
    }

    public String renderComments(Comparable threadId) throws Exception {
        requireAssets();

        for(Comment comment: CommentsController.instance().all()) {
            Log.finer("Existing comment: id={0} threadId={1} deleted={2}", comment.getId(), comment.getThreadId(), comment.getDeleted());
        }
        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put("commentThreadId", threadId);
        context.put("threadComments", CommentsController.instance().filterByKey("threadId", threadId).filter("deleted", false).sort("createdTicks", "asc"));

        String html = cascadingRenderTemplate("comments-for-thread", context);
        return html;
    }

    private void requireAssets() {
        Log.info("add to per request footer js");
        String commentsTagUrl = AssetsController.instance().resource("comment-riot-tag.tag", "flatBlog");
        Context.getResponse().getPageFooterLiterals().addString("<script src=\"" + commentsTagUrl + "\" type=\"riot/tag\"></script>");
        Context.getResponse().getPageFooterLiterals().addDefinedBundle("flatBlog:public.js");

    }

    public String cascadingRenderTemplate(String templatePrefix, HashMap<String, Object> context) throws Exception {
        URL url = getClass().getResource("/templates/" + templatePrefix + ".jinja");
        return  TemplateRenderer.instance().renderTemplate(url, context);
    }

}
