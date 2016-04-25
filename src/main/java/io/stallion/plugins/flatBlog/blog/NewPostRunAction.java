package io.stallion.plugins.flatBlog.blog;

import io.stallion.boot.AppContextLoader;
import io.stallion.boot.CommandOptionsBase;
import io.stallion.boot.StallionRunAction;
import io.stallion.dal.DalRegistry;
import io.stallion.dal.file.TextFilePersister;
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
                .put("id", DalRegistry.instance().getTickets().nextId())
                .put("slug", slug)
                .put("title", title)
                .put("siteUrl", Settings.instance().getSiteUrl())
                .put("previewKey", GeneralUtils.randomToken(8))
                .render();
        String blogFolderPath = ((TextFilePersister)DalRegistry.instance().get(blog.getFolder()).getPersister()).getBucketFolderPath();
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
