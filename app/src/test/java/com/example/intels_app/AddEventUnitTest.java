package com.example.intels_app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.graphics.Bitmap;

import org.junit.Test;

public class AddEventUnitTest {

    @Test
    public void testHashImage() {
        // Sample byte array
        byte[] sampleData = "test".getBytes();

        // Generate hash
        String hash = AddEvent.hashImage(sampleData);

        // Verify the hash is correct
        assertNotNull(hash);
        assertEquals(64, hash.length()); // SHA-256 hashes are 64 characters long
    }
}
