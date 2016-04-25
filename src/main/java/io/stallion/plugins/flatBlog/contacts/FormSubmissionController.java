package io.stallion.plugins.flatBlog.contacts;

import io.stallion.Context;
import io.stallion.dal.base.DalRegistration;

import io.stallion.dal.base.StandardModelController;
import io.stallion.dal.base.SelfGeneratingId;
import io.stallion.dal.file.JsonFilePersister;


public class FormSubmissionController extends StandardModelController<FormSubmission> implements SelfGeneratingId<FormSubmission> {

    public static FormSubmissionController instance() {
        return (FormSubmissionController) Context.dal().get("form_submissions");
    }



    public static void register() {
        DalRegistration registration = new DalRegistration()
                .setModelClass(FormSubmission.class)
                .setControllerClass(FormSubmissionController.class)
                .setShouldWatch(false)
                .setUseDataFolder(true)
                .setNameSpace("")
                .setWritable(true)
                .setPath("form_submissions")
                .setPersisterClass(JsonFilePersister.class);
        Context.dal().registerDal(registration);
    }


}

