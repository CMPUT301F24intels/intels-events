package com.example.intels_app;

import android.widget.ImageView;

public class Event {
    private String eventName;
    private String facilityName;
    private String location;
    private String dateTime;
    private String description;
    private int maxAttendees;
    private boolean geolocationRequirement;
    private boolean notifPreference;
    private ImageView poster;
    private ImageView qrCode;

    // Constructor
    public Event(String eventName, String facilityName, String location, String dateTime, String description,
                 int maxAttendees, boolean geolocationRequirement, boolean notifPreference) {
        this.eventName = eventName;
        this.facilityName = facilityName;
        this.location = location;
        this.dateTime = dateTime;
        this.description = description;
        this.maxAttendees = maxAttendees;
        this.geolocationRequirement = geolocationRequirement;
        this.notifPreference = notifPreference;
        //this.poster = poster;
        //this.qrCode = qrCode;
    }

    // Getters and setters
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    public boolean isGeolocationRequirement() {
        return geolocationRequirement;
    }

    public void setGeolocationRequirement(boolean geolocationRequirement) {
        this.geolocationRequirement = geolocationRequirement;
    }

    public boolean isNotifPreference() {
        return notifPreference;
    }

    public void setNotifPreference(boolean notifPreference) {
        this.notifPreference = notifPreference;
    }

    public ImageView getPoster() {
        return poster;
    }

    public void setPoster(ImageView poster) {
        this.poster = poster;
    }

    public ImageView getQrCode() {
        return qrCode;
    }

    public void setQrCode(ImageView qrCode) {
        this.qrCode = qrCode;
    }

    public void addEvent() {
        // Add event to database
        // https://firebase.google.com/docs/firestore/manage-data/add-data
    }

    // Add poster
    // https://developer.android.com/training/data-storage/shared

}
