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

    /**
     * Default constructor for Firebase.
     */
    public EventDataClass() {
    }

    /**
     * Constructs an instance of EventDataClass with a given image URL.
     * @param imageUrl The URL of the event's image.
     */
    public EventDataClass(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Retrieves the URL of the event's image.
     * @return The URL of the image as a String.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the URL of the event's image.
     * @param imageUrl The URL to set for the event's image.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}