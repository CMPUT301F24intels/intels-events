package com.example.intels_app;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.example.intels_app.DataClass;

public class DataClassUnitTest {

    private DataClass dataClass;

    @Test
    public void testDefaultConstructor() {

        dataClass = new DataClass();

        String imageUrl = dataClass.getImageUrl();

        assertNull(imageUrl); // The imageUrl should be null by default, as it's not initialized
    }

    @Test
    public void testConstructorWithImageUrl() {

        String expectedImageUrl = "https://example.com/image.jpg";

        dataClass = new DataClass(expectedImageUrl);

        assertNotNull(dataClass); // Ensure the DataClass object is created
        assertEquals(expectedImageUrl, dataClass.getImageUrl()); // Verify the imageUrl is set correctly
    }

    @Test
    public void testGetImageUrl() {

        String expectedImageUrl = "https://example.com/image.jpg";
        dataClass = new DataClass(expectedImageUrl);

        String actualImageUrl = dataClass.getImageUrl();

        assertEquals(expectedImageUrl, actualImageUrl); // Ensure the getter returns the expected imageUrl
    }

    @Test
    public void testSetImageUrl() {

        String initialImageUrl = "https://example.com/image.jpg";
        dataClass = new DataClass(initialImageUrl);

        String newImageUrl = "https://example.com/new_image.jpg";
        dataClass.setImageUrl(newImageUrl);

        assertEquals(newImageUrl, dataClass.getImageUrl()); // Ensure the setter updates the imageUrl correctly
    }
}
