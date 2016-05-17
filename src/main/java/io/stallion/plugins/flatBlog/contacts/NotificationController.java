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


import io.stallion.Context;
import io.stallion.asyncTasks.AsyncCoordinator;
import io.stallion.dataAccess.DataAccessRegistry;
import io.stallion.dataAccess.DataAccessRegistration;

import io.stallion.dataAccess.StandardModelController;
import io.stallion.dataAccess.file.JsonFilePersister;
import io.stallion.exceptions.UsageException;
import io.stallion.services.Log;
import io.stallion.settings.Settings;
import io.stallion.utils.DateUtils;


import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

import static io.stallion.utils.Literals.*;


public class NotificationController extends StandardModelController<Notification> {

    public static NotificationController instance() {
        return (NotificationController) Context.dal().get("notifications");
    }

    public Notification submitNotification(Notification notification) {
        if (empty(notification.getKey())) {
            throw new UsageException("The notification instance must have a non-empty key field");
        }
        if (empty(notification.getCallbackClassName())) {
            throw new UsageException("The notification instance must have a non-empty callbackClassName. Did you call setHandler() to pass in your handler instance?");
        }
        if (empty(notification.getCallbackPlugin())) {
            throw new UsageException("The notification instance must have a non-empty callbackPlugin");
        }
        if (emptyInstance(notification.getFrequency())) {
            throw new UsageException("The notification instance must have a non-empty frequency");
        }
        if (empty(notification.getContactId())) {
            throw new UsageException("The notification instance must have a non-empty contact");
        }
        if (empty(notification.getExtraData())) {
            throw new UsageException("Extra data must not be empty. Did you call setHandler() to pass in your handler instance.");
        }
        if (NotificationController.instance().forUniqueKey("key", notification.getKey().toString()) != null) {
            Log.info("Notification already exists for key={0} do not re-register", notification.getKey());
            return notification;
        }
        if (notification.getFrequency() == null) {
            notification.setFrequency(SubscriptionFrequency.DAILY);
        }
        if (empty(notification.getSendAt())) {
            ZonedDateTime sendAt = DateUtils.utcNow();

            if (notification.getFrequency().equals(SubscriptionFrequency.DAILY)) {
                ZonedDateTime thisEvening = sendAt.withHour(22).withMinute(0).withSecond(0).withNano(0);
                if (thisEvening.isBefore(sendAt)) {
                    sendAt = thisEvening.plusDays(1);
                } else {
                    sendAt = thisEvening;
                }
            } else if (notification.getFrequency().equals(SubscriptionFrequency.WEEKLY)) {
                ZonedDateTime friday = sendAt.with(ChronoField.DAY_OF_WEEK, 5).withHour(22).withMinute(0).withSecond(0).withNano(0);
                if (friday.isBefore(sendAt)) {
                    sendAt = friday.plusDays(7);
                } else {
                    sendAt = friday;
                }
            }
            // We always wait at least five minutes, to give the user time to make quick edits
            // to the comment. Then we send the notification at the next 15 minute interval.
            // This allows us to group notifications together rather than spamming the user.
            if (Settings.instance().getDebug() != true) {
                sendAt = sendAt.plusMinutes(5);
                sendAt = sendAt.plusMinutes((15 - sendAt.getMinute() % 15));
            } else {
                // Debug mode, only add one minute so we can get the email quickly
                sendAt = sendAt.plusMinutes(1);
            }
            notification.setSendAt(sendAt.withSecond(0).withNano(0).toInstant().toEpochMilli());
        }


        notification.setPeriodKey(
                notification.getContactId() + "--" +
                        DateUtils.milsToDateTime(notification.getSendAt()).format(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"))
        );


        Log.info("Save notification periodKey={0} key={1} sendAt={2} sendAtTicks={3}",
                notification.getPeriodKey(),
                notification.getKey(),
                DateUtils.formatLocalDateFromLong(notification.getSendAt()),
                notification.getSendAt()
        );

        NotificationController.instance().save(notification);

        NotificationAsyncTaskHandler handler = new NotificationAsyncTaskHandler()
                .setPeriodKey(notification.getPeriodKey());

        // All notifications for the same period will have the same periodKey and same customKey
        // Thus, only one task will ever be created per period, and so only one email will ever be sent.
        AsyncCoordinator.instance().enqueue(
                handler,
                "notification---" + notification.getSendAt() + "---" + notification.getContactId(),
                notification.getSendAt());

        return notification;
    }

    public static void register() {
        DataAccessRegistration registration = new DataAccessRegistration()
                .setModelClass(Notification.class)
                .setControllerClass(NotificationController.class)
                .setShouldWatch(false)
                .setUseDataFolder(true)
                .setNameSpace("")
                .setWritable(true)
                .setPath("notifications")
                .setPersisterClass(JsonFilePersister.class);
        Context.dal().register(registration);
    }


    public Long generateId(Notification obj) {
        return DataAccessRegistry.instance().getTickets().nextId();
    }
}
