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

package io.stallion.plugins.flatBlog.comments;

import io.stallion.exceptions.ClientException;
import io.stallion.plugins.flatBlog.contacts.Contact;
import io.stallion.plugins.flatBlog.contacts.ContactsController;
import io.stallion.restfulEndpoints.MinRole;
import io.stallion.settings.Settings;
import io.stallion.users.Role;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.Map;

import static io.stallion.utils.Literals.*;
import static io.stallion.Context.*;


public class SeleniumEndpoints {

    @Path("/selenium/get-contact-secret")
    @GET
    @Produces("application/json")
    @MinRole(Role.ANON)
    public Map getContactSecret(@QueryParam("secret") String secret, @QueryParam("email") String email) {
        if (!Settings.instance().getHealthCheckSecret().equals(secret)) {
            throw new ClientException("Invalid secret");
        }
        if (!email.startsWith("selenium+") || !email.endsWith("@stallion.io")) {
            throw new ClientException("Invalid email address");
        }
        Contact contact = ContactsController.instance().forUniqueKey("email", email);
        return map(val("secret", contact.getSecretToken()));
    }
}
