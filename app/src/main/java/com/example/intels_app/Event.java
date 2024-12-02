package com.example.intels_app;

import java.io.Serializable;
/**
 * This class represents an event and contains details such as the
 * event name, facility name, location, date and time, description,
 * and other settings. This class is designed to be used with Firebase
 * Firestore for storing event information.
 * @author Aayushi Shah
 */
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

    /**
     * Constructor for use with Firebase.
     */
    public Event() {}

    /**
     * Constructor to create an Event with a poster URL.
     * @param eventName            The name of the event.
     * @param facilityName         The facility name associated with the event.
     * @param location             The event location.
     * @param dateTime             The event date and time.
     * @param description          The event description.
     * @param maxAttendees         The maximum number of attendees allowed.
     * @param geolocationRequirement Whether the event requires geolocation verification.
     * @param posterUrl            The URL for the event poster.
     * @param deviceId             The device ID of the event creator.
     */
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

    /**
     * Constructor to create an Event without images.
     * @param eventName            The name of the event.
     * @param facilityName         The facility name associated with the event.
     * @param location             The event location.
     * @param dateTime             The event date and time.
     * @param description          The event description.
     * @param maxAttendees         The maximum number of attendees allowed.
     * @param geolocationRequirement Whether the event requires geolocation verification.
     * @param deviceId             The device ID of the event creator.
     */
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

    /**
     * Constructor for event creation without facility name.
     * @param EventName            The name of the event.
     * @param Location             The event location.
     * @param DateTime             The event date and time.
     * @param Description          The event description.
     * @param MaxAttendees         The maximum number of attendees allowed.
     * @param GeolocationRequirement Whether the event requires geolocation verification.
     * @param PosterUrl            The URL for the event poster.
     * @param deviceId             The device ID of the event creator.
     */
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

    /**
     * Constructor for event creation without facility name, with QR code URL.
     * @param EventName            The name of the event.
     * @param Location             The event location.
     * @param DateTime             The event date and time.
     * @param Description          The event description.
     * @param MaxAttendees         The maximum number of attendees allowed.
     * @param GeolocationRequirement Whether the event requires geolocation verification.
     * @param PosterUrl            The URL for the event poster.
     * @param QrCodeUrl            The URL for the event QR code.
     * @param deviceId             The device ID of the event creator.
     */
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

    /**
     * Retrieves the device ID of the event creator.
     * @return The device ID of the event creator.
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the device ID of the event creator.
     * @param deviceId The device ID of the event creator.
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Retrieves the unique ID of the event.
     * @return The unique ID of the event.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique ID of the event.
     * @param id The unique ID of the event.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Retrieves the name of the event.
     * @return The name of the event.
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Sets the name of the event.
     * @param eventName The name of the event.
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Retrieves the facility name associated with the event.
     * @return The facility name associated with the event.
     */
    public String getFacilityName() {
        return facilityName;
    }

    /**
     * Sets the facility name associated with the event.
     * @param facilityName The facility name associated with the event.
     */
    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    /**
     * Retrieves the location of the event.
     * @return The location of the event.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location of the event.
     * @param location The location of the event.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Retrieves the date and time of the event.
     * @return The date and time of the event.
     */
    public String getDateTime() {
        return dateTime;
    }

    /**
     * Sets the date and time of the event.
     * @param dateTime The date and time of the event.
     */
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Retrieves the description of the event.
     * @return The description of the event.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the event.
     * @param description The description of the event.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retrieves the maximum number of attendees allowed for the event.
     * @return The maximum number of attendees.
     */
    public int getMaxAttendees() {
        return maxAttendees;
    }

    /**
     * Sets the maximum number of attendees allowed for the event.
     * @param maxAttendees The maximum number of attendees.
     */
    public void setMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    /**
     * Checks if the event requires geolocation verification.
     * @return True if the event requires geolocation verification, false otherwise.
     */
    public boolean isGeolocationRequirement() {
        return geolocationRequirement;
    }

    /**
     * Sets whether the event requires geolocation verification.
     * @param geolocationRequirement True if the event requires geolocation verification.
     */
    public void setGeolocationRequirement(boolean geolocationRequirement) {
        this.geolocationRequirement = geolocationRequirement;
    }

    /**
     * Retrieves the URL of the event poster.
     * @return The URL of the event poster.
     */
    public String getPosterUrl() {
        return posterUrl;
    }

    /**
     * Sets the URL of the event poster.
     * @param posterUrl The URL of the event poster.
     */
    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    /**
     * Retrieves the URL of the event QR code.
     * @return The URL of the event QR code.
     */
    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    /**
     * Sets the URL of the event QR code.
     * @param qrCodeUrl The URL of the event QR code.
     */
    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    /**
     * Constructor for initializing an Event with ID and name.
     * @param id   The unique ID of the event.
     * @param name The name of the event.
     */
    public Event(String id, String name) {
        this.id = id;
        this.eventName = name;
    }

}
