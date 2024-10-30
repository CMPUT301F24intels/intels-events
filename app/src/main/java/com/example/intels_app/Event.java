package com.example.intels_app;

import android.widget.ImageView;

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
    private ImageView poster;
    private ImageView qrCode;

    // Constructor
    public Event(String eventName, String facilityName, String location, String date, String time, String description,
                 int maxAttendees, boolean geolocationRequirement, boolean notifPreference, ImageView poster, ImageView qrCode) {
        this.eventName = eventName;
        this.facilityName = facilityName;
        this.location = location;
        this.date = date;
        this.time = time;
        this.description = description;
        this.maxAttendees = maxAttendees;
        this.geolocationRequirement = geolocationRequirement;
        this.notifPreference = notifPreference;
        this.poster = poster;
        this.qrCode = qrCode;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    // Add poster
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
