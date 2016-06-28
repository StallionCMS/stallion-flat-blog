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
import io.stallion.dataAccess.UniqueKey;
import io.stallion.dataAccess.file.ModelWithFilePath;
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
