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


import io.stallion.Context;
import io.stallion.asyncTasks.AsyncCoordinator;
import io.stallion.dataAccess.DataAccessRegistry;
import io.stallion.dataAccess.DataAccessRegistration;

import io.stallion.dataAccess.StandardModelController;

import io.stallion.dataAccess.file.JsonFilePersister;
import io.stallion.plugins.flatBlog.FlatBlogSettings;
import io.stallion.services.Log;
import io.stallion.utils.DateUtils;


import static io.stallion.utils.Literals.*;


public class SubscriptionController extends StandardModelController<Subscription>  {

    public static SubscriptionController instance() {
        return (SubscriptionController) Context.dal().get("subscriptions");
    }

    public Subscription unsubscribe(Contact contact, Long subscriptionId) {
        Subscription subscription = SubscriptionController.instance().forId(subscriptionId);
        if (emptyInstance(subscription)) {
            return subscription;
        }
        subscription.setOptOutDate(DateUtils.mils());
        subscription.setEnabled(false);
        SubscriptionController.instance().save(subscription);
        return subscription;
    }

    public Subscription resubscribe(Contact contact, Long subscriptionId) {
        Subscription subscription = SubscriptionController.instance().forId(subscriptionId);
        if (emptyInstance(subscription)) {
            return subscription;
        }
        subscription.setOptOutDate(null);
        subscription.setEnabled(true);
        SubscriptionController.instance().save(subscription);
        if (FlatBlogSettings.getInstance().getDoubleOptIn() != true) {
            contact.setVerifiedOptIn(true);
        }
        if (!contact.isVerifiedOptIn()) {
            enqueueVerifyOptIn(contact);
        }
        return subscription;
    }

    public Subscription saveContactSubscription(Contact contact, String name, String ownerKey, SubscriptionFrequency frequency) {
        Subscription subscription = SubscriptionController.instance().forUniqueKey("ownerKey", ownerKey);
        if (subscription == null) {
            if (frequency.equals(SubscriptionFrequency.NEVER)) {
                Log.info("Frequency is never, so no subscription email={0} subname={1}", contact.getEmail(), name);
                return null;
            }
            subscription = new Subscription();
            subscription.setCreatedAt(DateUtils.mils());
            subscription.setOwnerKey(ownerKey);
        }
        subscription.setContactId(contact.getId());
        subscription.setName(name);
        subscription.setFrequency(frequency);
        subscription.setEnabled(true);

        if (frequency.equals(SubscriptionFrequency.NEVER)) {
            subscription.setEnabled(false);
            subscription.setOptOutDate(mils());
        }
        SubscriptionController.instance().save(subscription);
        Log.info("Subscription saved email={0} subname={1} frequency={2} enabled={3} ownerKey={4}",
                contact.getEmail(), name, subscription.getFrequency(), subscription.isEnabled(), subscription.getOwnerKey());
        if (FlatBlogSettings.getInstance().getDoubleOptIn() != true) {
            contact.setVerifiedOptIn(true);
        }
        if (!contact.isVerifiedOptIn()) {
            enqueueVerifyOptIn(contact);
        }
        return subscription;
    }

    public void enqueueVerifyOptIn(Contact contact) {
        AsyncCoordinator.instance().enqueue(
                new SubscriptionVerifyTaskHandler().setContactId(contact.getId()),
                "subscription-verify-" + contact.getId().toString() + "--" + DateUtils.formatNow("YYYY-MM-dd"),
                DateUtils.mils()
        );
    }


    public void globalOptOut(Contact contact) {
        contact.setOptedOut(true);
        contact.setOptOutDate(DateUtils.mils());
        ContactsController.instance().save(contact);
    }

    public static void register() {
        DataAccessRegistration registration = new DataAccessRegistration()
                .setModelClass(Subscription.class)
                .setControllerClass(SubscriptionController.class)
                .setShouldWatch(false)
                .setUseDataFolder(true)
                .setNameSpace("")
                .setWritable(true)
                .setPath("subscriptions")
                .setPersisterClass(JsonFilePersister.class);
        Context.dal().register(registration);
    }


    public  Long generateId(Subscription obj) {
        return DataAccessRegistry.instance().getTickets().nextId();
    }
}
