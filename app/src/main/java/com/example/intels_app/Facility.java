package com.example.intels_app;
/**
 * Represents a facility with its details.
 * @author Janan Panchal
 */

public class Facility {
    private String facilityName;
    private String location;
    private String organizerName;
    private String email;
    private String telephone;
    private String imageUrl;
    private String deviceID;

    /**
     * No argument constructor for Firebase
     */
    public Facility() {}

    /**
     * To create a Facility with no Facility image
     * @param facilityName Name of the facility
     * @param location Location of the facility
     * @param email Email of the facility
     * @param telephone Telephone number of the facility
     * @param deviceID Device ID of the facility
     */
    public Facility(String facilityName, String location, String email, String telephone, String deviceID) {
        this.facilityName = facilityName;
        this.location = location;
        this.email = email;
        this.telephone = telephone;
        this.deviceID = deviceID;
    }

    /**
     * Create a Facility with a Facility image
     * @param facilityName Name of the facility
     * @param location Location of the facility
     * @param email Email of the facility
     * @param telephone Telephone number of the facility
     * @param deviceID Device ID of the facility
     * @param imageUrl Image URL of the facility
     */
    public Facility(String facilityName, String location, String email, String telephone, String imageUrl, String deviceID) {
        this.facilityName = facilityName;
        this.location = location;
        this.email = email;
        this.telephone = telephone;
        this.imageUrl = imageUrl;
        this.deviceID = deviceID;
    }

    /**
     * Getter for facility name
     */
    public String getFacilityName() {
        return facilityName;
    }

    /**
     * Setter for facility name
     * @param facilityName Name of the facility
     */
    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    /**
     * Getter for location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Setter for facility location
     * @param location location of facility
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Getter for organizer name
     */
    public String getOrganizerName() {
        return organizerName;
    }

    /**
     * Setter for organizer name
     * @param organizerName organizer name
     */
    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    /**
     * Getter for facility email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter for facility email
     * @param email facility email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter for facility phone number
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * Setter for facility phone number
     * @param telephone facility phone number
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * Getter for Facility image
     */
    public String getFacilityImageUrl() {
        return imageUrl;
    }

    /**
     * Setter for facility image
     * @param posterUrl facility image
     */
    public void setFacilityImageUrl(String posterUrl) {
        this.imageUrl = posterUrl;
    }

    /**
     * Getter for device ID
     */
    public String getDeviceId() {
        return deviceID;
    }

    /**
     * Setter for device ID
     * @param deviceID device ID
     */
    public void setDeviceId(String deviceID) {
        this.deviceID = deviceID;
    }
}
