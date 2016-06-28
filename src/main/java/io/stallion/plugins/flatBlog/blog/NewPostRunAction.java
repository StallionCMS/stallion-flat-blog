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

import io.stallion.boot.AppContextLoader;
import io.stallion.boot.CommandOptionsBase;
import io.stallion.boot.StallionRunAction;
import io.stallion.dataAccess.DataAccessRegistry;
import io.stallion.dataAccess.file.TextFilePersister;
import io.stallion.plugins.flatBlog.FlatBlogSettings;
import io.stallion.plugins.flatBlog.settings.BlogConfig;
import io.stallion.settings.Settings;
import io.stallion.utils.DateUtils;
import io.stallion.utils.GeneralUtils;
import io.stallion.utils.Prompter;
import io.stallion.utils.SimpleTemplate;
import org.apache.commons.io.FileUtils;

import java.io.File;


public class NewPostRunAction implements StallionRunAction<CommandOptionsBase> {

    public String getActionName() {
        return "new-blog-post";
    }

    public String getHelp() {
        return "Interactive command-line that will create the scaffolding of a markdown blog post.";
    }

    public void loadApp(CommandOptionsBase options) {
        AppContextLoader.loadCompletely(options);
    }

    public void execute(CommandOptionsBase options) throws Exception {

        BlogConfig blog = FlatBlogSettings.getInstance().getBlogs().get(0);
        String title = Prompter.prompt("Choose a post title: ");
        String slug = blog.getRootUrl();
        if (!slug.endsWith("/")) {
            slug += "/";
        }
        slug += GeneralUtils.slugify(title);

        String postContent = new SimpleTemplate(postTemplate)
                .put("publishDate", "2099-01-01 11:15:00 America/New_York")
                .put("id", DataAccessRegistry.instance().getTickets().nextId())
                .put("slug", slug)
                .put("title", title)
                .put("siteUrl", Settings.instance().getSiteUrl())
                .put("previewKey", GeneralUtils.randomToken(8))
                .render();
        String blogFolderPath = ((TextFilePersister)DataAccessRegistry.instance().get(blog.getFolder()).getPersister()).getBucketFolderPath();
        String fileName =  blogFolderPath + "/" + DateUtils.formatNow("yyyy-MM-dd") + "-" + GeneralUtils.slugify(title) + ".txt";
        File file = new File(fileName);
        FileUtils.write(file, postContent, "UTF-8");
        System.out.println("Successfully wrote new blog post to file: " + file.getAbsolutePath());
    }

    private static final String postTemplate = "{ title }\n=====================================\n" +
            "publishDate: { publishDate }\n" +
            "slug: { slug }\n" +
            "id: { id }\n" +
            "previewKey: {previewKey}\n" +
            "metaDescription: \n" +
            "\n" +
            "Hello, I am a brand new blog post. I can be previewed at {siteUrl}{slug}?stPreview={previewKey}\n\n" +
            "When you are ready to publish me, change the publishDate to a near future or past date.";
}
