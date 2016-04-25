/*
 * Stallion Flat-file Blog: A simple blog-engine
 *
 * Copyright (C) 2015 - 2016 Patrick Fitzsimmons.
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
