package com.example.intels_app;

public class Profile {
    private String name;
    private int imageResId;

    public Profile(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
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
}
