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

import io.stallion.email.ContactableEmailer;
import io.stallion.users.User;

import java.util.Map;


public abstract class ContactEmailer extends ContactableEmailer {

    private Contact contact;

    public ContactEmailer(Contact contact) {
        super(contactToUser(contact));
        this.contact = contact;
        put("contact", contact);
    }

    public ContactEmailer(Contact contact, Map<String, Object> context) {
        super(contactToUser(contact), context);
        this.contact = contact;
        put("contact", contact);
    }

    private static User contactToUser(Contact contact) {
        User user = new User()
                .setEmail(contact.getEmail())
                .setDisplayName(contact.getDisplayName())
                .setGivenName(contact.getGivenName())
                .setFamilyName(contact.getFamilyName())
                ;
        return user;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
