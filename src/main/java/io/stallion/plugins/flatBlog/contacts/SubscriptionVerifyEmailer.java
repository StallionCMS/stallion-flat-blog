package io.stallion.plugins.flatBlog.contacts;

import io.stallion.settings.Settings;

import java.util.List;


public class SubscriptionVerifyEmailer extends ContactEmailer {
    public SubscriptionVerifyEmailer(Contact contact) {
        super(contact);
        List<Subscription> subscriptions = SubscriptionController.instance()
                .filterByKey("contactId", contact.getId().toString())
                .filter("enabled", true)
                .all();
        put("subscriptions", subscriptions);
    }


    @Override
    public boolean isTransactional() {
        return true;
    }

    @Override
    public String getTemplate() {
        return getClass().getResource("/templates/subscription-verify-email.jinja").toString();
    }

    @Override
    public String getSubject() {
        return "Verify your email subscriptions to " + Settings.instance().getSiteName();
    }


}
