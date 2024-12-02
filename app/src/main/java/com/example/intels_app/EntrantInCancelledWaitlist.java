package com.example.intels_app;
import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * This activity handles the display and management of cancelled entrants in an event waitlist.
 * It allows for searching cancelled entrants, sending custom notifications to those entrants,
 * and initiating a redraw process to replace cancelled entrants with other eligible individuals.
 * The activity interacts with Firestore to fetch data related to entrants, send notifications,
 * and update the status of entrants within the event.
 * @author Aayushi Shah, Katrina Alejo
 * @see com.example.intels_app.EventGridOrganizerActivity Activity for managing event grid and its operations
 * @see com.example.intels_app.Entrant Adapter for displaying entrants
 */

public class EntrantInCancelledWaitlist extends AppCompatActivity {
    private Button waitlist_button, cancelled_button;
    private ListView listView;
    private List<Profile> profileList;
    private String eventName;
    private CheckBox sendNotificationCheckbox;

    /**
     * Initializes the UI components and sets up event listeners for buttons.
     * @param savedInstanceState A Bundle containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waitlist_with_cancelled_entrants);

        // Retrieve eventName from the Intent
        eventName = getIntent().getStringExtra("eventName");
        Log.d("CancelledEntrants", "Retrieved eventName: " + eventName);

        if (eventName == null || eventName.isEmpty()) {
            Toast.makeText(this, "Event ID is missing. Cannot proceed.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("CancelledEntrants", "Retrieved eventName: " + eventName);

        EditText searchBar = findViewById(R.id.search_bar);
        listView = findViewById(R.id.profile_list);

        profileList = new ArrayList<>();
        CancelledProfileAdapter adapter = new CancelledProfileAdapter(this, profileList);
        listView.setAdapter(adapter);

        Button redrawButton = findViewById(R.id.replacement_draw_button);
        redrawButton.setOnClickListener(v -> {
            redrawReplacementEntrant();
        });


        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(EntrantInCancelledWaitlist.this, EventGridOrganizerActivity.class);
            startActivity(intent);
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        waitlist_button = findViewById(R.id.btn_waitlist);
        cancelled_button = findViewById(R.id.btn_cancelled);

        waitlist_button.setOnClickListener(v -> {
            Intent intent = new Intent(EntrantInCancelledWaitlist.this, EntrantInWaitlist.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        cancelled_button.setOnClickListener(v -> {
            Intent intent = new Intent(EntrantInCancelledWaitlist.this, EntrantInCancelledWaitlist.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("eventName", eventName);
            startActivity(intent);
        });


        sendNotificationCheckbox = findViewById(R.id.checkbox_notify);
        sendNotificationCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showCustomNotificationDialog();
            }
        });
    }

    /**
     * Refreshes the list of cancelled entrants when returning to this activity.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list of cancelled entrants when returning to this activity
        fetchCancelledEntrants();
        // Set colors for UI
        cancelled_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
        waitlist_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
    }

    /**
     * Fetches the list of cancelled entrants for the specified event from Firestore.
     * Updates the ListView with the retrieved profiles.
     */
    private void fetchCancelledEntrants() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notificationsRef = db.collection("notifications");

        List<Entrant> entrantsList = new ArrayList<>(); // Updated to use Entrant model
        EntrantAdapter adapter = new EntrantAdapter(this, entrantsList); // Updated adapter
        listView.setAdapter(adapter);

