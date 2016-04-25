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

import org.apache.commons.lang3.StringUtils;

public class BlogConfig {
    private String rootUrl = "/";
    private String folder;
    private String table;
    private String id = "";
    private String templateFolder;
    private String bucket;
    private Boolean useDatabase = null;
    private String title = "";
    private String metaDescription = "";
    private int postsPerPage = 10;

    public String getTemplatePath(String type) {
        String path = templateFolder + "/" + type + ".jinja";
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }

    public String getRootUrl() {
        return rootUrl;
    }

    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getId() {
        if (StringUtils.isEmpty(id)) {
            if (StringUtils.isEmpty(folder)) {
                return table;
            } else {
                return folder;
            }
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemplateFolder() {
        return templateFolder;
    }

    public void setTemplateFolder(String templateFolder) {
        this.templateFolder = templateFolder;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public Boolean getUseDatabase() {
        return useDatabase;
    }

    public void setUseDatabase(Boolean useDatabase) {
        this.useDatabase = useDatabase;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public int getPostsPerPage() {
        return postsPerPage;
    }

    public void setPostsPerPage(int postsPerPage) {
        this.postsPerPage = postsPerPage;
    }
}
