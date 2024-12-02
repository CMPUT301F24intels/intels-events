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

    /**
     * Default constructor required for Firebase serialization.
     */
    public DataClass() {
    }

    /**
     * Constructor that initializes the imageUrl field.
     * @param imageUrl The URL of the image to be set.
     */
    public DataClass(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Returns the URL of the image.
     * @return The URL of the image as a String.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the URL of the image.
     * @param imageUrl The URL of the image to be set.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
