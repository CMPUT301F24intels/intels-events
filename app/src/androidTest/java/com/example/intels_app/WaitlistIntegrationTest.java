package com.example.intels_app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;

import java.util.HashMap;

public class WaitlistIntegrationTest {
    @Test
    public void testEntrantJoiningWaitlist() {
        String testDeviceId = "DeviceId123";
        String testEventName = "Event123";
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Simulate joining waitlist by adding to Firestore
        db.collection("waitlisted_events")
                .document(testDeviceId)
                .set(new HashMap<String, Object>() {{
                    put("deviceId", testDeviceId);
                    put("eventName", testEventName);
                }})
                .addOnCompleteListener(task -> {
                    assertTrue(task.isSuccessful());

                    // Verify that entry exists in waitlisted_events
                    db.collection("waitlisted_events").document(testDeviceId).get()
                            .addOnSuccessListener(document -> {
                                assertEquals(testEventName, document.getString("eventName"));
                            });
                });
    }

    @Test
    public void testEventFilteringForEntrants() {
        String specificEventName = "filterEvent123";
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query entrants for a specific event
        db.collection("waitlisted_events")
                .whereEqualTo("eventName", specificEventName)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        assertEquals(specificEventName, document.getString("eventName"));
                    }
                });
    }

    @Test
    public void testEntrantProfileAndWaitlistAddition() {
        String testDeviceId = "testDeviceId456";
        String testEventName = "profileEvent123";
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Simulate profile creation and adding to waitlist
        db.collection("profiles").document(testDeviceId)
                .set(new HashMap<String, Object>() {{
                    put("deviceId", testDeviceId);
                    put("name", "Entrant Profile");
                }})
                .addOnCompleteListener(task -> {
                    assertTrue(task.isSuccessful());

                    // Verify profile in Firestore
                    db.collection("profiles").document(testDeviceId).get()
                            .addOnSuccessListener(profileDoc -> {
                                assertEquals("Entrant Profile", profileDoc.getString("name"));
                            });

                    // Add to waitlisted_entrants
                    db.collection("waitlisted_entrants").document(testDeviceId)
                            .set(new HashMap<String, Object>() {{
                                put("deviceId", testDeviceId);
                                put("eventName", testEventName);
                            }})
                            .addOnCompleteListener(waitlistTask -> {
                                assertTrue(waitlistTask.isSuccessful());

                                // Verify waitlist entry
                                db.collection("waitlisted_entrants").document(testDeviceId).get()
                                        .addOnSuccessListener(waitlistDoc -> {
                                            assertEquals(testEventName, waitlistDoc.getString("eventName"));
                                        });
                            });
                });
    }

}
