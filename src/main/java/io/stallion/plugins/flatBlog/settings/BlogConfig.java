package io.stallion.plugins.flatBlog.settings;

import org.apache.commons.lang3.StringUtils;

public class BlogConfig {
    private String rootUrl = "/";
    private String folder;
    private String table;
    private String id = "";
    private String templateFolder;
    private String bucket;
    private Boolean useDatabase = null;
    private String title = "";
    private String metaDescription = "";
    private int postsPerPage = 10;

    public String getTemplatePath(String type) {
        String path = templateFolder + "/" + type + ".jinja";
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }

    public String getRootUrl() {
        return rootUrl;
    }

    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getId() {
        if (StringUtils.isEmpty(id)) {
            if (StringUtils.isEmpty(folder)) {
                return table;
            } else {
                return folder;
            }
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemplateFolder() {
        return templateFolder;
    }

    public void setTemplateFolder(String templateFolder) {
        this.templateFolder = templateFolder;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public Boolean getUseDatabase() {
        return useDatabase;
    }

    public void setUseDatabase(Boolean useDatabase) {
        this.useDatabase = useDatabase;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public int getPostsPerPage() {
        return postsPerPage;
    }

    public void setPostsPerPage(int postsPerPage) {
        this.postsPerPage = postsPerPage;
    }
}
