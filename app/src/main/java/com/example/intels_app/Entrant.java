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

    /**
     * Constructor for creating an entrant with a name.
     * Initializes the entrant's notifications with a new instance of {@link EntrantNotifications}.
     * @param name The name of the entrant.
     */
    public Entrant(String name) {
        this.name = name;
        this.notifications = new EntrantNotifications();
    }

    /**
     * Constructor for creating an entrant with a name and image URL.
     * This constructor is used when there is an associated image for the entrant.
     * @param name     The name of the entrant.
     * @param imageUrl The URL of the entrant's profile image.
     */
    public Entrant(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the name of the entrant.
     * @return The name of the entrant.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the notifications for the entrant.
     * @return An instance of {@link EntrantNotifications} containing the entrant's notifications.
     */
    public EntrantNotifications getNotifications() {
        return notifications;
    }

    /**
     * Gets the URL of the entrant's profile image.
     * @return The URL of the entrant's profile image, or null if not set.
     */
    public String getImageUrl() {
        return imageUrl;
    }
}

