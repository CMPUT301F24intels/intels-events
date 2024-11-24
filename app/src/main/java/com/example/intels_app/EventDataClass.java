package com.example.intels_app;

public class EventDataClass {
    private String imageUrl;

    // Default constructor for Firebase
    public EventDataClass() {
    }

    public EventDataClass(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}