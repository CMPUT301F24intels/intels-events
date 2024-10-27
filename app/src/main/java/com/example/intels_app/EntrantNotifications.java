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
