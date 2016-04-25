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

import io.stallion.services.Log;
import io.stallion.settings.Settings;
import io.stallion.utils.GeneralUtils;
import org.apache.commons.lang3.StringUtils;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.stallion.utils.Literals.*;


public class NotificationEmailer extends ContactEmailer {
    private String subject;
    private List<NotificationCallbackResult> results;

    public NotificationEmailer(Contact contact, List<NotificationCallbackResult> results) {
        super(contact);
        if(empty(contact.getSecretToken())) {
            contact.setSecretToken(GeneralUtils.randomToken(20));
            ContactsController.instance().save(contact);
        }
        this.results = results;
        put("results", results);
        build();
    }

    private void build() {
        int totalCount = 0;

        String siteName = Settings.instance().getSiteName();
        if (siteName.length() > 25) {
            try {
                siteName = new URL(Settings.instance().getSiteUrl()).getHost();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }


        Map<String, Integer> counts = map();
        Map<String, String> singleToPlural = map();

        for(NotificationCallbackResult result: results) {
            counts.put(result.getThing(), counts.getOrDefault(result.getThing(), 0) + 1);
            singleToPlural.put(result.getThing(), result.getThingPlural());
        }

        StringBuffer subjectBuilder = new StringBuffer();
        if (results.size() > 1) {
            subjectBuilder.append(results.size() + " updates from \"" + siteName + "\": ");
        } else {
            subjectBuilder.append("Update from \"" + siteName + "\": ");
        }
        for (Map.Entry<String, Integer> entry: counts.entrySet()) {
            if (entry.getValue() > 1) {
                subjectBuilder.append(entry.getValue() + " " + singleToPlural.get(entry.getKey()));
            } else {
                subjectBuilder.append("1 " + entry.getKey());
            }
            subjectBuilder.append(", ");
        }
        subject = subjectBuilder.toString();
        subject = StringUtils.strip(StringUtils.strip(subject, " "), ",");
    }

    @Override
    public boolean isTransactional() {
        return false;
    }

    @Override
    public String getTemplate() {
        URL url = getClass().getResource("/templates/contact-notification.jinja");
        Log.info("Template URL: {0} other={1}", url, getClass().getResource("templates/contact-notification.jinja"));
        return url.toString();

    }

    @Override
    public String getSubject() {
        return subject;
    }


}

