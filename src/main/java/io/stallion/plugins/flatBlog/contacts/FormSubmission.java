package io.stallion.plugins.flatBlog.contacts;

import io.stallion.dal.base.AlternativeKey;
import io.stallion.dal.base.ModelBase;

import io.stallion.dal.base.Setable;
import io.stallion.dal.file.ModelWithFilePath;
import io.stallion.utils.DateUtils;
import io.stallion.utils.GeneralUtils;


import java.util.HashMap;
import java.util.Map;

import static io.stallion.dal.base.SettableOptions.*;

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
    @Setable(value=Immutable.class, creatable = true)
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


    @Setable(value=Immutable.class, creatable = true)
    public Map<String, Object> getData() {
        return data;
    }

    public FormSubmission setData(Map<String, Object> data) {
        this.data = data;
        return this;
    }


    @Setable(value=Immutable.class, creatable = true)
    public String getFormName() {
        return formName;
    }

    public FormSubmission setFormName(String formName) {
        this.formName = formName;
        return this;
    }


    @Setable(value=Immutable.class, creatable = true)
    public String getPageUrl() {
        return pageUrl;
    }

    public FormSubmission setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
        return this;
    }

    @Setable(value=Immutable.class, creatable = true)
    public String getPageTitle() {
        return pageTitle;
    }

    public FormSubmission setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
        return this;
    }

    @Setable(value=Immutable.class, creatable = true)
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

    @Setable(value=Immutable.class, creatable = true)
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
