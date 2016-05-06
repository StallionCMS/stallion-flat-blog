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
import io.stallion.dal.file.TextItem;
import io.stallion.dal.filtering.FilterChain;
import io.stallion.dal.filtering.Pager;
import io.stallion.plugins.flatBlog.settings.BlogConfig;
import io.stallion.restfulEndpoints.EndpointResource;
import io.stallion.settings.Settings;
import io.stallion.templating.TemplateRenderer;
import io.stallion.utils.DateUtils;
import io.stallion.utils.GeneralUtils;

import javax.ws.rs.*;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static io.stallion.utils.Literals.empty;

public class Endpoints implements EndpointResource {

    private BlogConfig config;

    public Endpoints(BlogConfig config) {
        this.config = config;
    }

    public Map<String, Object> makeContext() throws Exception {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("blogConfig", config);
        context.put("postsFilter", filterChain());
        if (!empty(config.getTitle())) {
            Context.getResponse().getMeta().setTitle(config.getTitle());
        }
        if (!empty(config.getMetaDescription())) {
            Context.getResponse().getMeta().setDescription(config.getMetaDescription());
        }
        String blogRoot = GeneralUtils.slugify(config.getRootUrl());
        if (empty(blogRoot) || blogRoot.equals("-")) {
            blogRoot = "root";
        } else if (blogRoot.startsWith("-")) {
            blogRoot = blogRoot.substring(1);
        }
        Context.getResponse().getMeta().setBodyCssId("flatBlog-" + blogRoot);
        Context.getResponse().getMeta().getCssClasses().add("st-flatBlog-" + blogRoot);
        return context;
    }

    private BlogPostController postsController() {
        return BlogPost.controller(config.getBucket());
    }

    private FilterChain<BlogPost> filterChain() throws Exception {
        return postsController().filterForConfig(config)
                .filter("published", true);
    }

    @GET
    @Path("/")
    @Produces("text/html")
    public String listHome() throws Exception {
        return listHome(0);
    }

    @GET
    @Path("/page/:page")
    @Produces("text/html")
    public String listHome(@PathParam("page") Integer page) throws Exception {
        Map<String, Object> context = makeContext();
        Pager pager = filterChain()
                .sort("publishDate", "desc")
                .pager(page, config.getPostsPerPage());
        context.put("postsPager", pager);
        if (pager.getItems().size() == 0) {
            Context.getResponse().setStatus(404);
        }
        return TemplateRenderer.instance().renderTemplate(config.getListingTemplate(), context);
    }

    @GET
    @Path("/feed/")
    @Produces("text/xml")
    public String feed() throws Exception {
        return rss();
    }

    @GET
    @Path("/rss.xml")
    @Produces("text/xml")
    public String rss() throws Exception  {
        Map<String, Object> context = makeContext();
        Pager pager = filterChain()
                .sort("publishDate", "desc")
                .pager(0, 20);
        context.put("postsPager", pager);
        context.put("blogUrl", Context.getSettings().getSiteUrl() + config.getRootUrl());
        ZonedDateTime buildTime = ZonedDateTime.of(2015, 1, 1, 12, 0, 0, 0, GeneralUtils.UTC);
        if (pager.getItems().size() > 0) {
            TextItem item = (TextItem) pager.getItems().get(0);
            buildTime = item.getPublishDate().plusMinutes(1);
        }
        context.put("generator", Settings.instance().getMetaGenerator());
        context.put("lastBuildDate", DateUtils.formatLocalDateFromZonedDate(buildTime, "EEE, dd MMM yyyy HH:mm:ss Z"));
        return TemplateRenderer.instance().renderTemplate(
                getClass().getResource("/templates/rss.jinja").toString(),
                context);
    }



    @GET
    @Path("/archives/:year/:month")
    public String listByDate(@PathParam("year") String year, @PathParam("month") String month) throws Exception {
        Map<String, Object> context = makeContext();
        Pager pager = filterChain()
                .filter("year", year)
                .filter("month", month)
                .sort("publishDate", "desc")
                .pager(0, 5000);
        context.put("postsPager", pager);
        return TemplateRenderer.instance().renderTemplate(config.getListingTemplate(), context);

    }

    @GET
    @Path("/by-tag/:tag")
    @Produces("text/html")
    public String listByTag(@PathParam("tag") String tag) throws Exception {
        Map<String, Object> context = makeContext();
        Pager pager = filterChain()
                .filter("tags", tag, "in")
                .sort("publishDate", "desc")
                .pager(0, 5000);
        context.put("postsPager", pager);
        return TemplateRenderer.instance().renderTemplate(config.getListingTemplate(), context);
    }
}
