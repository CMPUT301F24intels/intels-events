package com.example.intels_app;

/**
 * This class represents a user profile with details such as name, email,
 * phone number, profile image, and device ID. Along with multiple constructors
 * it includes getter and setter methods for each field.
 * @author Aayushi Shah, Dhanshri Patel
 * @see com.google.firebase.firestore.FirebaseFirestore Firebase
 */

class Profile {
    private String name;
    private String email;
    private String phone_number;
    private int imageResId;
    private String imageUrl;
    private String deviceId;
    private boolean notifPref;
    /*private String eventName;*/

    /**
     * Constructor to create a Profile with name and image resource ID.
     * @param name      The name of the profile holder.
     * @param imageResId The resource ID of the profile picture.
     */
    public Profile(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    /**
     * Constructor to create a Profile with name, email, phone number, and image URL.
     * @param name       The name of the profile holder.
     * @param email      The email of the profile holder.
     * @param phone_number The phone number of the profile holder.
     * @param imageUrl   The URL of the profile picture.
     */
    public Profile(String name, String email, String phone_number, String imageUrl) {
        this.name = name;
        this.email = email;
        this.phone_number = phone_number;
        this.imageUrl = imageUrl;
    }

    /**
     * Constructor to create a Profile without a profile picture.
     * @param name       The name of the profile holder.
     * @param email      The email of the profile holder.
     * @param phone_number The phone number of the profile holder.
     */
    public Profile(String name, String email, String phone_number) {
        this.name = name;
        this.email = email;
        this.phone_number = phone_number;
    }

    /**
     * Constructor to create a Profile with device ID, name, email, phone number, and image URL.
     * @param deviceId   The unique identifier for the device.
     * @param name       The name of the profile holder.
     * @param email      The email of the profile holder.
     * @param phone_number The phone number of the profile holder.
     * @param imageUrl   The URL of the profile picture.
     */
    public Profile(String deviceId, String name, String email, String phone_number, String imageUrl) {
        this.deviceId = deviceId;
        this.name = name;
        this.email = email;
        this.phone_number = phone_number;
        this.imageUrl = imageUrl;
    }

    /**
     * Constructor to create a Profile with device ID, name, email, phone number, image URL, and notification preference.
     * @param deviceId   The unique identifier for the device.
     * @param name       The name of the profile holder.
     * @param email      The email of the profile holder.
     * @param phone_number The phone number of the profile holder.
     * @param imageUrl   The URL of the profile picture.
     * @param notifPref  The notification preference of the profile holder.
     */
    public Profile(String deviceId, String name, String email, String phone_number, String imageUrl, boolean notifPref) {
         this.deviceId = deviceId;
         this.name = name;
         this.email = email;
         this.phone_number = phone_number;
         this.imageUrl = imageUrl;
         this.notifPref = notifPref;
     }

    /**
     * Constructor to create a Profile with name and image URL.
     * @param name     The name of the profile holder.
     * @param imageUrl The URL of the profile picture.
     */
     public Profile(String name, String imageUrl) {
         this.name = name;
         this.imageUrl = imageUrl;
     }

    /**
     * Constructor to create a Profile with device ID.
     * @param deviceId The unique identifier for the device.
     */
     public Profile(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Default constructor required for Firestore deserialization.
     */
    public Profile() {}

    /**
     * Sets the device id for profile
     * @param deviceId deviceID for profile
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Sets the name of the profile
     * @param name name of profile holder
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets ImageResId for profile
     * @param imageResId
     */
    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    /**
     * Gets the name of the profile
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the ImageResId for the profile
     */
    public int getImageResId() {
        return imageResId;
    }

    /**
     * Gets the email of profile as a string
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the profile
     * @param email email of profile
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the phone number of the profile.
     */
    public String getPhone_number() {
        return phone_number;
    }

    /**
     * Sets the phone number of the profile.
     * @param phone_number phone number of the profile
     */
    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    /**
     * Gets the imageURL of the profile picture
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the imageURL of the profile picture
     * @param imageUrl profile picture imageUrl
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the notification preference
     */
    public boolean isNotifPref() {
         return notifPref;
     }

    /**
     * Sets the notification preference
     * @param notifPref notification preference of profile holder
     */
     public void setNotifPref(boolean notifPref) {
        this.notifPref = notifPref;
     }
 }
