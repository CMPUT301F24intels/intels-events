/**
 * This class displays the notification when not chosen
 * from a waitlist.
 * @author Katrina Alejo
 * @see com.example.intels_app.Entrant Entrant object
 */

package com.example.intels_app;

public class OrganizerNotifications {

    public static final String not_chosen = "Not Chosen from Waitlist";

    public void sendNotification(Entrant entrant, String message) {
        entrant.getNotifications().addNotification(message);
    }
}
