package io.stallion.plugins.flatBlog.contacts;


public class NotificationCallbackResult {
    private String completeEmailSubject = "";
    private String emailBody = "";
    private String thing = "";
    private String thingPlural = "";

    public String getCompleteEmailSubject() {
        return completeEmailSubject;
    }

    public NotificationCallbackResult setCompleteEmailSubject(String completeEmailSubject) {
        this.completeEmailSubject = completeEmailSubject;
        return this;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public NotificationCallbackResult setEmailBody(String emailBody) {
        this.emailBody = emailBody;
        return this;
    }

    public String getThing() {
        return thing;
    }

    public NotificationCallbackResult setThing(String thing) {
        this.thing = thing;
        return this;
    }

    public String getThingPlural() {
        return thingPlural;
    }

    public NotificationCallbackResult setThingPlural(String thingPlural) {
        this.thingPlural = thingPlural;
        return this;
    }
}
