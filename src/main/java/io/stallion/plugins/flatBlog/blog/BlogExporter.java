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

import io.stallion.plugins.flatBlog.FlatBlogSettings;
import io.stallion.plugins.flatBlog.settings.BlogConfig;
import io.stallion.tools.Exporter;
import io.stallion.utils.DateUtils;

import java.util.List;

import static io.stallion.utils.Literals.*;
import static io.stallion.Context.*;


public class BlogExporter implements Exporter {
    @Override
    public List<String> listUrls() {
        List<String> urlPaths = list();
        for (BlogConfig conf: FlatBlogSettings.getInstance().getBlogs()) {
            String root = conf.getRootUrl();
            if (root.equals("/")) {
                root = "";
            }
            urlPaths.add(or(root, "/"));
            urlPaths.add(root + "/rss.xml");
            urlPaths.add(root + "/feed");
            BlogPostController controller = BlogPost.controller(conf.getBucket());
            int count = controller.filter("published", true).count();
            for (BlogPost post: controller.filter("published", true).all()) {
                urlPaths.add(root + "/archives/" + DateUtils.formatLocalDateFromZonedDate(post.getPublishDate(), "yyyy/MM/"));
                for (String tag: post.getTags()) {
                    urlPaths.add("/by-tag/" + tag + "/");
                }
            }
            int pageCount = (count / conf.getPostsPerPage()) + 1;
            for (int i = 2; i <= count; i++) {
                urlPaths.add("/page/" + i + "/");
            }

        }
        return urlPaths;
    }
}
