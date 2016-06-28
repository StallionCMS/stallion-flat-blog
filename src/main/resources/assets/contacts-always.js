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

/**
*  Contact form handling. Should be included on every page.
*/
(function() {
    var contacts = {};

    window.stallion_plugin_contacts = contacts;

    contacts.init = function() {
        $('.st-contacts-form').submit(contacts.onSubmitHandler);
        stallion.autoGrow({}, $('.st-contacts-form textarea'));
    };

    contacts.onSubmitHandler = function(event, b, c) {
        event.preventDefault(event);
        var form = this;
        console.log('handleSubmit! ', this, event, b, c);
        var submission = {
            data: stallion.formToData(form),
            pageUrl: $("link[rel='canonical']").attr('href') || window.location.href,
            pageTitle: document.title
        };
        submission.email = submission.data.email;

        var url = '/_stx/flatBlog/contacts/submit-form';

        stallion.request({
            url: url,
            method: 'post',
            form: form,
            data: submission,
            success: function(o) {
                $form = $(form);
                if ($form.data("redirectUrl")) {
                    window.location.href = $(form).data("redirectUrl");
                } else if ($form.find(".st-form-success").length) {
                    $success = $form.find(".st-form-success").remove();
                    $form.after($success);
                    $form.hide();
                    $success.show();
                } else if ($form.data("successMessage")) {
                    $form.hide();
                    $form.after("<h3>" + $form.data("successMessage") + "</h3>");
                } else {
                    $form.hide();
                    $form.after($("<h3>Your information has been submitted!</h3>"));
                }
            }
        });
    };
    $(document).ready(contacts.init);

}());
