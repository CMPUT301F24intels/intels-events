package com.example.intels_app;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * FinalList activity displays a list of accepted event entrants and provides functionality to search the list
 * and send custom notifications. Entrant data is fetched from Firestore and displayed in a ListView.

 * Users can navigate back to the organizer's event grid and compose custom notification messages for selected entrants.
 * Author: Dhanshri Patel, Katrina Alejo
 */
public class FinalList extends AppCompatActivity {
    private ListView entrantList;
    private List<Profile> profileList;
    private ImageButton back_button;
    private CheckBox sendNotifications;
    private String eventName;

    /**
     * Initializes the UI elements and sets up the list of accepted entrants.
     * @param savedInstanceState Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_list);

        // Get the event ID from the intent
        eventName = getIntent().getStringExtra("eventName");

        eventName = getIntent().getStringExtra("eventName");
        Log.d("AcceptedEntrants", "Retrieved eventName: " + eventName);

        entrantList = findViewById(R.id.entrant_list);
        profileList = new ArrayList<>();
        ProfileAdapter adapter = new ProfileAdapter(this, profileList);
        entrantList.setAdapter(adapter);

        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(view -> {
            Intent intent = new Intent(FinalList.this, EntrantInWaitlist.class);
            intent.putExtra("eventName", eventName);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        sendNotifications = findViewById(R.id.send_notifications);
        sendNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showCustomNotificationDialog();
            }
        });

        // Fetch accepted entrants from Firestore
        fetchAcceptedEntrants((ProfileAdapter) entrantList.getAdapter());
    }

    /**
     * Fetches the list of accepted entrants from Firestore and
     * updates the UI with the profiles.
     * @param adapter The adapter used to display the profiles in the ListView.
     */
    private void fetchAcceptedEntrants(ProfileAdapter adapter) {
        Log.d("AcceptedEntrants", "Fetching accepted entrants for event: " + eventName);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notificationsRef = db.collection("notifications");

        Set<String> processedProfileIds = new HashSet<>();

        notificationsRef
                .whereEqualTo("type", "accepted")
                .whereEqualTo("eventName", eventName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        profileList.clear();
                        List<DocumentSnapshot> notifications = task.getResult().getDocuments();

                        if (notifications.isEmpty()) {
                            Toast.makeText(this, "No accepted entrants found for this event.", Toast.LENGTH_SHORT).show();
                            adapter.updateData(new ArrayList<>(profileList));
                            adapter.notifyDataSetChanged();
                            return;
                        }

                        for (DocumentSnapshot notification : notifications) {
                            String profileId = notification.getString("profileId");

                            if (profileId != null && !processedProfileIds.contains(profileId)) {
                                processedProfileIds.add(profileId);

                                db.collection("waitlisted_entrants")
                                        .document(profileId)
                                        .get()
                                        .addOnSuccessListener(profileDoc -> {
                                            if (profileDoc.exists()) {
                                                String name = profileDoc.getString("profile.name");
                                                String imageUrl = profileDoc.getString("profile.imageUrl");
                                                String deviceId = profileDoc.getString("deviceId");

                                                Profile profile = new Profile(name, imageUrl);
                                                profileList.add(profile);

                                                Log.d("AcceptedEntrants", "Added profile: Name = " + name + ", DeviceId = " + deviceId);

                                                // Send notification to this profile
                                                if (deviceId != null && !deviceId.isEmpty()) {
                                                    sendNotificationToProfile(deviceId, profileId, eventName, "You have been accepted!");
                                                } else {
                                                    Log.w("AcceptedEntrants", "Device ID missing for profile: " + profileId);
                                                }

                                                adapter.updateData(new ArrayList<>(profileList));
                                                adapter.notifyDataSetChanged();
                                            }
                                        })
                                        .addOnFailureListener(e -> Log.w("Firestore", "Error fetching profile for ID: " + profileId, e));
                            } else if (profileId == null) {
                                Log.w("AcceptedEntrants", "Missing profileId in notification.");
                            }
                        }
                    } else {
                        Log.w("Firestore", "Error fetching accepted notifications", task.getException());
                        Toast.makeText(this, "Failed to retrieve accepted entrants.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Displays a custom notification dialog for the user to enter a notification message.
     */
    private void showCustomNotificationDialog() {
        EditText input = new EditText(this);
        input.setHint("Enter custom notification message");

        new AlertDialog.Builder(this)
                .setTitle("Custom Notification")
                .setMessage("Enter the message to send to all selected entrants:")
                .setView(input)
                .setPositiveButton("Send", (dialog, which) -> {
                    String message = input.getText().toString().trim();
                    if (!message.isEmpty()) {
                        sendNotificationToEntrants(message);
                        sendNotifications.setChecked(false);
                    } else {
                        Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    sendNotifications.setChecked(false);
                    dialog.cancel();
                })
                .show();
    }

    /**
     * Sends a notification to all accepted entrants for the event.
     * @param message The message to be sent to all accepted entrants.
     */
    private void sendNotificationToEntrants(String message) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notificationsRef = db.collection("notifications");

        if (eventName == null || eventName.isEmpty()) {
            Log.e("Notification", "Event name is missing");
            Toast.makeText(this, "Event name is missing. Cannot send notification.", Toast.LENGTH_SHORT).show();
            return;
        }

        notificationsRef.whereEqualTo("type", "accepted")
                .whereEqualTo("eventName", eventName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<DocumentSnapshot> notifications = task.getResult().getDocuments();

                        if (notifications.isEmpty()) {
                            Toast.makeText(this, "No accepted entrants to notify.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (DocumentSnapshot notification : notifications) {
                            String profileId = notification.getString("profileId");

                            if (profileId != null) {
                                db.collection("waitlisted_entrants")
                                        .document(profileId)
                                        .get()
                                        .addOnSuccessListener(profileDoc -> {
                                            if (profileDoc.exists()) {
                                                // Fetch profile data and check notifPref
                                                Map<String, Object> profile = (Map<String, Object>) profileDoc.get("profile");
                                                if (profile != null) {
                                                    Boolean notifPref = (Boolean) profile.get("notifPref");
                                                    String deviceId = (String) profile.get("deviceId");

                                                    // Only send notification if notifPref is true
                                                    if (notifPref != null && notifPref && deviceId != null && !deviceId.isEmpty()) {
                                                        sendNotificationToProfile(deviceId, profileId, eventName, message);
                                                    } else {
                                                        Log.d("Notification", "Skipping profile with notifPref set to false or missing device ID: " + profileId);
                                                    }
                                                }
                                            }
                                        })
                                        .addOnFailureListener(e -> Log.e("Notification", "Error fetching profile document for ID: " + profileId, e));
                            }
                        }

                        Toast.makeText(this, "Notifications sent to all accepted entrants.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("Notification", "Error fetching accepted entrants", task.getException());
                        Toast.makeText(this, "Failed to send notifications.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Sends a notification to a specific profile.
     * @param deviceId  The device ID of the profile to send the notification to.
     * @param profileId The profile ID of the recipient.
     * @param eventName The event name for which the notification is being sent.
     * @param message   The message to be sent.
     */
    private void sendNotificationToProfile(String deviceId, String profileId, String eventName, String message) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create notification data
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("deviceId", deviceId);
        notificationData.put("profileId", profileId);
        notificationData.put("eventName", eventName);
        notificationData.put("message", message);
        notificationData.put("timestamp", FieldValue.serverTimestamp());

        // Add the notification to Firestore
        db.collection("notifications")
                .add(notificationData)
                .addOnSuccessListener(documentReference -> Log.d("Notification", "Notification sent with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.e("Notification", "Error sending notification", e));
    }
}
