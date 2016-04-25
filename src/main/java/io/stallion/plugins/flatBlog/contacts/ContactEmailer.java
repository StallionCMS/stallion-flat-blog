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
