package com.example.intels_app;

import java.util.ArrayList;
import java.util.List;
/**
 * This class manages a list of notifications for an entrant.
 * This class allows notifications to be added and retrieved,
 * providing a simple structure for handling messages related
 * to an entrant.
 * @author Katrina Alejo
 */

public class EntrantNotifications {

    private List<String> notifications = new ArrayList<>();

    /**
     * Adds a notification message to the list of notifications.
     * @param message The notification message to be added.
     */
    public void addNotification(String message) {
        notifications.add(message);
    }

    /**
     * Retrieves the list of notifications for the entrant.
     * @return A list of notification messages.
     */
    public List<String> getNotifications() {
        return notifications;
    }
}
