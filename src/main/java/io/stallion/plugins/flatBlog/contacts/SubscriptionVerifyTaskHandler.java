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

package io.stallion.plugins.flatBlog.contacts;

import io.stallion.asyncTasks.AsyncTaskHandlerBase;
import io.stallion.utils.DateUtils;


import java.time.ZonedDateTime;


public class SubscriptionVerifyTaskHandler extends AsyncTaskHandlerBase {
    private Long contactId;


    public void process() {
        Contact contact = ContactsController.instance().forId(contactId);
        ZonedDateTime now = DateUtils.utcNow();
        ZonedDateTime rejectedAt = DateUtils.milsToDateTime(contact.getVerifyRejectedAt());
        ZonedDateTime sentAt = DateUtils.milsToDateTime(contact.getVerifySentAt());
        // We recently rejected a verification attempt, do not resent
        if (rejectedAt.plusDays(7).isAfter(now)) {
            return;
        }
        // We already sent a verification attempt in the last 24 hours, do not resend
        if (sentAt.plusDays(1).isAfter(now)) {
            return;
        }
        contact.setVerifySentAt(now.toInstant().toEpochMilli());
        new SubscriptionVerifyEmailer(contact).sendEmail();
    }

    public Long getContactId() {
        return contactId;
    }

    public SubscriptionVerifyTaskHandler setContactId(Long contactId) {
        this.contactId = contactId;
        return this;
    }
}
