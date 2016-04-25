package io.stallion.plugins.flatBlog.comments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.stallion.Context;
import io.stallion.dal.base.*;
import io.stallion.dal.file.ModelWithFilePath;
import io.stallion.users.Role;
import io.stallion.utils.DateUtils;
import io.stallion.utils.GeneralUtils;
import io.stallion.utils.json.JSON;
import io.stallion.utils.json.RestrictedViews;
import static io.stallion.dal.base.SettableOptions.*;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import javax.servlet.http.Cookie;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static io.stallion.utils.Literals.empty;
import static io.stallion.utils.Literals.list;

public class Comment extends ModelBase implements ModelWithFilePath {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd-HHmmss");

    private String authorFirstName = "";
    private String authorLastName = "";
    private String authorDisplayName = "";
    private String bodyHtml = "";
    private String bodyMarkdown = "";
    private String authorEmail = "";
    private String authorWebSite = "";
    private String authorSecret = "";
    private State state = State.PENDING_AKISMET;
    private Boolean isApproved = false;
    private Boolean akismetApproved = false;
    private Boolean moderatorApproved = false;
    private Long akismetCheckedAt = 0L;
    private Long moderatedAt = 0L;
    private Long approvedAt = 0L;
    private Long parentId = 0L;
    private String threadId = "";
    private String editingToken = "";
    private String parentPermalink = "";
    private String captchaResponse = "";
    private Long createdTicks = 0L;
    private String filePath = "";
    private String parentTitle;
    private Long contactId;
    private Long notificationsRegisteredAt = 0L;
    private Boolean previouslyApproved = false;
    private Boolean threadSubscribe = false;
    private Boolean mentionSubscribe = false;

    @Setable(value = OwnerUpdateable.class, creatable = true)
    public String getAuthorFirstName() {
        return authorFirstName;
    }

    public Comment setAuthorFirstName(String authorFirstName) {
        this.authorFirstName = authorFirstName;
        return this;
    }



    @Setable(value = AnyUpdateable.class, creatable = true)
    public String getAuthorLastName() {
        return authorLastName;
    }

    public Comment setAuthorLastName(String authorLastName) {
        this.authorLastName = authorLastName;
        return this;
    }



    @Setable(value = AnyUpdateable.class, creatable = true)
    @JsonView(RestrictedViews.Public.class)
    public String getAuthorDisplayName() {
        return authorDisplayName;
    }

    public Comment setAuthorDisplayName(String authorDisplayName) {
        this.authorDisplayName = authorDisplayName;
        return this;
    }




    @JsonView(RestrictedViews.Public.class)
    public String getBodyHtml() {
        return bodyHtml;
    }

