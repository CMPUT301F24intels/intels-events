package com.example.intels_app;

/**
 * This class represents the data for an event, specifically storing the event's image URL.
 * It is used to facilitate data management in Firebase Firestore, where the image URL is
 * fetched or saved for each event.
 *
 * The class provides a constructor to initialize the event with an image URL, and getter
 * and setter methods for accessing and modifying the image URL.
 *
 * @author Kaniskha Aswani
 * @see com.example.intels_app.EventDataClass
 */

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