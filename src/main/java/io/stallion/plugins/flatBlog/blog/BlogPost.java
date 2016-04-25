package io.stallion.plugins.flatBlog.blog;

import io.stallion.Context;
import io.stallion.dal.file.TextItem;
import io.stallion.services.Log;

import java.time.format.DateTimeFormatter;

public class BlogPost extends TextItem {

    private String blogId = "";

    public static BlogPostController controller(String bucket) {
        return (BlogPostController)Context.dal().get(bucket);
    }

    public String getRssPubDate() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z");
        String dt = getPublishDate().format(format);
        Log.info("PubDate: {0} String: {1}", getPublishDate(), dt);
        return dt;
    }


    public String getBlogId() {
        return blogId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }
}
