package io.stallion.plugins.flatBlog.contacts;

import io.stallion.dal.base.AlternativeKey;
import io.stallion.dal.base.MappedModel;
import io.stallion.dal.base.UniqueKey;
import io.stallion.utils.json.JSON;


public class Notification extends MappedModel {
    private String key;
    private Long contactId;
    private String subscriptionId;

    private boolean seen = false;
    private boolean sent = false;
    private Long sendAt = 0L;
    private Long sentAt = 0L;
    private Long createdAt = 0L;
    private String callbackClassName = "";
    private String callbackPlugin = null;
    private SubscriptionFrequency frequency = SubscriptionFrequency.DAILY;
    private String periodKey = "";
    private String extraData = "";


    @UniqueKey
    public String getKey() {
        return key;
    }

    public Notification setKey(String key) {
        this.key = key;
        return this;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public Notification setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
        return this;
    }

    public boolean isSeen() {
        return seen;
    }

    public Notification setSeen(boolean seen) {
        this.seen = seen;
        return this;
    }

    public boolean isSent() {
        return sent;
    }

    public Notification setSent(boolean sent) {
        this.sent = sent;
        return this;
    }

    public Long getSendAt() {
        return sendAt;
    }

    public Notification setSendAt(Long sendAt) {
        this.sendAt = sendAt;
        return this;
    }

    public Long getSentAt() {
        return sentAt;
    }

    public Notification setSentAt(Long sentAt) {
        this.sentAt = sentAt;
        return this;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Notification setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getCallbackClassName() {
        return callbackClassName;
    }

    public Notification setCallbackClassName(String callbackClassName) {
        this.callbackClassName = callbackClassName;
        return this;
    }

    public String getCallbackPlugin() {
        return callbackPlugin;
    }

    public Notification setCallbackPlugin(String callbackPlugin) {
        this.callbackPlugin = callbackPlugin;
        return this;
    }

    public SubscriptionFrequency getFrequency() {
        return frequency;
    }

    public Notification setFrequency(SubscriptionFrequency frequency) {
        this.frequency = frequency;
        return this;
    }

    @AlternativeKey
    public String getPeriodKey() {
        return periodKey;
    }

    public Notification setPeriodKey(String periodKey) {
        this.periodKey = periodKey;
        return this;
    }

    public Notification setHandler(NotificationCallbackHandlerInterface handler) {
        this.setExtraData(JSON.stringify(handler));
        this.setCallbackClassName(handler.getClass().getCanonicalName());
        return this;
    }


    public Notification setExtraData(String extraData) {
        this.extraData = extraData;
        return this;
    }

    public String getExtraData() {
        return extraData;
    }

    public Long getContactId() {
        return contactId;
    }

    public Notification setContactId(Long contactId) {
        this.contactId = contactId;
        return this;
    }
}
