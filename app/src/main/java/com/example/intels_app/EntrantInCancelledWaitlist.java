/**
 * This class extends AppCompatActivity and provides a user interface to manage
 * entrants who have been cancelled/declined themselves from a waitlist. This activity
 * allows organizers to view, filter, and send notifications to cancelled entrants
 * using a ListView and search functionality.
 * @author Aayushi Shah, Katrina Alejo
 * @see com.example.intels_app.Profile Profile object
 * @see com.example.intels_app.EntrantInWaitlist Entrant information for an event
 * @see com.example.intels_app.EventGridOrganizerActivity Organizer's gridview of events
 */

package com.example.intels_app;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class EntrantInCancelledWaitlist extends AppCompatActivity {
    private Button waitlist_button, cancelled_button;
    private ListView listView;
    private List<Profile> profileList;
    private String eventName;
    private CheckBox sendNotificationCheckbox;

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

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list of cancelled entrants when returning to this activity
        fetchCancelledEntrants();
        // Set colors for UI
        cancelled_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
        waitlist_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
    }

    private void fetchCancelledEntrants() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notificationsRef = db.collection("notifications");

        List<String> profileNames = new ArrayList<>();
        EntrantAdapter adapter = new EntrantAdapter(this, profileNames); // Update the adapter to display names
        listView.setAdapter(adapter);

        // Fetch documents where the type is "declined" and matches the event name
        notificationsRef.whereEqualTo("type", "declined")
                .whereEqualTo("eventName", eventName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Clear existing data to avoid duplicates
                        profileNames.clear();

                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            Log.d("CancelledEntrants", "Documents retrieved for cancelled entrants.");

                            // Iterate over documents in the result
                            for (DocumentSnapshot notification : task.getResult()) {
                                String profileId = notification.getString("profileId");

                                if (profileId != null && !profileId.isEmpty()) {
                                    // Fetch the name from waitlisted_entrants
                                    db.collection("waitlisted_entrants")
                                            .document(profileId)
                                            .get()
                                            .addOnSuccessListener(profileDoc -> {
                                                if (profileDoc.exists()) {
                                                    String profileName = profileDoc.getString("profile.name"); // Fetch the name field
                                                    if (profileName != null) {
                                                        profileNames.add(profileName); // Add the profile name to the list
                                                        Log.d("CancelledEntrants", "Added profile name: " + profileName);
                                                    } else {
                                                        Log.w("CancelledEntrants", "Name field is missing for profileId: " + profileId);
                                                    }
                                                } else {
                                                    Log.w("CancelledEntrants", "Profile document does not exist for ID: " + profileId);
                                                }

                                                // Notify the adapter to refresh the ListView
                                                adapter.notifyDataSetChanged();
                                            })
                                            .addOnFailureListener(e -> Log.e("Firestore", "Error fetching profile for ID: " + profileId, e));
                                } else {
                                    Log.w("CancelledEntrants", "Profile ID missing in notification document.");
                                }
                            }
                        } else {
                            Log.w("CancelledEntrants", "No documents found for cancelled entrants.");
                            Toast.makeText(this, "No cancelled entrants found for this event.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.w("Firestore", "Error fetching documents", task.getException());
                        Toast.makeText(this, "Error retrieving data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

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
}