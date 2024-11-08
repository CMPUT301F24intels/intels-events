/**
 * This class represents a notification with a title and message, for sending
 * and displaying notifications within the app.
 * @author Katrina Alejo, Aayushi Shah
 */

package com.example.intels_app;

public class Notification {
    private String title;
    private String message;

    public Notification() {
        // Default constructor required for calls to DataSnapshot.getValue(Notification.class)
    }

    public Notification(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
