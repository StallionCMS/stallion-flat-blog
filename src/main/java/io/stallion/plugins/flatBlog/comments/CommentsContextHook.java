package io.stallion.plugins.flatBlog.comments;


import io.stallion.Context;

import io.stallion.settings.Settings;
import io.stallion.templating.TemplateContextHookHandler;
import io.stallion.utils.rss.RssLink;

import java.util.Map;

public class CommentsContextHook extends TemplateContextHookHandler {


    public void handle(Map<String, Object> obj) {
        obj.put("commenting", new CommentingContext());
        String link = Settings.instance().getSiteUrl() + "/_stx/comments/rss.xml";
        String name = "Comments on " + Settings.instance().getSiteName();
        Context.response().getMeta().getRssLinks().add(
                new RssLink().setLink(link).setTitle(name)
        );
    }
}
