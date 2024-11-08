/**
 * This class manages a list of notifications for an entrant.
 * This class allows notifications to be added and retrieved,
 * providing a simple structure for handling messages related
 * to an entrant.
 * @author Katrina Alejo
 */

package com.example.intels_app;

import java.util.ArrayList;
import java.util.List;

public class EntrantNotifications {

    private List<String> notifications = new ArrayList<>();

    public void addNotification(String message) {
        notifications.add(message);
    }

    public List<String> getNotifications() {
        return notifications;
    }
}
