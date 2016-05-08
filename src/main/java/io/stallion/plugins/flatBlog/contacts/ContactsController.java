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
import io.stallion.dal.DalRegistry;
import io.stallion.dal.base.DalRegistration;

import io.stallion.dal.base.StandardModelController;
import io.stallion.dal.file.JsonFilePersister;
import io.stallion.utils.GeneralUtils;

import static io.stallion.utils.Literals.*;


public class ContactsController extends StandardModelController<Contact>{

    public static ContactsController instance() {
        return (ContactsController) Context.dal().get("contacts");
    }

    public Contact getOrCreate(Contact contact) {
        Contact existing = forUniqueKey("email", contact.getEmail());
        if (!empty(existing)) {
            return existing;
        }
        save(contact);
        return contact;
    }


    @Override
    public void onPreSavePrepare(Contact contact) {
        if (empty(contact.getSecretToken())) {
            contact.setSecretToken(GeneralUtils.randomToken(20));
        }

    }


    public static void register() {
        DalRegistration registration = new DalRegistration()
                .setModelClass(Contact.class)
                .setControllerClass(ContactsController.class)
                .setShouldWatch(false)
                .setUseDataFolder(true)
                .setNameSpace("")
                .setWritable(true)
                .setPath("contacts")
                .setPersisterClass(JsonFilePersister.class);
        Context.dal().registerDal(registration);
    }


    public Long generateId(Contact obj) {
        return DalRegistry.instance().getTickets().nextId();
    }
}
