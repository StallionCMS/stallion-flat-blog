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

import io.stallion.email.ContactableEmailer;
import io.stallion.users.User;
import static io.stallion.utils.Literals.*;

public class FormSubmissionEmailer extends ContactableEmailer {
    private FormSubmission submission;
    private Contact contact;

    public FormSubmissionEmailer(User user, FormSubmission submission, Contact contact) {
        super(user);
        this.contact = contact;
        this.submission = submission;
        put("contact", contact);
        put("submission", submission);
    }

    @Override
    public boolean isTransactional() {
        return false;
    }

    @Override
    public String getTemplate() {
        return getClass().getResource("/templates/form-submission-notify.jinja").toString();
    }

    @Override
    public String getSubject() {
        return "New form submission: " + or(contact.getEmail(), "(no email)") + " on page " + or(submission.getPageTitle(), "(no page title)");
    }

}

