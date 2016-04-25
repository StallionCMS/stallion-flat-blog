package io.stallion.plugins.flatBlog.comments;

import io.stallion.dal.base.Setable;
import io.stallion.dal.base.SettableOptions;
import io.stallion.plugins.flatBlog.contacts.SubscriptionFrequency;


public class CommentSubscriptionInfo {
    private SubscriptionFrequency replyNotifyFrequency = SubscriptionFrequency.DAILY;
    private SubscriptionFrequency threadNotifyFrequency = SubscriptionFrequency.NEVER;
    private boolean blogSubscribe = false;

    @Setable(value = SettableOptions.Unrestricted.class, creatable = true)
    public SubscriptionFrequency getReplyNotifyFrequency() {
        return replyNotifyFrequency;
    }

    public CommentSubscriptionInfo setReplyNotifyFrequency(SubscriptionFrequency replyNotifyFrequency) {
        this.replyNotifyFrequency = replyNotifyFrequency;
        return this;
    }

    @Setable(value = SettableOptions.Unrestricted.class, creatable = true)
    public SubscriptionFrequency getThreadNotifyFrequency() {
        return threadNotifyFrequency;
    }

    public CommentSubscriptionInfo setThreadNotifyFrequency(SubscriptionFrequency threadNotifyFrequency) {
        this.threadNotifyFrequency = threadNotifyFrequency;
        return this;
    }

    @Setable(value = SettableOptions.Unrestricted.class, creatable = true)
    public boolean isBlogSubscribe() {
        return blogSubscribe;
    }

    public CommentSubscriptionInfo setBlogSubscribe(Boolean blogSubscribe) {
        if (blogSubscribe == null) {
            blogSubscribe = false;
        }
        this.blogSubscribe = blogSubscribe;
        return this;
    }
}
