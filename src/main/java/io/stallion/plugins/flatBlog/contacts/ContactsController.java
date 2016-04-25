package io.stallion.plugins.flatBlog.contacts;


import io.stallion.Context;
import io.stallion.dal.DalRegistry;
import io.stallion.dal.base.DalRegistration;
import io.stallion.dal.base.SelfGeneratingId;
import io.stallion.dal.base.StandardModelController;
import io.stallion.dal.file.JsonFilePersister;
import io.stallion.utils.GeneralUtils;

import static io.stallion.utils.Literals.*;


public class ContactsController extends StandardModelController<Contact> implements SelfGeneratingId<Contact> {

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
