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

package io.stallion.plugins.flatBlog;

import io.stallion.Context;
import io.stallion.assets.BundleFile;
import io.stallion.assets.DefinedBundle;
import io.stallion.boot.StallionRunAction;
import io.stallion.dal.base.DalRegistration;
import io.stallion.dal.file.TextFilePersister;
import io.stallion.hooks.HookRegistry;
import io.stallion.plugins.StallionJavaPlugin;
import io.stallion.plugins.flatBlog.blog.*;
import io.stallion.plugins.flatBlog.comments.CommentsEndpoints;
import io.stallion.plugins.flatBlog.comments.CommentsContextHook;
import io.stallion.plugins.flatBlog.comments.CommentsController;
import io.stallion.plugins.flatBlog.comments.CommentsTag;
import io.stallion.plugins.flatBlog.contacts.*;
import io.stallion.plugins.flatBlog.settings.BlogConfig;
import io.stallion.restfulEndpoints.EndpointsRegistry;
import io.stallion.restfulEndpoints.JavaRestEndpoint;
import io.stallion.restfulEndpoints.ResourceToEndpoints;
import io.stallion.sitemaps.SiteMapController;
import io.stallion.sitemaps.SiteMapItem;
import io.stallion.services.Log;
import io.stallion.settings.Settings;
import io.stallion.settings.TargetFolder;
import io.stallion.templating.TemplateRenderer;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static io.stallion.utils.Literals.list;

public class FlatBlogPlugin extends StallionJavaPlugin {

    @Override
    public String getPluginName() {
        return "flatBlog";
    }


    @Override
    public List<? extends StallionRunAction> getActions() {
        return list(new NewPostRunAction(), new NewFlatBlogSiteAction());
    }

    @Override
    public void boot() throws Exception {
        bootContacts();
        bootComments();
        bootBlog();
    }

    public void bootBlog() throws Exception {

        FlatBlogSettings.getInstance().validate();
        JavaRestEndpoint ep = new JavaRestEndpoint();
        HookRegistry.instance().register(new ContextBuilder());

        for (BlogConfig config: FlatBlogSettings.getInstance().getBlogs()) {
            Endpoints resource = new Endpoints(config);
            String rootUrl = config.getRootUrl();
            if (rootUrl.endsWith("/")) {
                rootUrl = rootUrl.substring(0, rootUrl.length() - 1);
            }
            ResourceToEndpoints converter = new ResourceToEndpoints(rootUrl);
            List<JavaRestEndpoint> endpointList = converter.convert(resource);
            JavaRestEndpoint[] endpointArray = endpointList.toArray(new JavaRestEndpoint[0]);
            EndpointsRegistry.instance().addEndpoints(endpointArray);

            SiteMapController.instance().addItem(new SiteMapItem().setPermalink(Settings.instance().getSiteUrl() + rootUrl));
            config.setBucket(config.getFolder());
            DalRegistration registration = new DalRegistration()
                    .setModelClass(BlogPost.class)
                    .setControllerClass(BlogPostController.class)
                    .setPath(config.getFolder())
                    .setShouldWatch(true)
                    .setUseDataFolder(false)
                    .setPersisterClass(TextFilePersister.class)
                    .setTemplatePath(config.getPostTemplate())
                    .setWritable(false);
            Context.dal().registerDal(registration);

        }

    }

    public void bootContacts() throws Exception {
        ContactsController.register();
        FormSubmissionController.register();
        SubscriptionController.register();
        NotificationController.register();
        EndpointsRegistry.instance().addResource("/_stx/flatBlog", new ContactsEndpoints());
        HookRegistry.instance().register(new TemplateContextHydrater());
        TemplateRenderer.instance().getJinjaTemplating().registerTag(new FormTag());
        DefinedBundle.getAlwaysFooterJavascripts().add("flatBlog", "contacts-always.js");
    }


    public void bootComments() throws Exception {
        Log.info("Register comments resources");
        EndpointsRegistry.instance().addResource("/_stx/flatBlog", new CommentsEndpoints());
        Log.info("Register comments pojo folder");
        TargetFolder tf = new TargetFolder().setPath("comments").setWritable(true);
        CommentsController.register();
        Log.info("Register CommentsContextHook");
        HookRegistry.instance().register(new CommentsContextHook());
        Log.info("Adding public.css to bundle");
        DefinedBundle.getAlwaysHeadStylesheets().add(
                new BundleFile().setPluginName("flatBlog").setLiveUrl("comments-public.css"));

        DefinedBundle.register(
                new DefinedBundle("flatBlog:public.js", ".js",

                        new BundleFile().setPluginName("flatBlog").setLiveUrl("comments-public.js"),
                        new BundleFile().setPluginName("flatBlog").setLiveUrl("comments-public-riot.tag.js").setProcessor("riot")
                )
        );
        TemplateRenderer.instance().getJinjaTemplating().registerTag(new CommentsTag());

        DefinedBundle.register(new DefinedBundle(
                "commentsAdminStylesheets", ".css",
                new BundleFile().setPluginName("flatBlog").setLiveUrl("comments-manage.css")
        ));
        DefinedBundle.register(new DefinedBundle(
                "commentsAdminJavascripts", ".js",
                new BundleFile().setPluginName("flatBlog").setLiveUrl("comments-manage.js"),
                new BundleFile().setPluginName("flatBlog").setLiveUrl("comments-manage-riot.tag.js").setProcessor("riot")
        ));

        Log.info("Comments boot complete");


    }
}

