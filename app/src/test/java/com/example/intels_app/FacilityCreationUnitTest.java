package com.example.intels_app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class FacilityCreationUnitTest {
    @Test
    public void testFacilityNoArguments() {
        Facility facility = new Facility();

        assertNull(facility.getFacilityName());
        assertNull(facility.getLocation());
        assertNull(facility.getEmail());
        assertEquals(0, facility.getTelephone());
        assertNull(facility.getFacilityImageUrl());
        assertNull(facility.getDeviceId());
    }

    @Test
    public void testFacilityNoImage() {
        // Given
        String facilityName = "Aayushi's Facility";
        String location = "Downtown Edmonton";
        String email = "shah@gmail.com";
        int telephone = 1234567890;
        String deviceID = "device123";

        Facility facility = new Facility(facilityName, location, email, telephone, deviceID);

        assertEquals(facilityName, facility.getFacilityName());
        assertEquals(location, facility.getLocation());
        assertEquals(email, facility.getEmail());
        assertEquals(telephone, facility.getTelephone());
        assertNull(facility.getFacilityImageUrl());  // Should be null
        assertEquals(deviceID, facility.getDeviceId());
    }

    @Test
    public void testFacilityWithImage() {
        // Given
        String facilityName = "Aayushi's Facility";
        String location = "Downtown Edmonton";
        String email = "shah@gmail.com";
        int telephone = 1234567890;
        String imageUrl = "http://example.com/image.jpg";
        String deviceID = "device123";

        Facility facility = new Facility(facilityName, location, email, telephone, imageUrl, deviceID);

        assertEquals(facilityName, facility.getFacilityName());
        assertEquals(location, facility.getLocation());
        assertEquals(email, facility.getEmail());
        assertEquals(telephone, facility.getTelephone());
        assertEquals(imageUrl, facility.getFacilityImageUrl());
        assertEquals(deviceID, facility.getDeviceId());
    }
}
