package io.stallion.plugins.flatBlog.contacts;


import io.stallion.dal.base.AlternativeKey;
import io.stallion.dal.base.ModelBase;
import io.stallion.dal.base.UniqueKey;
import io.stallion.dal.file.ModelWithFilePath;
import io.stallion.utils.GeneralUtils;


public class Subscription extends ModelBase implements ModelWithFilePath {
    private String name;
    private String ownerKey;
    private boolean enabled = true;
    private Long optInDate = 0L;
    private Long optOutDate = 0L;
    private Long createdAt = 0L;
    private Object contactId;
    private SubscriptionFrequency frequency = SubscriptionFrequency.INSTANT;
    private String filePath = "";
    private boolean canChangeFrequency = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @UniqueKey
    public String getOwnerKey() {
        return ownerKey;
    }

    public void setOwnerKey(String ownerKey) {
        this.ownerKey = ownerKey;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }



    @AlternativeKey
    public Object getContactId() {
        return contactId;
    }

    public void setContactId(Object contactId) {
        this.contactId = contactId;
    }


    public SubscriptionFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(SubscriptionFrequency frequency) {
        this.frequency = frequency;
    }

    public Long getOptInDate() {
        return optInDate;
    }

    public void setOptInDate(Long optInDate) {
        this.optInDate = optInDate;
    }

    public Long getOptOutDate() {
        return optOutDate;
    }

    public void setOptOutDate(Long optOutDate) {
        this.optOutDate = optOutDate;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }


    public String generateFilePath() {
        return GeneralUtils.slugify(getOwnerKey()) + "---" + getId() + ".json";
    }


    public String getFilePath() {
        return this.filePath;
    }


    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isCanChangeFrequency() {
        return canChangeFrequency;
    }

    public void setCanChangeFrequency(boolean canChangeFrequency) {
        this.canChangeFrequency = canChangeFrequency;
    }
}
