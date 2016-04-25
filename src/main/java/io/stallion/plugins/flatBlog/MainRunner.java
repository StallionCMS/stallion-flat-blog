package io.stallion.plugins.flatBlog;

public class MainRunner
{
    public static void main( String[] args ) throws Exception {
        io.stallion.boot.MainRunner.mainWithPlugins(args,
                new FlatBlogPlugin()
        );
    }
}
