/**
 * This class represents an event and contains details such as the
 * event name, facility name, location, date and time, description,
 * and other settings. This class is designed to be used with Firebase
 * Firestore for storing event information.
 * @author Aayushi Shah
 */

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
    private String posterUrl;
    private String qrCodeUrl;
    private String deviceId;

    // No argument constructor for Firebase
    public Event() {}

    // Constructor with posterUrl
    public Event(String eventName, String facilityName, String location, String dateTime, String description,
                 int maxAttendees, boolean geolocationRequirement, String posterUrl, String deviceId) {
        this.eventName = eventName;
        this.facilityName = facilityName;
        this.location = location;
        this.dateTime = dateTime;
        this.description = description;
        this.maxAttendees = maxAttendees;
        this.geolocationRequirement = geolocationRequirement;
        this.posterUrl = posterUrl;
        this.deviceId = deviceId;
        //this.qrCode = qrCode;
    }

    // Constructor with no images
    public Event(String eventName, String facilityName, String location, String dateTime, String description,
                 int maxAttendees, boolean geolocationRequirement, String deviceId) {
        this.eventName = eventName;
        this.facilityName = facilityName;
        this.location = location;
        this.dateTime = dateTime;
        this.description = description;
        this.maxAttendees = maxAttendees;
        this.geolocationRequirement = geolocationRequirement;
        this.deviceId = deviceId;
        //this.posterUrl = posterUrl;
        //this.qrCode = qrCode;
    }

    // Constructor without facility name for event creation
    public Event(String EventName, String Location, String DateTime, String Description, int MaxAttendees, boolean GeolocationRequirement, String PosterUrl, String deviceId) {
        this.eventName = EventName;
        this.location = Location;
        this.dateTime = DateTime;
        this.description = Description;
        this.maxAttendees = MaxAttendees;
        this.geolocationRequirement = GeolocationRequirement;
        this.posterUrl = PosterUrl;
        this.deviceId = deviceId;
    }

    // Constructor without facility name for event creation
    public Event(String EventName, String Location, String DateTime, String Description, int MaxAttendees, boolean GeolocationRequirement, String PosterUrl, String QrCodeUrl, String deviceId) {
        this.eventName = EventName;
        this.location = Location;
        this.dateTime = DateTime;
        this.description = Description;
        this.maxAttendees = MaxAttendees;
        this.geolocationRequirement = GeolocationRequirement;
        this.posterUrl = PosterUrl;
        this.qrCodeUrl = QrCodeUrl;
        this.deviceId = deviceId;
    }

    // Getters and setters

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
