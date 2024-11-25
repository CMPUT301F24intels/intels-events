/**
 * Represents a facility with its details.
 * @author Janan Panchal
 */
package com.example.intels_app;

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

    // Getters and setters
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

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getFacilityImageUrl() {
        return imageUrl;
    }

    public void setFacilityImageUrl(String posterUrl) {
        this.imageUrl = posterUrl;
    }

    public String getDeviceId() {
        return deviceID;
    }

    public void setDeviceId(String deviceID) {
        this.deviceID = deviceID;
    }
}
