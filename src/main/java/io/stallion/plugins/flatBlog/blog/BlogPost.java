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

package io.stallion.plugins.flatBlog.blog;

import io.stallion.Context;
import io.stallion.dataAccess.file.TextItem;
import io.stallion.services.Log;

import java.time.format.DateTimeFormatter;

public class BlogPost extends TextItem {

    private String blogId = "";

    public static BlogPostController controller(String bucket) {
        return (BlogPostController)Context.dal().get(bucket);
    }

    public String getRssPubDate() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z");
        String dt = getPublishDate().format(format);
        Log.info("PubDate: {0} String: {1}", getPublishDate(), dt);
        return dt;
    }


    public String getBlogId() {
        return blogId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }
}
