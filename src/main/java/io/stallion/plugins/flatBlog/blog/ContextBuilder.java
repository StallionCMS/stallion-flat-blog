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

package io.stallion.plugins.flatBlog.blog;

import io.stallion.Context;
import io.stallion.plugins.flatBlog.settings.BlogConfig;
import io.stallion.plugins.flatBlog.FlatBlogSettings;
import io.stallion.settings.Settings;
import io.stallion.templating.TemplateContextHookHandler;
import io.stallion.utils.rss.RssLink;


import java.util.Map;

import static io.stallion.utils.Literals.*;


public class ContextBuilder extends TemplateContextHookHandler {


    public void handle(Map<String, Object> context) {
        for(BlogConfig blog: FlatBlogSettings.getInstance().getBlogs()) {
            String rootUrl = Context.getSettings().getSiteUrl() + blog.getRootUrl();
            if (!rootUrl.endsWith("/")) {
                rootUrl = rootUrl + "/";
            }
            String url = rootUrl + "rss.xml";
            String title = or(blog.getTitle(), Settings.instance().getSiteName());
            if (empty(title)) {
                title = Settings.instance().getSiteName();
            }
            Context.getResponse().getMeta().getRssLinks().add(
                    new RssLink().setTitle(title + " Recent Posts").setLink(url)
            );
        }

    }
}
