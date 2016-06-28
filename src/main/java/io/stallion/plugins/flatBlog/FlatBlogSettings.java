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

package io.stallion.plugins.flatBlog;

import io.stallion.exceptions.ConfigException;
import io.stallion.plugins.BasePluginSettings;
import io.stallion.plugins.flatBlog.settings.BlogConfig;
import io.stallion.settings.SettingMeta;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class FlatBlogSettings extends BasePluginSettings {
    @SettingMeta(cls=ArrayList.class)
    private List<String> notifyEmails;
    @SettingMeta()
    private Boolean doubleOptIn;
    @SettingMeta()
    private Boolean moderationEnabled = null;
    @SettingMeta(valInt = 90)
    private Integer commentsClosedAfterDays = null;
    @SettingMeta()
    private Boolean requireValidEmail = null;
    @SettingMeta()
    private String akismetKey = null;
    @SettingMeta()
    private String reCaptchaSecret = null;
    @SettingMeta()
    private String reCaptchaSiteKey = null;
    @SettingMeta(val="comments")
    private String folderName = null;
    @SettingMeta()
    private String realIpHeader;
    @SettingMeta(cls=ArrayList.class)
    private List<String> moderatorEmails = null;
    @SettingMeta(valBoolean = false, help="If true, will display a gravtar image next to users images.")
    private Boolean commentsUseGravatar;

    @SettingMeta(val="blog_posts")
    private String blogPostTableName;

    @SettingMeta(cls=ArrayList.class)
    private List<BlogConfig> blogs;

    public static FlatBlogSettings getInstance() {
        return getInstance(FlatBlogSettings.class, "flatBlog");
    }

    public void validate() {
        if (getBlogs() == null || getBlogs().size() < 1) {
            throw new ConfigException("No blogs defined in your flatBlog plugin settings!");
        }
        int x = 0;
        for (BlogConfig config: getBlogs()) {
            x++;
            if (StringUtils.isEmpty(config.getFolder())) {
                throw new ConfigException("No folder defined for flatBlog config #" + x);
            }

        }
    }


    public void assignDefaults() {

    }

    public List<BlogConfig> getBlogs() {
        return blogs;
    }

    public void setBlogs(List<BlogConfig> blogs) {
        this.blogs = blogs;
    }

    public String getBlogPostTableName() {
        return blogPostTableName;
    }

    public void setBlogPostTableName(String blogPostTableName) {
        this.blogPostTableName = blogPostTableName;
    }


    public List<String> getNotifyEmails() {
        return notifyEmails;
    }

    public void setNotifyEmails(List<String> notifyEmails) {
        this.notifyEmails = notifyEmails;
    }

    public Boolean getDoubleOptIn() {
        return doubleOptIn;
    }

    public void setDoubleOptIn(Boolean doubleOptIn) {
        this.doubleOptIn = doubleOptIn;
    }

    public Boolean getModerationEnabled() {
        return moderationEnabled;
    }

    public void setModerationEnabled(Boolean moderationEnabled) {
        this.moderationEnabled = moderationEnabled;
    }

    public int getCommentsClosedAfterDays() {
        return commentsClosedAfterDays;
    }

    public void setCommentsClosedAfterDays(int commentsClosedAfterDays) {
        this.commentsClosedAfterDays = commentsClosedAfterDays;
    }

    public Boolean getRequireValidEmail() {
        return requireValidEmail;
    }

    public void setRequireValidEmail(Boolean requireValidEmail) {
        this.requireValidEmail = requireValidEmail;
    }

    public String getAkismetKey() {
        return akismetKey;
    }

    public void setAkismetKey(String akismetKey) {
        this.akismetKey = akismetKey;
    }


    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }


    public String getRealIpHeader() {
        return realIpHeader;
    }

    public void setRealIpHeader(String realIpHeader) {
        this.realIpHeader = realIpHeader;
    }

    public String getReCaptchaSecret() {
        return reCaptchaSecret;
    }

    public void setReCaptchaSecret(String reCaptchaSecret) {
        this.reCaptchaSecret = reCaptchaSecret;
    }

    public List<String> getModeratorEmails() {
        return moderatorEmails;
    }

    public void setModeratorEmails(List<String> moderatorEmails) {
        this.moderatorEmails = moderatorEmails;
    }

    public String getReCaptchaSiteKey() {
        return reCaptchaSiteKey;
    }

    public void setReCaptchaSiteKey(String reCaptchaSiteKey) {
        this.reCaptchaSiteKey = reCaptchaSiteKey;
    }

    public Boolean getCommentsUseGravatar() {
        return commentsUseGravatar;
    }

    public FlatBlogSettings setCommentsUseGravatar(Boolean commentsUseGravatar) {
        this.commentsUseGravatar = commentsUseGravatar;
        return this;
    }
}
