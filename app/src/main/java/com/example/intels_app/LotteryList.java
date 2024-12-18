package com.example.intels_app;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * This class displays a list of selected entrants for a lottery event
 * in a RecyclerView based on the event ID and displays each entrant's
 * profile information.
 * Author: Katrina Alejo
 * @see com.example.intels_app.Profile Profiles class
 * @see com.example.intels_app.SelectedEntrantAdapter Adapter for profiles
 */
public class LotteryList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SelectedEntrantAdapter adapter;
    private List<Profile> selectedEntrants;
    private FirebaseFirestore db;
    private String eventName;
    private CheckBox sendNotifications;

    /**
     * Initializes UI components, sets up Firestore, loads selected entrants,
     * and configures back button, search bar, and send notifications checkbox.
     * @param savedInstanceState Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lottery_list);

        // Get the event ID from the intent
        eventName = getIntent().getStringExtra("eventName");

        if (eventName == null) {
            Toast.makeText(this, "Event ID is missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        selectedEntrants = new ArrayList<>();
        adapter = new SelectedEntrantAdapter(this, selectedEntrants);
        recyclerView.setAdapter(adapter);

        // Load selected entrants from Firestore
        loadSelectedEntrants();

        // Back button functionality
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(LotteryList.this, EntrantInWaitlist.class);
            intent.putExtra("eventName", eventName); // Pass the eventName back if needed
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Send Notifications Checkbox
        sendNotifications = findViewById(R.id.send_notifications);
        sendNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showCustomNotificationDialog();
            }
        });
    }

    /**
     * Loads the selected entrants for the lottery event from Firestore.
     * Filters out entrants who have declined the lottery, and adds the remaining entrants to the list.
     */
    private void loadSelectedEntrants() {
        db.collection("notifications")
                .whereEqualTo("type", "declined")
                .whereEqualTo("eventName", eventName)
                .get()
                .addOnSuccessListener(declinedDocs -> {
                    List<String> declinedProfileIds = new ArrayList<>();

                    // Collect all declined profile IDs
                    for (DocumentSnapshot doc : declinedDocs) {
                        String profileId = doc.getString("profileId");
                        if (profileId != null) {
                            declinedProfileIds.add(profileId);
                        }
                    }

                    // Fetch selected entrants excluding declined profiles
                    db.collection("selected_entrants")
                            .whereEqualTo("eventName", eventName)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                selectedEntrants.clear();
                                List<String> profileIds = new ArrayList<>();

                                // Step 1: Extract all profile IDs from selected_entrants
                                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                    String profileId = doc.getString("profileId");
                                    if (profileId != null && !declinedProfileIds.contains(profileId)) {
                                        profileIds.add(profileId); // Only include non-declined profiles
                                    }
                                }

                                if (profileIds.isEmpty()) {
                                    Toast.makeText(this, "No entrants selected for this event.", Toast.LENGTH_SHORT).show();
                                    adapter.notifyDataSetChanged();
                                    return;
                                }

                                // Query waitlisted_entrants for all valid profile IDs
                                db.collection("waitlisted_entrants")
                                        .whereIn(FieldPath.documentId(), profileIds)
                                        .get()
                                        .addOnSuccessListener(waitlistedDocs -> {
                                            for (DocumentSnapshot doc : waitlistedDocs) {
                                                // Extract profile details from waitlisted_entrants
                                                Map<String, Object> profileData = (Map<String, Object>) doc.get("profile");

                                                if (profileData != null) {
                                                    String name = (String) profileData.get("name");
                                                    /*String status = (String) profileData.get("status");*/ // e.g., "accepted" or "pending"
                                                    String imageUrl = (String) profileData.get("imageUrl");

                                                    // Create a Profile object or similar representation
                                                    Profile profile = new Profile(name, imageUrl);
                                                    selectedEntrants.add(profile);
                                                } else {
                                                    Log.w("LotteryList", "Profile missing in waitlisted_entrants for ID: " + doc.getId());
                                                }
                                            }

                                            // Notify the adapter after loading all profiles
                                            adapter.notifyDataSetChanged();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.w("LotteryList", "Error fetching profile details from waitlisted_entrants", e);
                                            Toast.makeText(this, "Failed to load entrant details.", Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Log.w("LotteryList", "Error fetching selected entrants", e);
                                Toast.makeText(this, "Failed to load selected entrants.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.w("LotteryList", "Error fetching declined profiles", e);
                    Toast.makeText(this, "Failed to load declined profiles.", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Deletes an entrant from the lottery list by removing them from the "selected_entrants" collection
     * and updating their status in the "notifications" collection.
     * @param profileId The ID of the profile to be removed.
     */
    public void deleteEntrantFromLotteryList(String profileId) {
        // Remove from `selected_entrants`
        db.collection("selected_entrants")
                .whereEqualTo("eventName", eventName)
                .whereEqualTo("profileId", profileId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        db.collection("selected_entrants").document(doc.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> Log.d("LotteryList", "Entrant removed from selected_entrants"))
                                .addOnFailureListener(e -> Log.e("LotteryList", "Error removing entrant from selected_entrants", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("LotteryList", "Error fetching entrant from selected_entrants", e));

        // Update status in `notifications` to "not-selected"
        db.collection("notifications")
                .whereEqualTo("eventName", eventName)
                .whereEqualTo("profileId", profileId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        db.collection("notifications").document(doc.getId())
                                .update("status", "not-selected")
                                .addOnSuccessListener(aVoid -> Log.d("LotteryList", "Entrant status updated in notifications"))
                                .addOnFailureListener(e -> Log.e("LotteryList", "Error updating entrant status in notifications", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("LotteryList", "Error fetching entrant from notifications", e));
    }

    /**
     * Show a dialog to enter a custom notification message.
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
                        sendNotifications.setChecked(false);  // Uncheck the checkbox after sending
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
     * Send notification to all entrants in the lottery list.
     * @param message Notification message to be sent.
     */
    private void sendNotificationToEntrants(String message) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Validate the message
        if (message == null || message.isEmpty()) {
            Toast.makeText(this, "Message cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedEntrants.isEmpty()) {
            Toast.makeText(this, "No selected entrants found.", Toast.LENGTH_SHORT).show();
            return;
        }
        for (Profile entrant : selectedEntrants) {
            String profileId = entrant.getName();
            if (profileId == null || profileId.isEmpty()) {
                Log.w("Notification", "Profile ID missing for entrant.");
                continue;
            }
            // Fetch deviceId and notifPref from waitlisted_entrants collection
            db.collection("waitlisted_entrants")
                    .document(profileId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> profile = (Map<String, Object>) documentSnapshot.get("profile");
                            if (profile != null) {
                                Boolean notifPref = (Boolean) profile.get("notifPref"); // Retrieve notifPref
                                String deviceId = (String) profile.get("deviceId"); // Retrieve deviceId
                                if (notifPref != null && notifPref) {
                                    if (deviceId != null && !deviceId.isEmpty()) {
                                        sendNotificationToProfile(deviceId, profileId, message);
                                    } else {
                                        Log.w("Notification", "Device ID is missing for profile: " + profileId);
                                    }
                                } else {
                                    Log.d("Notification", "Skipping profile with notifPref set to false: " + profileId);
                                }
                            } else {
                                Log.w("Notification", "Profile data is missing for profileId: " + profileId);
                            }
                        } else {
                            Log.w("Notification", "No document found for profileId: " + profileId);
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Notification", "Error fetching profile data for profileId: " + profileId, e));
        }
        Toast.makeText(this, "Notifications sent to draw entrants.", Toast.LENGTH_SHORT).show();
    }

    private void sendNotificationToProfile(String deviceId, String profileId, String message) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create notification data
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("deviceId", deviceId);
        notificationData.put("profileId", profileId);
        notificationData.put("eventName", eventName);
        notificationData.put("message", message);
        notificationData.put("timestamp", FieldValue.serverTimestamp());

        // Add the notification to the Firestore collection
        db.collection("notifications")
                .add(notificationData)
                .addOnSuccessListener(documentReference -> Log.d("Notification", "Notification sent with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.e("Notification", "Error sending notification", e));
    }

}


