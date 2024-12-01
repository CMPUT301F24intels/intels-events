package com.example.intels_app;

/**
 * This class represents an entrant with a name, notifications, and an optional profile image.
 * It allows for storing the entrant's name, managing their notification preferences, and setting
 * their profile image URL.
 * @author Aayushi Shah
 * @see com.example.intels_app.Event Event related to the entrant
 */

public class Entrant {
    private String name;
    private EntrantNotifications notifications;
    private String imageUrl;

    public Entrant(String name) {
        this.name = name;
        this.notifications = new EntrantNotifications();
    }

    public Entrant(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public EntrantNotifications getNotifications() {
        return notifications;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

