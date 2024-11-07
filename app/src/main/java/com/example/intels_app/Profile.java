package com.example.intels_app;

 class Profile {
    private String name;
    private String email;
    private int phone_number;
    private int imageResId;
    private String imageUrl;
    private String deviceId;
    private String eventName;



    public Profile(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public Profile(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Profile(String name, String email, int phone_number, String imageUrl) {
        this.name = name;
        this.email = email;
        this.phone_number = phone_number;
        this.imageUrl = imageUrl;
    }
    //Constructor without profile pic
    public Profile(String name, String email, int phone_number) {
        this.name = name;
        this.email = email;
        this.phone_number = phone_number;
    }

    public Profile(String deviceId, String name, String email, int phone_number) {
        this.deviceId = deviceId;
        this.name = name;
        this.email = email;
        this.phone_number = phone_number;
    }

    public Profile(String deviceId, String name, String email, int phone_number, String imageUrl) {
        this.deviceId = deviceId;
        this.name = name;
        this.email = email;
        this.phone_number = phone_number;
        this.imageUrl = imageUrl;
    }

    public Profile(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    // Default constructor required for Firestore
    public Profile() {}

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventId(String eventName) {
        this.eventName = eventName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(int phone_number) {
        this.phone_number = phone_number;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
