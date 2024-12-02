package com.example.intels_app;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.example.intels_app.FacilityDataClass;

public class FacilityDataClassUnitTest {

    private FacilityDataClass facilityDataClass;

    @Before
    public void setUp() {
        // Initialize the FacilityDataClass object before each test
        facilityDataClass = new FacilityDataClass("https://example.com/facility-image.jpg");
    }

    @Test
    public void testGetImageUrl() {
        // Test if the image URL is correctly retrieved using the getter method
        assertEquals("https://example.com/facility-image.jpg", facilityDataClass.getImageUrl());
    }

    @Test
    public void testSetImageUrl() {
        // Test if the image URL can be set correctly using the setter method
        facilityDataClass.setImageUrl("https://example.com/new-image.jpg");
        assertEquals("https://example.com/new-image.jpg", facilityDataClass.getImageUrl());
    }

    @Test
    public void testConstructorWithImageUrl() {
        // Test the constructor with an image URL and ensure it is correctly initialized
        FacilityDataClass newFacilityData = new FacilityDataClass("https://example.com/another-image.jpg");
        assertEquals("https://example.com/another-image.jpg", newFacilityData.getImageUrl());
    }
}
