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
    private Button waitlist_button, cancelled_button, final_list_button, back_button;
    private ListView listView;
    private List<Profile> profileList;
    private CheckBox sendNotificationCheckbox;
    private String eventName;
    private ProfileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waitlist_with_entrants);

        eventName = getIntent().getStringExtra("eventId");

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
        profileList.add(new Profile("Aayushi", "dshjfd@gmail.com", 7050404));
        profileList.add(new Profile("Janan", "dshjfd@gmail.com", 7050404));
        adapter = new ProfileAdapter(this, profileList);
        listView.setAdapter(adapter);

        fetchEntrants();

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EntrantInWaitlist.this, EventGridOrganizerActivity.class);
                startActivity(intent);
                finish();
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

        cancelled_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
        waitlist_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));

        waitlist_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelled_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
                waitlist_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));

                Intent intent = new Intent(EntrantInWaitlist.this, EntrantInWaitlist.class);
                startActivity(intent);
                finish();
            }
        });

        cancelled_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelled_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
                waitlist_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));

                Intent intent = new Intent(EntrantInWaitlist.this, EntrantInCancelledWaitlist.class);
                startActivity(intent);
                finish();
            }
        });
        final_list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EntrantInWaitlist.this, FinalList.class);
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

    private void fetchEntrants() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference waitlistRef = db.collection("waitlisted_entrants");

        List<String> documentNames = new ArrayList<>();
        EntrantAdapter adapter = new EntrantAdapter(this, documentNames);
        listView.setAdapter(adapter);

        waitlistRef.whereEqualTo("eventName", eventName)  // Filter by eventName
                .get()  // Use `.get()` to fetch data once
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Clear existing data to avoid duplicates
                        documentNames.clear();

                        // Check if the task returned any documents
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            Log.d("EntrantInWaitlist", "Documents retrieved for waitlisted entrants.");

                            // Iterate over documents in the result
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                String documentId = documentSnapshot.getId();  // Or fetch a specific field like "name"
                                Log.d("EntrantInWaitlist", "Document ID: " + documentId);
                                documentNames.add(documentId);  // Add document ID (or name) to list
                            }

                            // Notify the adapter to refresh the ListView
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.w("EntrantInWaitlist", "No documents found for this event.");
                            Toast.makeText(this, "No entrants found for this event.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.w("Firestore", "Error fetching documents", task.getException());
                        Toast.makeText(this, "Error retrieving data.", Toast.LENGTH_SHORT).show();
                    }
                });

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
                /*
         */
    }}
