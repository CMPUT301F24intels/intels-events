package com.example.intels_app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;

import java.util.HashMap;

public class NotificationsUnitTest {
    @Test
    public void testCustomNotificationStorage() {
        String testEventId = "event123";
        String customMessage = "This is a test notification";
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Simulate sending a custom notification
        db.collection("notifications").document(testEventId)
                .set(new HashMap<String, Object>() {{
                    put("eventId", testEventId);
                    put("message", customMessage);
                }})
                .addOnCompleteListener(task -> {
                    assertTrue(task.isSuccessful());

                    // Verify custom message in Firestore
                    db.collection("notifications").document(testEventId).get()
                            .addOnSuccessListener(document -> {
                                assertEquals(customMessage, document.getString("message"));
                            });
                });
    }
}
