package io.stallion.plugins.flatBlog.contacts;


import io.stallion.boot.AppContextLoader;

import io.stallion.boot.ServeCommandOptions;
import io.stallion.boot.StallionServer;
import io.stallion.plugins.PluginRegistry;
import io.stallion.plugins.flatBlog.FlatBlogPlugin;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;


/**
 * Runs a stallion site. This class is mainly used for debugging, and testing work in progress code, since
 * this main class can be executed from Maven.
 */
public class ServerMain {
    public static void main(String[] args) throws Exception {
        String folder = ServerMain.class.getResource("/contacts_site").getPath();
        ServeCommandOptions options = new ServeCommandOptions();
        CmdLineParser parser = new CmdLineParser(options);
        try {
            parser.parseArgument(args);
        } catch(CmdLineException e ) {
            System.err.println("\n\nError!\n\n" + e.getMessage());
            System.err.println("\n\nAllowed options: \n");
            parser.printUsage(System.err);
            System.err.println("\n");
            System.exit(1);
        }


        AppContextLoader.loadAndStartForTests(folder);
        FlatBlogPlugin pluginBooter = new FlatBlogPlugin();
        pluginBooter.boot();
        PluginRegistry.instance().getJavaPluginByName().put(pluginBooter.getPluginName(), pluginBooter);
        StallionServer server = new StallionServer();
        server.execute(server.newCommandOptions());
    }
}
