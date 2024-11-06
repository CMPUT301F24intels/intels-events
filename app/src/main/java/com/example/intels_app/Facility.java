package com.example.intels_app;

public class Facility {
    private String facilityName;
    private String location;
    private String organizerName;
    private String email;
    private int telephone;
    private String imageUrl;
    private String deviceID;

    // No argument constructor for Firebase
    public Facility() {}

    // Constructor with all fields except imageUrl
    public Facility(String facilityName, String location, String organizerName, String email, int telephone, String deviceID) {
        this.facilityName = facilityName;
        this.location = location;
        this.organizerName = organizerName;
        this.email = email;
        this.telephone = telephone;
        this.deviceID = deviceID;
    }


    public Facility(String facilityName, String location, String organizerName, String email, int telephone, String imageUrl, String deviceID) {
        this.facilityName = facilityName;
        this.location = location;
        this.organizerName = organizerName;
        this.email = email;
        this.telephone = telephone;
        this.imageUrl = imageUrl;
        this.deviceID = deviceID;
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

    public int getTelephone() {
        return telephone;
    }

    public void setTelephone(int telephone) {
        this.telephone = telephone;
    }

    public String getPosterUrl() {
        return imageUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.imageUrl = posterUrl;
    }
}
