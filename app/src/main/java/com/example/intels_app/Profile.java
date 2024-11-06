package com.example.intels_app;

public class Profile {
    private String name;
    private String username;
    private int phone_number;
    private int imageResId;
    private String imageUrl;

    public Profile(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public Profile(String name, String username, int phone_number, String imageUrl) {
        this.name = name;
        this.username = username;
        this.phone_number = phone_number;
        this.imageUrl = imageUrl;
    }

    // Default constructor required for Firestore
    public Profile() {}

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

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
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
