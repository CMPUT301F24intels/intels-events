package com.example.intels_app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ProfileCreationUnitTest {
    @Test
    public void testDefaultConstructor() {
        Profile profile = new Profile();

        assertNull(profile.getName());
        assertNull(profile.getEmail());
        assertEquals("0", profile.getPhone_number());
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
        assertNull(profile.getEmail());
        assertNull(profile.getDeviceId());
    }

    @Test
    public void testProfileWithNameAndEmail() {
        // Given
        String name = "Aayushi Shah";
        String email = "shah@gmail.com";

        Profile profile = new Profile(name, email);

        assertEquals(name, profile.getName());
        assertEquals(email, profile.getEmail());
        assertNull(profile.getDeviceId());
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
        assertNull(profile.getDeviceId());
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
    public void testProfileWithDeviceIdNameEmailAndPhoneNumber() {
        String deviceId = "device123";
        String name = "Het Patel";
        String email = "hetttt@example.com";
        String phoneNumber = "123123123";

        Profile profile = new Profile(deviceId, name, email, phoneNumber);

        assertEquals(deviceId, profile.getDeviceId());
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
    public void testProfileWithDeviceIdOnly() {
        String deviceId = "device789";

        Profile profile = new Profile(deviceId);

        assertEquals(deviceId, profile.getDeviceId());
        assertNull(profile.getName());
    }

    @Test
    public void testProfileSettersAndGetters() {
        Profile profile = new Profile();

        profile.setName("Aayushi Shah");
        profile.setEmail("shah@example.com");
        profile.setPhone_number("1234567890");
        profile.setImageUrl("http://example.com/shahahaha.jpg");
        profile.setDeviceId("device123");
        profile.setImageResId(54321);

        assertEquals("Aayushi Shah", profile.getName());
        assertEquals("shah@example.com", profile.getEmail());
        assertEquals("1234567890", profile.getPhone_number());
        assertEquals("http://example.com/shahahaha.jpg", profile.getImageUrl());
        assertEquals("device123", profile.getDeviceId());
        assertEquals(54321, profile.getImageResId());
    }
}
