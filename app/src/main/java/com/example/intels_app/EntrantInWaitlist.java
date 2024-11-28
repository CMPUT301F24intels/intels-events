/**
 * This class extends AppCompatActivity and provides a user interface to manage
 * entrants who have enrolled/joined a waitlist. This activity allows organizers
 * to view, filter, and send notifications to entrants in waitlist using a ListView
 * and search functionality.
 * @author Aayushi Shah, Katrina Alejo
 * @see com.example.intels_app.Profile Profile object
 * @see com.example.intels_app.EntrantInCancelledWaitlist Cancelled entrant information
 * @see com.example.intels_app.EventGridOrganizerActivity Organizer's gridview of events
 */

package com.example.intels_app;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntrantInWaitlist extends AppCompatActivity {
    private Button waitlist_button, cancelled_button, final_list_button, lottery_list_button, back_button;
    private ListView listView;
    private List<Profile> profileList;
    private CheckBox sendNotificationCheckbox;
    private String eventName;
    private ProfileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waitlist_with_entrants);

        eventName = getIntent().getStringExtra("eventName");

        if (eventName == null || eventName.isEmpty()) {
            Toast.makeText(this, "Event ID is missing. Cannot proceed.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("EntrantInWaitlist", "Retrieved eventName: " + eventName);

        EditText searchBar = findViewById(R.id.search_bar);
        listView = findViewById(R.id.profile_list);
        sendNotificationCheckbox = findViewById(R.id.checkbox_notify);

        profileList = new ArrayList<>();
        profileList.add(new Profile("Aayushi", "dshjfd@gmail.com", "7050404"));
        profileList.add(new Profile("Janan", "dshjfd@gmail.com", "7050404"));
        adapter = new ProfileAdapter(this, profileList);
        listView.setAdapter(adapter);

        fetchEntrants();

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EntrantInWaitlist.this, EventGridOrganizerActivity.class);
                startActivity(intent);
            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s); // Filter the adapter based on search input
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        waitlist_button = findViewById(R.id.btn_waitlist);
        cancelled_button = findViewById(R.id.btn_cancelled);
        final_list_button = findViewById(R.id.final_list_button);
        lottery_list_button = findViewById(R.id.lottery_list_button);

        waitlist_button.setOnClickListener(v -> {
            Intent intent = new Intent(EntrantInWaitlist.this, EntrantInWaitlist.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        cancelled_button.setOnClickListener(v -> {
            Intent intent = new Intent(EntrantInWaitlist.this, EntrantInCancelledWaitlist.class);
            intent.putExtra("eventName", eventName);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });



        final_list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EntrantInWaitlist.this, FinalList.class);
                intent.putExtra("eventName", eventName);
                intent.putExtra("eventName", eventName);
                startActivity(intent);
                finish();
            }
        });

        lottery_list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EntrantInWaitlist.this, LotteryList.class);
                intent.putExtra("eventName", eventName); // Pass eventName to LotteryList
                startActivity(intent);
                finish();
            }
        });

        sendNotificationCheckbox = findViewById(R.id.checkbox_notify);

        // Set up the listener for the checkbox
        sendNotificationCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showCustomNotificationDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set colors when returning to this activity
        waitlist_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
        cancelled_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
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
        CollectionReference waitlistRef = db.collection("waitlisted_entrants");

        if (eventName == null || eventName.isEmpty()) {
            Log.e("Firestore", "eventName is null or empty");
            Toast.makeText(this, "Event Name is missing. Cannot send notification.", Toast.LENGTH_SHORT).show();
            return;
        }

        waitlistRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<DocumentSnapshot> entrants = task.getResult().getDocuments();

                        if (entrants.isEmpty()) {
                            Toast.makeText(this, "No entrants found for this event.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        boolean notificationSent = false;

                        for (DocumentSnapshot entrant : entrants) {
                            // Retrieve the events array
                            List<Map<String, Object>> events = (List<Map<String, Object>>) entrant.get("events");
                            Map<String, Object> profile = (Map<String, Object>) entrant.get("profile");

                            if (events != null && profile != null) {
                                Boolean notifPref = (Boolean) profile.get("notifPref"); // Retrieve notifPref

                                // Check if notifPref is true
                                if (notifPref != null && notifPref) {
                                    for (Map<String, Object> event : events) {
                                        // Check if the eventName matches
                                        if (eventName.equals(event.get("eventName"))) {
                                            // Get the deviceId and profileId
                                            String deviceId = (String) profile.get("deviceId");
                                            String profileId = entrant.getId(); // Document ID

                                            // Send notification only if deviceId exists
                                            if (deviceId != null && !deviceId.isEmpty()) {
                                                sendNotificationToProfile(deviceId, profileId, eventName, message);
                                                notificationSent = true;
                                            } else {
                                                Log.w("Notification", "Device ID missing for profile: " + profileId);
                                            }
                                            break; // Stop checking once a match is found
                                        }
                                    }
                                } else {
                                    Log.d("Notification", "Skipping entrant due to notifPref being false or null: " + entrant.getId());
                                }
                            }
                        }

                        if (notificationSent) {
                            Toast.makeText(this, "Notifications sent successfully to all entrants.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "No eligible entrants found for this event.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("Firestore", "Error fetching entrants", task.getException());
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

    private void fetchEntrants() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference waitlistRef = db.collection("waitlisted_entrants");

        List<Entrant> entrantList = new ArrayList<>();
        EntrantAdapter adapter = new EntrantAdapter(this, entrantList);
        listView.setAdapter(adapter);

        waitlistRef.whereArrayContains("events", new HashMap<String, Object>() {{
                    put("eventName", eventName);
                }})
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        entrantList.clear(); // Clear existing data to avoid duplicates
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                Map<String, Object> profile = (Map<String, Object>) documentSnapshot.get("profile");
                                if (profile != null) {
                                    String name = (String) profile.get("name");
                                    String imageUrl = (String) profile.get("imageUrl");
                                    entrantList.add(new Entrant(name, imageUrl)); // Add entrant to the list
                                }
                            }
                            adapter.notifyDataSetChanged(); // Refresh the ListView
                        } else {
                            Toast.makeText(this, "No entrants found for this event.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("Firestore", "Error fetching entrants", task.getException());
                        Toast.makeText(this, "Error retrieving data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

        /*
        waitlistRef.whereEqualTo("eventName", eventName)  // Filter by eventName
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.w("Firestore", "Listen failed.", e);
                        Toast.makeText(this, "Error listening to changes.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    documentNames.clear(); // Helps remove duplicates

                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        Log.d("EntrantInWaitlist", "Received updated documents from waitlisted_entrants.");

                        // Add each document ID or name to the list
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String documentId = documentSnapshot.getId();  // Or get a field like "name"
                            Log.d("EntrantInWaitlist", "Document ID: " + documentId);
                            documentNames.add(documentId);  // Store document name (ID) in list
                        }

                        // Notify adapter of data change to refresh the ListView
                        adapter.notifyDataSetChanged();

                    } else {
                        Log.w("EntrantInWaitlist", "No document names found for this event.");
                        Toast.makeText(this, "No entrants found for this event.", Toast.LENGTH_SHORT).show();
                    }
                });

         */
    }

