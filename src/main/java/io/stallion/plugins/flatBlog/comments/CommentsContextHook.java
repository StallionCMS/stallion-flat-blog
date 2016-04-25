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

import io.stallion.settings.Settings;
import io.stallion.templating.TemplateContextHookHandler;
import io.stallion.utils.rss.RssLink;

import java.util.Map;

public class CommentsContextHook extends TemplateContextHookHandler {


    public void handle(Map<String, Object> obj) {
        obj.put("commenting", new CommentingContext());
        String link = Settings.instance().getSiteUrl() + "/_stx/comments/rss.xml";
        String name = "Comments on " + Settings.instance().getSiteName();
        Context.response().getMeta().getRssLinks().add(
                new RssLink().setLink(link).setTitle(name)
        );
    }
}
