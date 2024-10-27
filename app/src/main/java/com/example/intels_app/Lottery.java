package com.example.intels_app;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Lottery {
    private Waitlist waitlist;
    private OrganizerNotifications notifications;

    public Lottery(Waitlist waitlist, OrganizerNotifications notifications) {
        this.waitlist = waitlist;
        this.notifications = notifications;
    }

    public List<Entrant> runLottery(int numberToSelect) {
        List<Entrant> selectedEntrants = new ArrayList<>();
        Random random = new Random();

        List<Entrant> entrants = new ArrayList<>(waitlist.getEntrants());
        while (selectedEntrants.size() < numberToSelect && !entrants.isEmpty()) {
            int index = random.nextInt(entrants.size());
            Entrant selected = entrants.remove(index);
            selectedEntrants.add(selected);
        }

        // Notify entrants who were not chosen
        for (Entrant entrant : entrants) {
            notifications.sendNotification(entrant, "You were not selected in the recent draw.");
        }

        return selectedEntrants;
    }
}
