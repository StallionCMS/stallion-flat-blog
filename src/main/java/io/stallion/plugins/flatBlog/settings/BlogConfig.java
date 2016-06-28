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

package io.stallion.plugins.flatBlog.settings;

import org.apache.commons.lang3.StringUtils;

public class BlogConfig {
    private String rootUrl = "/";
    private String folder;
    private String listingTemplate = "blog.post.jinja";
    private String postTemplate = "blog.listing.jinja";
    private String bucket;
    private String title = "";
    private String metaDescription = "";
    private int postsPerPage = 10;


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




    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
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

    public String getListingTemplate() {
        return listingTemplate;
    }

    public BlogConfig setListingTemplate(String listingTemplate) {
        this.listingTemplate = listingTemplate;
        return this;
    }

    public String getPostTemplate() {
        return postTemplate;
    }

    public BlogConfig setPostTemplate(String postTemplate) {
        this.postTemplate = postTemplate;
        return this;
    }
}
