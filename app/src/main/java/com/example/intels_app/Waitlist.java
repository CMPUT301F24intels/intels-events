package com.example.intels_app;

import java.util.ArrayList;
import java.util.List;

public class Waitlist {

    private List<Entrant> entrants = new ArrayList<>();

    public void addEntrant(Entrant entrant) {
        if (!entrants.contains(entrant)) {
            entrants.add(entrant);
        }
    }

    public List<Entrant> getEntrants() {
        return entrants;
    }

    public void removeEntrant(Entrant entrant) {
        entrants.remove(entrant);
    }
}