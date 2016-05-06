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

package io.stallion.plugins.flatBlog.settings;

import io.stallion.exceptions.CommandException;
import io.stallion.templating.JinjaTemplating;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static io.stallion.utils.Literals.*;
import static io.stallion.Context.*;


public class FlatBlogSettingsBuilder {
    private String rootUrl;
    private boolean commentModeration;



    public void build(String targetFolder, JinjaTemplating templating) {
        File file = new File(targetFolder + "/conf/flatBlog.toml");
        if (file.exists()) {
            throw new CommandException("A conf/flatBlog.toml file already exists. Cannot initialize the new blog.");
        }
        Map map = map(val("builder", this));
        try {
            String template = IOUtils.toString(getClass().getResource("/templates/wizard/flatBlog.toml.jinja").toURI());
            String content = templating.renderTemplate(template, map);
            FileUtils.write(file, content, "UTF-8");
        } catch (IOException|URISyntaxException e) {
            throw new RuntimeException(e);
        }


    }

    public String getRootUrl() {
        return rootUrl;
    }

    public FlatBlogSettingsBuilder setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
        return this;
    }

    public boolean isCommentModeration() {
        return commentModeration;
    }

    public FlatBlogSettingsBuilder setCommentModeration(boolean commentModeration) {
        this.commentModeration = commentModeration;
        return this;
    }
}
