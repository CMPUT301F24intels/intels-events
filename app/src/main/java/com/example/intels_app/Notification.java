package com.example.intels_app;
/**
 * This class represents a notification with a title and message, for sending
 * and displaying notifications within the app.
 * @deprecated
 * @author Katrina Alejo, Aayushi Shah
 */
public class Notification {
    private String title;
    private String message;
    private String deviceId;

    public Notification() {
        // Default constructor required for calls to DataSnapshot.getValue(Notification.class)
    }

    public Notification(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public Notification(String title, String message, String deviceId) {
        this.title = title;
        this.message = message;
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
