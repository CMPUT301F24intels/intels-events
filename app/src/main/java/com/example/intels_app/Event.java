package com.example.intels_app;

import java.io.Serializable;

public class Event implements Serializable {
    private String id;
    private String eventName;
    private String facilityName;
    private String location;
    private String dateTime;
    private String description;
    private int maxAttendees;
    private boolean geolocationRequirement;
    private boolean notifPreference;
    private String posterUrl;
    private String qrCodeUrl;

    // No argument constructor for Firebase
    public Event() {}

    // Constructor with posterUrl
    public Event(String eventName, String facilityName, String location, String dateTime, String description,
                 int maxAttendees, boolean geolocationRequirement, boolean notifPreference, String posterUrl) {
        this.eventName = eventName;
        this.facilityName = facilityName;
        this.location = location;
        this.dateTime = dateTime;
        this.description = description;
        this.maxAttendees = maxAttendees;
        this.geolocationRequirement = geolocationRequirement;
        this.notifPreference = notifPreference;
        this.posterUrl = posterUrl;
        //this.qrCode = qrCode;
    }

    // Constructor with no images
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
        //this.posterUrl = posterUrl;
        //this.qrCode = qrCode;
    }

    // Getters and setters

    public String getId() {
        return id;
    }

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

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    // Getter and Setter for qrCodeUrl
    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public void addEvent() {
        // Add event to database
        // https://firebase.google.com/docs/firestore/manage-data/add-data
    }

    // Add poster
    // https://developer.android.com/training/data-storage/shared

    public Event(String id, String name) {
        this.id = id;
        this.eventName = name;
    }

}