    public Comment setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
        return this;
    }


    @JsonView(RestrictedViews.Member.class)
    @Setable(value = Immutable.class, creatable = true)
    public String getAuthorEmail() {
        return authorEmail;
    }

    public Comment setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
        return this;
    }


    @Setable(value = AnyUpdateable.class, creatable = true)
    @JsonView(RestrictedViews.Public.class)
    public String getAuthorWebSite() {
        return authorWebSite;
    }

    public Comment setAuthorWebSite(String authorWebSite) {
        this.authorWebSite = authorWebSite;
        return this;
    }

    public State getState() {
        return state;
    }

    public Comment setState(State state) {
        this.state = state;
        return this;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }

    @JsonView(RestrictedViews.Member.class)
    public Comment setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
        return this;
    }

    public Boolean getAkismetApproved() {
        return akismetApproved;
    }

    public Comment setAkismetApproved(Boolean akismetApproved) {
        this.akismetApproved = akismetApproved;
        return this;
    }

    public Boolean getModeratorApproved() {
        return moderatorApproved;
    }

    public Comment setModeratorApproved(Boolean moderatorApproved) {
        this.moderatorApproved = moderatorApproved;
        return this;
    }

    public Long getAkismetCheckedAt() {
        return akismetCheckedAt;
    }

    public Comment setAkismetCheckedAt(Long akismetCheckedAt) {
        this.akismetCheckedAt = akismetCheckedAt;
        return this;
    }

    public Long getModeratedAt() {
        return moderatedAt;
    }

    public Comment setModeratedAt(Long moderatedAt) {
        this.moderatedAt = moderatedAt;
        return this;
    }


    @JsonView(RestrictedViews.Public.class)
    @Setable(value = Immutable.class, creatable = true)
    public Long getParentId() {
        return parentId;
    }

    public Comment setParentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }


    @JsonView(RestrictedViews.Public.class)
    @Setable(value = Immutable.class, creatable = true)
    @AlternativeKey
    public String getThreadId() {
        return threadId;
    }

    public Comment setThreadId(String threadId) {
        this.threadId = threadId;
        return this;
    }

    public String getEditingToken() {
        return editingToken;
    }

    public Comment setEditingToken(String editingToken) {
        this.editingToken = editingToken;
        return this;
    }

    @Setable(value = Immutable.class, creatable = true)
    public String getParentPermalink() {
        return parentPermalink;
    }

    public Comment setParentPermalink(String parentPermalink) {
        this.parentPermalink = parentPermalink;
        return this;
    }


    @Setable(value = Immutable.class, creatable = true)
    public String getCaptchaResponse() {
        return captchaResponse;
    }

    public Comment setCaptchaResponse(String captchaResponse) {
        this.captchaResponse = captchaResponse;
        return this;
    }

    @JsonView(RestrictedViews.Public.class)
    public Long getCreatedTicks() {
        return createdTicks;
    }

    public Comment setCreatedTicks(Long createdTicks) {
        this.createdTicks = createdTicks;
        return this;
    }


    public String getAuthorSecret() {
        return authorSecret;
    }

    public Comment setAuthorSecret(String authorSecret) {
        this.authorSecret = authorSecret;
        return this;
    }

    @JsonView(RestrictedViews.Public.class)
    public String getAvatarLetter() {
        return authorDisplayName.substring(0, 1).toUpperCase();
    }

    @JsonView(RestrictedViews.Public.class)
    public String getAvatarColor() {

        String hash = DigestUtils.md5Hex(authorDisplayName).toUpperCase();
        String r = Integer.toHexString(Integer.parseInt(hash.substring(0, 1), 16) / 2);
        String g = Integer.toHexString(Integer.parseInt(hash.substring(1, 2), 16) / 2);
        String b = Integer.toHexString(Integer.parseInt(hash.substring(2, 3), 16) / 2);
        return "#" + r + r + g + g + b + b;
    }

    @JsonIgnore
    @JsonView(RestrictedViews.Member.class)
    public boolean isEditable() {
        if (Context.getUser().isInRole(Role.STAFF)) {
            return true;
        }
        if (Context.getRequest() != null) {
            Cookie cookie = Context.getRequest().getCookie(Constants.AUTHOR_SECRET_COOKIE);
            if (cookie != null && !empty(cookie.getValue())) {
                if (cookie.getValue().equals(getAuthorSecret())) {
                    return true;
                }
            }
        }
        return false;
    }

    @JsonIgnore
    public boolean isAdminable() {
        if (Context.getRequest() != null) {
            Cookie cookie = Context.getRequest().getCookie(Constants.AUTHOR_SECRET_COOKIE);
            if (cookie != null) {
                if (cookie.getValue().equals(getAuthorSecret())) {
                    return true;
                }
            }
        }
        return false;
    }


    @JsonProperty
    public String getThreadIdSlugified() {
        return GeneralUtils.slugify(getThreadId().toString());
    }

    @JsonIgnore
    public String getOwnerJson() {
        try {
            return JSON.stringify(this, RestrictedViews.Member.class, true);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateFilePath() {
        String threadId = getThreadId().toString();
        if (threadId.contains(".")) {
            threadId = FilenameUtils.getBaseName(threadId);
        }
        return threadId + "/" + DateUtils.utcNow().format(formatter) + "-" + GeneralUtils.slugify(getAuthorEmail()) + ".json";
    }

    public String getFilePath() {
        return filePath;
    }


    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @JsonIgnore
    public String getModeratedClass() {
        if (state == State.APPROVED) {
            return "st-comment-approved";
        } else if (state == State.PENDING_MODERATION) {
            return "st-comment-pending";
        } else {
            return "st-comment-rejected";
        }
    }


    @JsonView(RestrictedViews.Public.class)
    @Setable(value = Immutable.class, creatable = true)
    public String getParentTitle() {
        return parentTitle;
    }

    public Comment setParentTitle(String parentTitle) {
        this.parentTitle = parentTitle;
        return this;
    }

    public String getPermalink() {
        return this.parentPermalink + "#st-comment-" + getId();
    }

    public String permalinkWithParams(Map<String, String> params) {
        List<BasicNameValuePair> pairs = list();
        for(Map.Entry<String, String> entry: params.entrySet()) {
            pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return permalinkWithQuery(URLEncodedUtils.format(pairs, "UTF-8"));
    }

    public String permalinkWithQuery(String query) {
        String link;
        if (this.parentPermalink.contains("?")) {
            link = this.parentPermalink + "&";
        } else {
            link = this.parentPermalink + "?";
        }
        return link + query + "#st-comment-" + getId();
    }

    @JsonView(RestrictedViews.Owner.class)
    public Long getContactId() {
        return contactId;
    }

    public Comment setContactId(Long contactId) {
        this.contactId = contactId;
        return this;
    }

    public Long getApprovedAt() {
        return approvedAt;
    }

    public Comment setApprovedAt(Long approvedAt) {
        this.approvedAt = approvedAt;
        return this;
    }

    public Boolean getPreviouslyApproved() {
        return previouslyApproved;
    }

    public Comment setPreviouslyApproved(Boolean previouslyApproved) {
        this.previouslyApproved = previouslyApproved;
        return this;
    }

    public Long getNotificationsRegisteredAt() {
        return notificationsRegisteredAt;
    }

    public Comment setNotificationsRegisteredAt(Long notificationsRegisteredAt) {
        this.notificationsRegisteredAt = notificationsRegisteredAt;
        return this;
    }

    @JsonView(RestrictedViews.Member.class)
    @Setable(value = Immutable.class, creatable = true)
    public Boolean getThreadSubscribe() {
        return threadSubscribe;
    }

    public void setThreadSubscribe(Boolean threadSubscribe) {
        this.threadSubscribe = threadSubscribe;
    }

    @JsonView(RestrictedViews.Member.class)
    @Setable(value = Immutable.class, creatable = true)
    public Boolean getMentionSubscribe() {
        return mentionSubscribe;
    }

    public void setMentionSubscribe(Boolean mentionSubscribe) {
        this.mentionSubscribe = mentionSubscribe;
    }

    @JsonView(RestrictedViews.Member.class)
    @Setable(value = AnyUpdateable.class, creatable = true)
    public String getBodyMarkdown() {
        return bodyMarkdown;
    }

    public Comment setBodyMarkdown(String bodyMarkdown) {
        this.bodyMarkdown = bodyMarkdown;
        return this;
    }
}
