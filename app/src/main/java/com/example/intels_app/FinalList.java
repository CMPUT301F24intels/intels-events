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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

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
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_list);

        // Get the event ID from the intent
        eventId = getIntent().getStringExtra("eventId");

        eventName = getIntent().getStringExtra("eventName");
        Log.d("AcceptedEntrants", "Retrieved eventName: " + eventName);

        entrantList = findViewById(R.id.entrant_list);
        profileList = new ArrayList<>();
        ProfileAdapter adapter = new ProfileAdapter(this, profileList);
        entrantList.setAdapter(adapter);

        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(view -> {
            Intent intent = new Intent(FinalList.this, EntrantInWaitlist.class);
            intent.putExtra("eventId", eventId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        EditText searchBar = findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) { }
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

    private void fetchAcceptedEntrants(ProfileAdapter adapter) {
        // Log the event name to confirm it's being passed correctly
        Log.d("AcceptedEntrants", "Fetching accepted entrants for event: " + eventName);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notificationsRef = db.collection("notifications");

        // Fetch notifications where the type is "accepted" and eventId matches eventName
        notificationsRef
                .whereEqualTo("type", "accepted")
                .whereEqualTo("eventId", eventName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        profileList.clear(); // Clear the list before populating
                        List<DocumentSnapshot> notifications = task.getResult().getDocuments();

                        if (notifications.isEmpty()) {
                            Toast.makeText(this, "No accepted entrants found for this event.", Toast.LENGTH_SHORT).show();
                            adapter.updateData(new ArrayList<>(profileList));
                            adapter.notifyDataSetChanged();
                            return;
                        }

                        // Fetch profile data for each accepted notification
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
                                                Log.d("AcceptedEntrants", "Added profile: Name = " + name + ", ImageUrl = " + imageUrl);
                                            }

                                            // Update the adapter after adding profiles
                                            adapter.updateData(new ArrayList<>(profileList));
                                            adapter.notifyDataSetChanged();
                                        })
                                        .addOnFailureListener(e -> Log.w("Firestore", "Error fetching profile for ID: " + profileId, e));
                            } else {
                                Log.w("AcceptedEntrants", "Missing profileId in notification.");
                            }
                        }
                    } else {
                        Log.w("Firestore", "Error fetching accepted notifications", task.getException());
                        Toast.makeText(this, "Failed to retrieve accepted entrants.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

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

    private void sendNotificationToEntrants(String message) {
        Toast.makeText(this, "Notification sent: " + message, Toast.LENGTH_LONG).show();
    }
}
