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

import io.stallion.asyncTasks.AsyncTaskHandlerBase;
import io.stallion.services.Log;
import io.stallion.utils.StallionClassLoader;
import io.stallion.utils.json.JSON;

import java.util.List;

import static io.stallion.utils.Literals.empty;
import static io.stallion.utils.Literals.list;


public class NotificationAsyncTaskHandler extends AsyncTaskHandlerBase {

    private String periodKey = "";


    public void process() {
        Log.info("Processing notifications for periodKey {0}", getPeriodKey());
        //List<Notification> notifications = NotificationController.instance().listForKey("periodKey", getPeriodKey().toString());
        List<Notification> notifications = NotificationController.instance().filter("periodKey", getPeriodKey().toString()).all();
        if (empty(notifications)) {
            Log.warn("Surprisingly, no notifications for periodKey {0}", getPeriodKey());
            return;
        }
        List<NotificationCallbackResult> results = list();
        Contact contact = null;

        for (Notification notification: notifications) {
            Log.info("Looping over notification {0} {1}", notification.getKey());
            if (notification.isSeen()) {
                return;
            }
            if (notification.isSent()) {
                return;
            }

            Class cls = StallionClassLoader.loadClass(notification.getCallbackPlugin(), notification.getCallbackClassName());

            NotificationCallbackHandlerInterface handler = (NotificationCallbackHandlerInterface)JSON.parse(notification.getExtraData(), cls);
            Log.info("Run notification handler for {0} {1}", notification.getKey(), handler);
            NotificationCallbackResult result = handler.handle(notification);
            if (result != null) {
                results.add(result);
            }
            if (contact == null) {
                contact = ContactsController.instance().forId(notification.getContactId());
            }

        }



        if (contact != null && !contact.isOptedOut() && results.size() > 0) {
            Log.info("Sending notification email to {0} for period {1}", contact.getEmail(), getPeriodKey());
            new NotificationEmailer(contact, results).sendEmail();
        } else {
            Log.info("No notifications to send");
        }
    }

    public String getPeriodKey() {
        return periodKey;
    }

    public NotificationAsyncTaskHandler setPeriodKey(String periodKey) {
        this.periodKey = periodKey;
        return this;
    }
}
