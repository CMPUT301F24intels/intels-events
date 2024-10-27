package com.example.intels_app;

public class OrganizerNotifications {

    public static final String not_chosen = "Not Chosen from Waitlist";

    public void sendNotification(Entrant entrant, String message) {
        entrant.getNotifications().addNotification(message);
    }
}
