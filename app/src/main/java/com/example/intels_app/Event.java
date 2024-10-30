package com.example.intels_app;

public class Event {
    private String id;
    private String eventName;
    private String facilityName;
    private String location;
    private String date;
    private String time;
    private String description;
    private int maxAttendees;
    private boolean geolocationRequirement;
    private boolean notifPreference;

    // Add pfp
    // https://developer.android.com/training/data-storage/shared

    public Event(String id, String name) {
        this.id = id;
        this.eventName = name;
    }

    public String getId() {
        return id;
    }

    public String getEventName() {
        return eventName;
    }
}
