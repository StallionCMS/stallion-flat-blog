package io.stallion.plugins.flatBlog.contacts;

import io.stallion.dal.base.MappedModel;
import io.stallion.dal.base.UniqueKey;
import io.stallion.dal.file.ModelWithFilePath;
import io.stallion.email.Contactable;
import io.stallion.utils.GeneralUtils;


public class Contact extends MappedModel implements ModelWithFilePath, Contactable {
    private String givenName = "";
    private String familyName = "";
    private String email = "";
    private String displayName = "";
    private String webSite = "";
    private String everCookie = "";
    private String filePath = "";
    private String secretToken = "";
    private boolean optedOut = false;
    private boolean disabled = false;
    private boolean totallyOptedOut;
    private String honorific = "";
    private Long optOutDate = 0L;
    private boolean verifiedOptIn = false;
    private long optInAt = 0;
    private long verifySentAt = 0;
    private long verifyRejectedAt = 0;
    private boolean verifiedEmail = false;

    public String getGivenName() {
        return givenName;
    }

    public Contact setGivenName(String givenName) {
        this.givenName = givenName;
        return this;
    }

    public String getFamilyName() {
        return familyName;
    }

    public Contact setFamilyName(String familyName) {
        this.familyName = familyName;
        return this;
    }

    @UniqueKey
    public String getEmail() {
        return email;
    }

    public Contact setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Contact setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getWebSite() {
        return webSite;
    }

    public Contact setWebSite(String webSite) {
        this.webSite = webSite;
        return this;
    }

    @UniqueKey
    public String getEverCookie() {
        return everCookie;
    }

    public Contact setEverCookie(String everCookie) {
        this.everCookie = everCookie;
        return this;
    }


    public String generateFilePath() {
        return GeneralUtils.slugify(getEmail()) + "---" + getId() + ".json";
    }


    public String getFilePath() {
        return filePath;
    }


    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    @UniqueKey
    public String getSecretToken() {
        return secretToken;
    }

    public void setSecretToken(String secretToken) {
        this.secretToken = secretToken;
    }



    public Long getOptOutDate() {
        return optOutDate;
    }

    public void setOptOutDate(Long optOutDate) {
        this.optOutDate = optOutDate;
    }

    public boolean isVerifiedOptIn() {
        return verifiedOptIn;
    }

    public void setVerifiedOptIn(boolean verifiedOptIn) {
        this.verifiedOptIn = verifiedOptIn;
    }

    public long getOptInAt() {
        return optInAt;
    }

    public void setOptInAt(long optInAt) {
        this.optInAt = optInAt;
    }

    public long getVerifySentAt() {
        return verifySentAt;
    }

    public void setVerifySentAt(long verifySentAt) {
        this.verifySentAt = verifySentAt;
    }

    public long getVerifyRejectedAt() {
        return verifyRejectedAt;
    }

    public void setVerifyRejectedAt(long verifyRejectedAt) {
        this.verifyRejectedAt = verifyRejectedAt;
    }

    public boolean isVerifiedEmail() {
        return verifiedEmail;
    }

    public void setVerifiedEmail(boolean verifiedEmail) {
        this.verifiedEmail = verifiedEmail;
    }

    public boolean isOptedOut() {
        return optedOut;
    }

    public Contact setOptedOut(boolean optedOut) {
        this.optedOut = optedOut;
        return this;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public Contact setDisabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public boolean isTotallyOptedOut() {
        return totallyOptedOut;
    }

    public Contact setTotallyOptedOut(boolean totallyOptedOut) {
        this.totallyOptedOut = totallyOptedOut;
        return this;
    }

    public String getHonorific() {
        return honorific;
    }

    public Contact setHonorific(String honorific) {
        this.honorific = honorific;
        return this;
    }
}
