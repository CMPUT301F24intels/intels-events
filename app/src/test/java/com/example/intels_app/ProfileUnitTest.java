package com.example.intels_app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ProfileUnitTest {
    @Test
    public void testDefaultConstructor() {
        Profile profile = new Profile();

        assertNull(profile.getName());
        assertNull(profile.getEmail());
        assertEquals(null, profile.getPhone_number());
        assertNull(profile.getImageUrl());
        assertEquals(0, profile.getImageResId());
        assertNull(profile.getDeviceId());
    }

    @Test
    public void testProfileWithNameAndImageResId() {
        String name = "Aayushi Shah";
        int imageResId = 12345;

        Profile profile = new Profile(name, imageResId);

        assertEquals(name, profile.getName());
        assertEquals(imageResId, profile.getImageResId());
    }

    @Test
    public void testConstructorWithNameEmailPhoneAndImageUrl() {
        // Given
        String name = "Janan Panchal";
        String email = "shah@example.com";
        String phoneNumber = "123456789";
        String imageUrl = "http://example.com/image.jpg";

        Profile profile = new Profile(name, email, phoneNumber, imageUrl);

        assertEquals(name, profile.getName());
        assertEquals(email, profile.getEmail());
        assertEquals(phoneNumber, profile.getPhone_number());
        assertEquals(imageUrl, profile.getImageUrl());
    }

    @Test
    public void testProfileWithNameEmailAndPhoneNumber() {
        String name = "Dhanshri";
        String email = "dhanshri@example.com";
        String phoneNumber = "987654321";

        Profile profile = new Profile(name, email, phoneNumber);

        assertEquals(name, profile.getName());
        assertEquals(email, profile.getEmail());
        assertEquals(phoneNumber, profile.getPhone_number());
        assertNull(profile.getImageUrl());
    }

    @Test
    public void testProfileWithDeviceIdNameEmailPhoneNumberAndImageUrl() {
        String deviceId = "device123";
        String name = "Kanishkaaaa";
        String email = "aswami@example.com";
        String phoneNumber = "555666777";
        String imageUrl = "http://example.com/aswami.jpg";

        Profile profile = new Profile(deviceId, name, email, phoneNumber, imageUrl);

        assertEquals(deviceId, profile.getDeviceId());
        assertEquals(name, profile.getName());
        assertEquals(email, profile.getEmail());
        assertEquals(phoneNumber, profile.getPhone_number());
        assertEquals(imageUrl, profile.getImageUrl());
    }

    @Test
    public void testDeviceIDNameEmailPhoneImageNotif() {
        String deviceId = "device123";
        String name = "Kanishkaaaa";
        String email = "aswami@example.com";
        String phoneNumber = "555666777";
        String imageUrl = "http://example.com/aswami.jpg";
        Boolean notifPref = true;

        Profile profile = new Profile(deviceId, name, email, phoneNumber, imageUrl, notifPref);

        assertEquals(deviceId, profile.getDeviceId());
        assertEquals(name, profile.getName());
        assertEquals(email, profile.getEmail());
        assertEquals(phoneNumber, profile.getPhone_number());
        assertEquals(imageUrl, profile.getImageUrl());
        assertEquals(true, profile.isNotifPref());
    }

    @Test
    public void testNameImageUrl() {
        String name = "Aayushi Shah";
        String imageUrl = "http://example.com/shahahaha.jpg";

        Profile profile = new Profile(name, imageUrl);

        assertEquals(name, profile.getName());
        assertEquals(imageUrl, profile.getImageUrl());
    }

    @Test
    public void testProfileWithDeviceIdOnly() {
        String deviceId = "device789";

        Profile profile = new Profile(deviceId);

        assertEquals(deviceId, profile.getDeviceId());
    }

    private Profile profile;

    @Before
    public void setUp() {
        // Initialize the Profile object before each test
        profile = new Profile("John Doe", "john.doe@example.com", "1234567890", "https://example.com/profile.jpg");
    }

    @Test
    public void testGetName() {
        assertEquals("John Doe", profile.getName());
    }

    @Test
    public void testSetName() {
        profile.setName("Jane Doe");
        assertEquals("Jane Doe", profile.getName());
    }

    @Test
    public void testGetEmail() {
        assertEquals("john.doe@example.com", profile.getEmail());
    }

    @Test
    public void testSetEmail() {
        profile.setEmail("jane.doe@example.com");
        assertEquals("jane.doe@example.com", profile.getEmail());
    }

    @Test
    public void testGetPhoneNumber() {
        assertEquals("1234567890", profile.getPhone_number());
    }

    @Test
    public void testSetPhoneNumber() {
        profile.setPhone_number("0987654321");
        assertEquals("0987654321", profile.getPhone_number());
    }

    @Test
    public void testGetImageUrl() {
        assertEquals("https://example.com/profile.jpg", profile.getImageUrl());
    }

    @Test
    public void testSetImageUrl() {
        profile.setImageUrl("https://example.com/new-profile.jpg");
        assertEquals("https://example.com/new-profile.jpg", profile.getImageUrl());
    }

    @Test
    public void testGetDeviceId() {
        // Testing the constructor with deviceId
        Profile profileWithDeviceId = new Profile("device123");
        assertEquals("device123", profileWithDeviceId.getDeviceId());
    }

    @Test
    public void testSetDeviceId() {
        profile.setDeviceId("newDevice123");
        assertEquals("newDevice123", profile.getDeviceId());
    }

    @Test
    public void testIsNotifPref() {
        // Test the default value of notification preference
        assertFalse(profile.isNotifPref());

        // Set new value
        profile.setNotifPref(true);
        assertTrue(profile.isNotifPref());
    }
}
