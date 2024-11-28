/**
 * The {Entrant} class represents anyone signing up for an event or using the
 * app as an entrant. Each entrant has a name and is associated to a Entrant
 * Notification object that will handle notifications for the specific entrant.
 * @author Aayushi Shah
 * @see com.example.intels_app.EntrantNotifications Entrant notifications
 */

package com.example.intels_app;

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

