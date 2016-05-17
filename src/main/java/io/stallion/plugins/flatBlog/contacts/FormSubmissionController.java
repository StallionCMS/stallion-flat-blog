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
import io.stallion.dataAccess.DataAccessRegistration;

import io.stallion.dataAccess.StandardModelController;

import io.stallion.dataAccess.file.JsonFilePersister;


public class FormSubmissionController extends StandardModelController<FormSubmission>{

    public static FormSubmissionController instance() {
        return (FormSubmissionController) Context.dal().get("form_submissions");
    }



    public static void register() {
        DataAccessRegistration registration = new DataAccessRegistration()
                .setModelClass(FormSubmission.class)
                .setControllerClass(FormSubmissionController.class)
                .setShouldWatch(false)
                .setUseDataFolder(true)
                .setNameSpace("")
                .setWritable(true)
                .setPath("form_submissions")
                .setPersisterClass(JsonFilePersister.class);
        Context.dal().register(registration);
    }


}

