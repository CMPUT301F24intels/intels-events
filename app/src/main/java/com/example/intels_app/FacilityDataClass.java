package com.example.intels_app;

/**
 * This class represents data for a facility, specifically the URL of an image associated with it.
 * It provides getter and setter methods for accessing and modifying the image URL.
 *
 * @author Kaniskha Aswani
 */

public class FacilityDataClass {
    private String imageUrl;

    /**
     * Constructor to initialize the facility data with an image URL.
     * @param imageUrl URL of the facility image.
     */
    public FacilityDataClass(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the URL of the facility image.
     * @return The URL of the facility image.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the URL of the facility image.
     * @param imageUrl The URL to set for the facility image.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}