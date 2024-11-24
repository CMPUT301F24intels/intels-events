package com.example.intels_app;

public class DataClass {
    private String imageUrl;

    // Default constructor for Firebase
    public DataClass() {
    }

    public DataClass(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
