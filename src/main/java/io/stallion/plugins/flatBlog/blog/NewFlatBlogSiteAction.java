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

import io.stallion.boot.CommandOptionsBase;
import io.stallion.boot.NewProjectBuilder;
import io.stallion.boot.StallionRunAction;
import io.stallion.plugins.flatBlog.settings.FlatBlogSettingsBuilder;
import io.stallion.utils.Prompter;
import org.apache.commons.io.FileUtils;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static io.stallion.utils.Literals.*;
import static io.stallion.Context.*;


public class NewFlatBlogSiteAction extends NewProjectBuilder {
    FlatBlogSettingsBuilder builder = null;

    public String getActionName() {
        return "new-flat-blog-site";
    }

    public String getHelp() {
        return "Create the configuration and scaffolding for a new, flat-blog website.";
    }

    public void loadApp(CommandOptionsBase options) {

    }

    public CommandOptionsBase newCommandOptions() {
        return new CommandOptionsBase();
    }

    public void execute(CommandOptionsBase options) throws Exception {
        init(options);
        makePages();
        makeStandardConf();
        makeTemplates();
        makeAssets();
        makePages();
        makeBlogConf();

        makeBlogTemplates();
        if (!builder.getRootUrl().equals("/")) {
            String sidebarLi = "" +
                    "          <li class=\"nav-item\">\n" +
                    "            <a class=\"pure-button\" href=\"" + builder.getRootUrl() + "\">Blog</a>\n" +
                    "          </li>\n" +
                    "          <li class=\"nav-item\">\n" +
                    "            <a class=\"pure-button\" href=\"/contact-us\">Contact Us</a>\n" +
                    "          </li>";
            String footerLi = " <li class=\"pure-menu-item\"><a href=\"" + builder.getRootUrl() + "\" class=\"pure-menu-link\">Blog</a></li>\n" +
                    "<li class=\"pure-menu-item\"><a href=\"/contact-us\" class=\"pure-menu-link\">Contact Us</a></li>\n";
            replaceString("templates/base.jinja", "<!--extra-sidebar-links-->", sidebarLi);
            replaceString("templates/base.jinja", "<!--extra-footer-links-->", footerLi);
        }
        makeBlogPosts();


        boolean shouldMakeUser = new Prompter("Do you want to create an admin user right now? This is needed to use some of the internal tools from the web. You can do this later by running >stallion user-add. (y/n)? ").yesNo();
        if (shouldMakeUser) {
            makeUser();
        }
        System.out.printf("\n\nYour site is now complete! You can test it out by running >bin/stallion serve\n\n");
    }

    protected void makeBlogTemplates() throws IOException {
        copyFile("/templates/wizard/blog.listing.jinja", "/templates/blog.listing.jinja");
        copyFile("/templates/wizard/blog.post.jinja", "/templates/blog.post.jinja");
        copyFile("/templates/wizard/contact-page.jinja", "/templates/contact-page.jinja");
    }



    protected void makeBlogConf() {
        builder = new FlatBlogSettingsBuilder();
        builder.setRootUrl(or(Prompter.prompt("Choose a root URL for your main blog page: (default is '/') "), "/"));
        builder.setCommentModeration(new Prompter("Enable comment moderation? ").yesNo());
        builder.build(getTargetFolder(), getTemplating());
    }

    protected void makeBlogPosts() throws IOException {
        copyFile("/templates/wizard/contact-us.txt", "/pages/contact-us.txt");
        copyFile("/templates/wizard/first-post.txt", "/posts/first-post.txt");
        copyFile("/templates/wizard/second-post.txt", "/posts/second-post.txt");
        copyFile("/templates/wizard/a-draft-post.txt", "/posts/a-draft-post.txt");


    }
}
