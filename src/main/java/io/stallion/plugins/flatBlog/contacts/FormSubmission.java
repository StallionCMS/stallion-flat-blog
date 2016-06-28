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

package io.stallion.plugins.flatBlog.contacts;

import io.stallion.dataAccess.AlternativeKey;
import io.stallion.dataAccess.ModelBase;

import io.stallion.dataAccess.file.ModelWithFilePath;
import io.stallion.utils.DateUtils;
import io.stallion.utils.GeneralUtils;


import java.util.HashMap;
import java.util.Map;

public class FormSubmission extends ModelBase implements ModelWithFilePath {

    private String email = "";
    private String everCookie = "";
    private Long contactId = 0L;
    private Long submittedAt = 0L;
    private Map<String, Object> data = new HashMap<String, Object>();
    private String formName = "";
    private String pageUrl = "";
    private String pageTitle = "";
    private String formId = "";
    private String filePath = "";

    @AlternativeKey
    public String getEmail() {
        return email;
    }

    public FormSubmission setEmail(String email) {
        this.email = email;
        return this;
    }

    @AlternativeKey
    public String getEverCookie() {
        return everCookie;
    }

    public FormSubmission setEverCookie(String everCookie) {
        this.everCookie = everCookie;
        return this;
    }

    @AlternativeKey
    public Long getContactId() {
        return contactId;
    }

    public FormSubmission setContactId(Long contactId) {
        this.contactId = contactId;
        return this;
    }


    public Map<String, Object> getData() {
        return data;
    }

    public FormSubmission setData(Map<String, Object> data) {
        this.data = data;
        return this;
    }


    public String getFormName() {
        return formName;
    }

    public FormSubmission setFormName(String formName) {
        this.formName = formName;
        return this;
    }


    public String getPageUrl() {
        return pageUrl;
    }

    public FormSubmission setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
        return this;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public FormSubmission setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
        return this;
    }

        public String getFormId() {
        return formId;
    }

    public FormSubmission setFormId(String formId) {
        this.formId = formId;
        return this;
    }

    public Long getSubmittedAt() {
        return submittedAt;
    }

    public FormSubmission setSubmittedAt(Long submittedAt) {
        this.submittedAt = submittedAt;
        return this;
    }

    public String generateFilePath() {
        return DateUtils.formatLocalDate(getSubmittedAt(), "YYYY-mm-dd-HHmmss-") + GeneralUtils.slugify(getEmail()) + "---" + getId() + ".json";
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
