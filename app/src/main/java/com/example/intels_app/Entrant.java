package com.example.intels_app;

public class Entrant {
    private String name;
    private EntrantNotifications notifications;

    public Entrant(String name) {
        this.name = name;
        this.notifications = new EntrantNotifications();
    }

    public String getName() {
        return name;
    }

    public EntrantNotifications getNotifications() {
        return notifications;
    }
}

