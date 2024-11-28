/**
 * This class represents a user profile with details such as name, email,
 * phone number, profile image, and device ID. Along with multiple constructors
 * it includes getter and setter methods for each field.
 * @author Aayushi Shah
 * @see com.google.firebase.firestore.FirebaseFirestore Firebase
 */

package com.example.intels_app;

 class Profile {
    private String name;
    private String email;
    private String phone_number;
    private int imageResId;
    private String imageUrl;
    private String deviceId;
    private boolean notifPref;
    /*private String eventName;*/


    public Profile(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public Profile(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Profile(String name, String email, String phone_number, String imageUrl) {
        this.name = name;
        this.email = email;
        this.phone_number = phone_number;
        this.imageUrl = imageUrl;
    }
    //Constructor without profile pic
    public Profile(String name, String email, String phone_number) {
        this.name = name;
        this.email = email;
        this.phone_number = phone_number;
    }

    public Profile(String deviceId, String name, String email, String phone_number, String imageUrl) {
        this.deviceId = deviceId;
        this.name = name;
        this.email = email;
        this.phone_number = phone_number;
        this.imageUrl = imageUrl;
    }

     public Profile(String deviceId, String name, String email, String phone_number, String imageUrl, boolean notifPref) {
         this.deviceId = deviceId;
         this.name = name;
         this.email = email;
         this.phone_number = phone_number;
         this.imageUrl = imageUrl;
         this.notifPref = notifPref;
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

    /*public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }*/

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

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

     public boolean isNotifPref() {
         return notifPref;
     }

     public void setNotifPref(boolean notifPref) {
        this.notifPref = notifPref;
     }
 }
