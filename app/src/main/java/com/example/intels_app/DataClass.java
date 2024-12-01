package com.example.intels_app;

/**
 * Represents a data model class for storing image URLs.
 * Primarily used for Firebase operations such as storing and retrieving image URLs.
 *
 * @author Kaniskha Aswani
 * @see com.google.firebase.firestore.FirebaseFirestore For database interactions
 * @see com.google.firebase.storage.FirebaseStorage For image storage in Firebase
 */

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
