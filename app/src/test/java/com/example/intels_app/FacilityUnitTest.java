package com.example.intels_app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class FacilityUnitTest {

    private Facility facility;

    @Test
    public void testFacilityNoImage() {
        String facilityName = "Aayushi's Facility";
        String location = "Downtown Edmonton";
        String email = "shah@gmail.com";
        String telephone = "1234567890";
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
        String facilityName = "Aayushi's Facility";
        String location = "Downtown Edmonton";
        String email = "shah@gmail.com";
        String telephone = "1234567890";
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

    @Before
    public void setUp() {
        // Initialize the Facility object before each test
        facility = new Facility("FacilityName", "Location", "Email", "Phone", "Device ID");
    }

    @Test
    public void testGetFacilityName() {
        facility.setFacilityName("New Facility");
        assertEquals("New Facility", facility.getFacilityName());
    }

    @Test
    public void testSetFacilityName() {
        facility.setFacilityName("New Facility");
        assertEquals("New Facility", facility.getFacilityName());
    }

    @Test
    public void testGetLocation() {
        facility.setLocation("New Location");
        assertEquals("New Location", facility.getLocation());
    }

    @Test
    public void testSetLocation() {
        facility.setLocation("New Location");
        assertEquals("New Location", facility.getLocation());
    }

    @Test
    public void testGetOrganizerName() {
        facility.setOrganizerName("John Doe");
        assertEquals("John Doe", facility.getOrganizerName());
    }

    @Test
    public void testSetOrganizerName() {
        facility.setOrganizerName("John Doe");
        assertEquals("John Doe", facility.getOrganizerName());
    }

    @Test
    public void testGetEmail() {
        facility.setEmail("contact@facility.com");
        assertEquals("contact@facility.com", facility.getEmail());
    }

    @Test
    public void testSetEmail() {
        facility.setEmail("contact@facility.com");
        assertEquals("contact@facility.com", facility.getEmail());
    }

    @Test
    public void testGetTelephone() {
        facility.setTelephone("9876543210");
        assertEquals("9876543210", facility.getTelephone());
    }

    @Test
    public void testSetTelephone() {
        facility.setTelephone("9876543210");
        assertEquals("9876543210", facility.getTelephone());
    }

    @Test
    public void testGetFacilityImageUrl() {
        facility.setFacilityImageUrl("https://example.com/facility-image.jpg");
        assertEquals("https://example.com/facility-image.jpg", facility.getFacilityImageUrl());
    }

    @Test
    public void testSetFacilityImageUrl() {
        facility.setFacilityImageUrl("https://example.com/facility-image.jpg");
        assertEquals("https://example.com/facility-image.jpg", facility.getFacilityImageUrl());
    }

    @Test
    public void testGetDeviceId() {
        facility.setDeviceId("deviceID123");
        assertEquals("deviceID123", facility.getDeviceId());
    }

    @Test
    public void testSetDeviceId() {
        facility.setDeviceId("deviceID123");
        assertEquals("deviceID123", facility.getDeviceId());
    }
}
