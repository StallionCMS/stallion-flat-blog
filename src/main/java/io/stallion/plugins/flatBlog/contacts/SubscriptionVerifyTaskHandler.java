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
