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

