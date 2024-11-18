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
import android.view.View;
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
        ProfileAdapter adapter = new ProfileAdapter(this, profileList);
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
            intent.putExtra("eventId", eventName);
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
        fetchCancelledEntrants((ProfileAdapter) listView.getAdapter());
        // Set colors for UI
        cancelled_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
        waitlist_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
    }

    private void fetchCancelledEntrants(ProfileAdapter adapter) {
        Log.d("CancelledEntrants", "Fetching declined entrants for event: " + eventName);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notificationsRef = db.collection("notifications");

        // Fetch notifications where the type is "declined" and the event matches
        notificationsRef
                .whereEqualTo("type", "declined")
                .whereEqualTo("eventId", eventName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        profileList.clear(); // Clear the list before populating
                        List<DocumentSnapshot> notifications = task.getResult().getDocuments();

                        if (notifications.isEmpty()) {
                            Toast.makeText(this, "No declined entrants found for this event.", Toast.LENGTH_SHORT).show();
                            adapter.updateData(new ArrayList<>(profileList));
                            adapter.notifyDataSetChanged();
                            return;
                        }

                        // Fetch profile data for each declined notification
                        for (DocumentSnapshot notification : notifications) {
                            String profileId = notification.getString("profileId");

                            if (profileId != null) {
                                db.collection("waitlisted_entrants")
                                        .document(profileId)
                                        .get()
                                        .addOnSuccessListener(profileDoc -> {
                                            if (profileDoc.exists()) {
                                                String name = profileDoc.getString("profile.name");
                                                String imageUrl = profileDoc.getString("profile.imageUrl");
                                                Profile profile = new Profile(name, imageUrl);
                                                profileList.add(profile);
                                                Log.d("CancelledEntrants", "Added profile: " + name);
                                            }

                                            // Update the adapter after adding profiles
                                            adapter.updateData(new ArrayList<>(profileList));
                                            adapter.notifyDataSetChanged();
                                        })
                                        .addOnFailureListener(e -> Log.w("Firestore", "Error fetching profile for ID: " + profileId, e));
                            }
                        }
                    } else {
                        Log.w("Firestore", "Error fetching declined notifications", task.getException());
                        Toast.makeText(this, "Failed to retrieve declined entrants.", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void showCustomNotificationDialog() {
        EditText input = new EditText(this);
        input.setHint("Enter custom notification message");

        new AlertDialog.Builder(this)
                .setTitle("Custom Notification")
                .setMessage("Enter the message to send to all waitlisted entrants:")
                .setView(input)
                .setPositiveButton("Send", (dialog, which) -> {
                    String message = input.getText().toString().trim();
                    if (!message.isEmpty()) {
                        sendNotificationToEntrants(message);
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

    private void sendNotificationToEntrants(String message) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Validate eventId
        if (eventName == null || eventName.isEmpty()) {
            Log.e("Firestore", "eventId is null or empty");
            Toast.makeText(this, "Event ID is missing. Cannot send notification.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create notification data to save in Firestore
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("message", message); // Custom message
        notificationData.put("timestamp", FieldValue.serverTimestamp()); // Server timestamp
        notificationData.put("eventName", eventName); // Tag to associate with the event

        // Add the notification to the top-level notifications collection
        db.collection("notifications")
                .add(notificationData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Notification saved successfully!", Toast.LENGTH_LONG).show();
                    Log.d("Firestore", "Notification saved with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error saving notification", e);
                    Toast.makeText(this, "Failed to save notification", Toast.LENGTH_SHORT).show();
                });
    }
}