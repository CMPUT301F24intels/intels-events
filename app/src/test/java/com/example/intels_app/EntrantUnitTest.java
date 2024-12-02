package com.example.intels_app;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.example.intels_app.Entrant;
import com.example.intels_app.EntrantNotifications;

public class EntrantUnitTest {

    private Entrant entrant;

    @Test
    public void testEntrantConstructorWithoutImageUrl() {
        String expectedName = "John Doe";

        entrant = new Entrant(expectedName);

        assertNotNull(entrant); // Ensures the entrant object is created
        assertEquals(expectedName, entrant.getName()); // Verify name is set correctly
        assertNotNull(entrant.getNotifications()); // Check if notifications object is initialized
        assertNull(entrant.getImageUrl()); // No image URL should be set, so it should be null
    }

    @Test
    public void testEntrantConstructorWithImageUrl() {
        String expectedName = "Jane Doe";
        String expectedImageUrl = "https://example.com/janedoe.jpg";

        entrant = new Entrant(expectedName, expectedImageUrl);

        assertNotNull(entrant); // Ensures the entrant object is created
        assertEquals(expectedName, entrant.getName()); // Verify name is set correctly
        assertEquals(expectedImageUrl, entrant.getImageUrl()); // Verify imageUrl is set correctly
    }

    @Test
    public void testGetName() {
        String expectedName = "John Smith";
        entrant = new Entrant(expectedName);

        String actualName = entrant.getName();

        assertEquals(expectedName, actualName); // Ensure name returned by getter matches the expected name
    }

    @Test
    public void testGetNotifications() {
        entrant = new Entrant("John Doe");

        EntrantNotifications notifications = entrant.getNotifications();

        assertNotNull(notifications); // Ensure notifications object is not null
        assertTrue(notifications instanceof EntrantNotifications); // Ensure it is of type EntrantNotifications
    }

    @Test
    public void testGetImageUrl() {
        String expectedImageUrl = "https://example.com/johndoe.jpg";
        entrant = new Entrant("John Doe", expectedImageUrl);

        String actualImageUrl = entrant.getImageUrl();

        assertEquals(expectedImageUrl, actualImageUrl); // Verify the image URL is correctly returned
    }
}
