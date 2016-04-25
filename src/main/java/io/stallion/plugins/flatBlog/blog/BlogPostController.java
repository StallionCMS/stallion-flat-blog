package io.stallion.plugins.flatBlog.blog;

import io.stallion.dal.file.TextItem;
import io.stallion.dal.file.TextItemController;
import io.stallion.dal.filtering.FilterChain;
import io.stallion.plugins.flatBlog.settings.BlogConfig;

public class BlogPostController<T extends TextItem> extends TextItemController {

    public FilterChain<BlogPost> filterForConfig(BlogConfig config) throws Exception {
        return this.filterChain();
    }
}
