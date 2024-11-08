package com.example.intels_app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;

import java.util.HashMap;

public class EventIntegrationTest {
    @Test
    public void testEventCreationInManageEvents() {
        String testEventId = "newEvent123";
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Simulate event creation in Firestore
        db.collection("events").document(testEventId)
                .set(new HashMap<String, Object>() {{
                    put("eventId", testEventId);
                    put("name", "New Test Event");
                }})
                .addOnCompleteListener(task -> {
                    assertTrue(task.isSuccessful());

                    // Verify new event in Firestore
                    db.collection("events").document(testEventId).get()
                            .addOnSuccessListener(document -> {
                                assertEquals("New Test Event", document.getString("name"));
                            });
                });
    }

    @Test
    public void testEventDeletionFromOrganizerView() {
        String testEventId = "DeleteEvent123";
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Simulate event deletion in Firestore
        db.collection("events").document(testEventId).delete()
                .addOnCompleteListener(task -> {
                    assertTrue(task.isSuccessful());

                    // Verify that event no longer exists
                    db.collection("events").document(testEventId).get()
                            .addOnSuccessListener(document -> {
                                assertFalse(document.exists());
                            });
                });
    }
}
