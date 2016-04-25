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