        // Fetch documents where the type is "declined" and matches the event name
        notificationsRef.whereEqualTo("type", "declined")
                .whereEqualTo("eventName", eventName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        entrantsList.clear(); // Clear previous data

                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            Log.d("CancelledEntrants", "Documents retrieved for cancelled entrants.");

                            List<String> profileIds = new ArrayList<>();
                            for (DocumentSnapshot notification : task.getResult()) {
                                String profileId = notification.getString("profileId");
                                if (profileId != null && !profileId.isEmpty()) {
                                    profileIds.add(profileId);
                                } else {
                                    Log.w("CancelledEntrants", "Profile ID missing in notification document.");
                                }
                            }

                            // Fetch all profiles in a single operation
                            if (!profileIds.isEmpty()) {
                                for (String profileId : profileIds) {
                                    db.collection("waitlisted_entrants")
                                            .document(profileId)
                                            .get()
                                            .addOnSuccessListener(profileDoc -> {
                                                if (profileDoc.exists()) {
                                                    Map<String, Object> profile = (Map<String, Object>) profileDoc.get("profile");
                                                    if (profile != null) {
                                                        String profileName = (String) profile.get("name");
                                                        String imageUrl = (String) profile.get("imageUrl");

                                                        if (profileName != null) {
                                                            entrantsList.add(new Entrant(profileName, imageUrl)); // Add Entrant object
                                                            Log.d("CancelledEntrants", "Added entrant: " + profileName);
                                                        } else {
                                                            Log.w("CancelledEntrants", "Name field is missing for profileId: " + profileId);
                                                        }
                                                    }
                                                } else {
                                                    Log.w("CancelledEntrants", "Profile document does not exist for ID: " + profileId);
                                                }

                                                // Refresh the adapter only after processing all documents
                                                if (profileIds.indexOf(profileId) == profileIds.size() - 1) {
                                                    adapter.notifyDataSetChanged(); // Notify adapter once
                                                }
                                            })
                                            .addOnFailureListener(e -> Log.e("Firestore", "Error fetching profile for ID: " + profileId, e));
                                }
                            } else {
                                Log.w("CancelledEntrants", "No valid profile IDs found in notifications.");
                                Toast.makeText(this, "No cancelled entrants found for this event.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w("CancelledEntrants", "No documents found for cancelled entrants.");
                            Toast.makeText(this, "No cancelled entrants found for this event.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.w("Firestore", "Error fetching notifications", task.getException());
                        Toast.makeText(this, "Error retrieving data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Shows a dialog box that allows the organizer to enter a custom notification message
     * to be sent to all cancelled entrants.
     */
    private void showCustomNotificationDialog() {
        EditText input = new EditText(this);
        input.setHint("Enter custom notification message");

        new AlertDialog.Builder(this)
                .setTitle("Custom Notification")
                .setMessage("Enter the message to send to all cancelled entrants:")
                .setView(input)
                .setPositiveButton("Send", (dialog, which) -> {
                    String message = input.getText().toString().trim();
                    if (!message.isEmpty()) {
                        sendNotificationToCancelledEntrants(message);
                        sendNotificationCheckbox.setChecked(false); // Uncheck the checkbox after sending
                    } else {
                        Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    sendNotificationCheckbox.setChecked(false);
                    dialog.cancel();
                })
                .show();
    }

    /**
     * Sends a notification with a custom message to all eligible cancelled entrants for the event.
     * @param message The custom message to be sent to the cancelled entrants.
     */
    private void sendNotificationToCancelledEntrants(String message) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notificationsRef = db.collection("notifications");

        if (eventName == null || eventName.isEmpty()) {
            Log.e("Firestore", "eventName is null or empty");
            Toast.makeText(this, "Event Name is missing. Cannot send notification.", Toast.LENGTH_SHORT).show();
            return;
        }

        notificationsRef.whereEqualTo("type", "declined")
                .whereEqualTo("eventName", eventName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<DocumentSnapshot> cancelledEntrants = task.getResult().getDocuments();

                        if (cancelledEntrants.isEmpty()) {
                            Toast.makeText(this, "No cancelled entrants found for this event.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        AtomicBoolean notificationSent = new AtomicBoolean(false);

                        for (DocumentSnapshot entrant : cancelledEntrants) {
                            String profileId = entrant.getString("profileId");

                            if (profileId != null && !profileId.isEmpty()) {
                                // Fetch the profile from waitlisted_entrants
                                db.collection("waitlisted_entrants")
                                        .document(profileId)
                                        .get()
                                        .addOnSuccessListener(profileDoc -> {
                                            if (profileDoc.exists()) {
                                                Map<String, Object> profile = (Map<String, Object>) profileDoc.get("profile");

                                                if (profile != null) {
                                                    // Check if notifPref is true
                                                    Boolean notifPref = (Boolean) profile.get("notifPref");
                                                    if (notifPref != null && notifPref) {
                                                        String deviceId = (String) profile.get("deviceId");

                                                        // Send notification if deviceId exists
                                                        if (deviceId != null && !deviceId.isEmpty()) {
                                                            sendNotificationToProfile(deviceId, profileId, eventName, message);
                                                            notificationSent.set(true);
                                                        } else {
                                                            Log.w("Notification", "Device ID missing for profile: " + profileId);
                                                        }
                                                    } else {
                                                        Log.d("Notification", "Skipping profile with notifPref set to false: " + profileId);
                                                    }
                                                }
                                            } else {
                                                Log.w("Notification", "Profile document does not exist for ID: " + profileId);
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("Firestore", "Error fetching profile document for ID: " + profileId, e);
                                        });
                            } else {
                                Log.w("Notification", "Profile ID missing in notification document.");
                            }
                        }

                        if (notificationSent.get()) {
                            Toast.makeText(this, "Notifications sent successfully to all eligible cancelled entrants.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "No eligible cancelled entrants found with notifications enabled.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("Firestore", "Error fetching cancelled entrants", task.getException());
                        Toast.makeText(this, "Failed to send notifications.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Sends a notification to a specific profile.
     * @param deviceId  The device ID of the profile to receive the notification.
     * @param profileId The ID of the profile to receive the notification.
     * @param eventName The name of the event associated with the notification.
     * @param message   The message to be sent to the profile.
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

        // Add the notification to the Firestore collection
        db.collection("notifications")
                .add(notificationData)
                .addOnSuccessListener(documentReference -> Log.d("Notification", "Notification sent with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.e("Notification", "Error sending notification", e));
    }

    /**
     * Performs a redraw for replacement entrants by selecting eligible entrants from the "not_selected_entrants"
     * collection, sending notifications to the selected and not-selected entrants, and updating Firestore.
     */
    private void redrawReplacementEntrant() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference selectedEntrantsRef = db.collection("selected_entrants");
        CollectionReference notSelectedRef = db.collection("not_selected_entrants");
        CollectionReference notificationsRef = db.collection("notifications");

        notificationsRef.whereEqualTo("type", "declined")
                .whereEqualTo("eventName", eventName)
                .get()
                .addOnSuccessListener(declinedQuerySnapshot -> {
                    int declinedCount = declinedQuerySnapshot.size();

                    if (declinedCount == 0) {
                        Toast.makeText(this, "No declined entrants to replace.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    notSelectedRef.whereEqualTo("eventName", eventName)
                            .whereEqualTo("reconsiderForDraw", true)
                            .get()
                            .addOnSuccessListener(notSelectedQuerySnapshot -> {
                                List<DocumentSnapshot> notSelectedList = notSelectedQuerySnapshot.getDocuments();

                                if (notSelectedList.isEmpty()) {
                                    Toast.makeText(this, "No eligible entrants for redraw.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // Shuffle and pick not_selected entrant for redraw
                                Collections.shuffle(notSelectedList);
                                List<DocumentSnapshot> newSelectedProfiles = notSelectedList.subList(0, Math.min(declinedCount, notSelectedList.size()));

                                // Notify selected entrant
                                for (DocumentSnapshot profile : newSelectedProfiles) {
                                    String profileId = profile.getId();
                                    String deviceId = profile.getString("deviceId");

                                    if (profileId != null && deviceId != null) {
                                        // Add to selected entrants collection
                                        Map<String, Object> selectedData = new HashMap<>();
                                        selectedData.put("profileId", profileId);
                                        selectedData.put("eventName", eventName);
                                        selectedData.put("timestamp", FieldValue.serverTimestamp());

                                        selectedEntrantsRef.add(selectedData)
                                                .addOnSuccessListener(docRef -> {
                                                    Log.d("Redraw", "Selected replacement entrant: " + profileId);
                                                    // Remove from not_selected_entrants in firebase
                                                    profile.getReference().delete();

                                                    // Send notification
                                                    sendNotificationToProfile(deviceId, profileId, eventName, "Congratulations! You have been selected for the event!", "selected");
                                                })
                                                .addOnFailureListener(e -> Log.e("Redraw", "Error adding replacement entrant", e));
                                    }
                                }

                                // Notify remaining profiles that were not selected
                                List<DocumentSnapshot> remainingProfiles = notSelectedList.subList(Math.min(declinedCount, notSelectedList.size()), notSelectedList.size());
                                for (DocumentSnapshot profile : remainingProfiles) {
                                    String profileId = profile.getId();
                                    String deviceId = profile.getString("deviceId");

                                    if (profileId != null && deviceId != null) {
                                        sendNotificationToProfile(deviceId, profileId, eventName, "You were not selected in the redraw.", "not_selected");
                                    }
                                }

                                Toast.makeText(this, "Redraw complete. Notifications sent.", Toast.LENGTH_SHORT).show();

                                // Redirect to DrawCompleteActivity
                                Intent intent = new Intent(EntrantInCancelledWaitlist.this, DrawCompleteActivity.class);
                                intent.putExtra("eventName", eventName); // Pass the event name to the DrawCompleteActivity
                                startActivity(intent);
                                finish(); // Close the current activity
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Redraw", "Error fetching not_selected entrants", e);
                                Toast.makeText(this, "Failed to fetch not_selected entrants.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("Redraw", "Error fetching declined notifications", e);
                    Toast.makeText(this, "Failed to fetch declined entrants.", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Sends a notification to a specific profile with additional type information.
     * @param deviceId  The device ID of the profile to receive the notification.
     * @param profileId The name of the profile to receive the notification.
     * @param eventName The name of the event associated with the notification.
     * @param message   The message to be sent to the profile.
     * @param type      The type of notification.
     */
    private void sendNotificationToProfile(String deviceId, String profileId, String eventName, String message, String type) {
        if (deviceId == null || deviceId.isEmpty()) {
            Log.w(TAG, "Device ID is missing for profile: " + profileId);
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("deviceId", deviceId);
        notificationData.put("profileId", profileId);
        notificationData.put("eventName", eventName);
        notificationData.put("message", message);
        notificationData.put("timestamp", FieldValue.serverTimestamp());
        notificationData.put("type", type);

        db.collection("notifications")
                .add(notificationData)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Notification sent with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error sending notification", e));
    }
}