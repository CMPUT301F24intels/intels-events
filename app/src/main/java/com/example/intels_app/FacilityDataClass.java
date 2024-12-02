package com.example.intels_app;

/**
 * This class represents data for a facility, specifically the URL of an image associated with it.
 * It provides getter and setter methods for accessing and modifying the image URL.
 *
 * @author Kaniskha Aswani
 */

public class FacilityDataClass {
    private String imageUrl;

    public FacilityDataClass(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}