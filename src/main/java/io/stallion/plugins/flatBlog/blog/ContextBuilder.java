package io.stallion.plugins.flatBlog.blog;

import io.stallion.Context;
import io.stallion.plugins.flatBlog.settings.BlogConfig;
import io.stallion.plugins.flatBlog.FlatBlogSettings;
import io.stallion.settings.Settings;
import io.stallion.templating.TemplateContextHookHandler;
import io.stallion.utils.rss.RssLink;


import java.util.Map;

import static io.stallion.utils.Literals.*;


public class ContextBuilder extends TemplateContextHookHandler {


    public void handle(Map<String, Object> context) {
        for(BlogConfig blog: FlatBlogSettings.getInstance().getBlogs()) {
            String rootUrl = Context.getSettings().getSiteUrl() + blog.getRootUrl();
            if (!rootUrl.endsWith("/")) {
                rootUrl = rootUrl + "/";
            }
            String url = rootUrl + "rss.xml";
            String title = or(blog.getTitle(), Settings.instance().getSiteName());
            if (empty(title)) {
                title = Settings.instance().getSiteName();
            }
            Context.getResponse().getMeta().getRssLinks().add(
                    new RssLink().setTitle(title + " Recent Posts").setLink(url)
            );
        }

    }
}
