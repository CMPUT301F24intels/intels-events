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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_list);

        // Retrieve eventName from the Intent
        eventName = getIntent().getStringExtra("eventName");
        Log.d("AcceptedEntrants", "Retrieved eventName: " + eventName);

        entrantList = findViewById(R.id.entrant_list);
        profileList = new ArrayList<>();
        ProfileAdapter adapter = new ProfileAdapter(this, profileList);
        entrantList.setAdapter(adapter);

        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(view -> {
            Intent intent = new Intent(FinalList.this, EventGridOrganizerActivity.class);
            startActivity(intent);
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
        CollectionReference entrantsRef = db.collection("waitlisted_entrants");

        entrantsRef.whereEqualTo("eventName", eventName) // Ensure it matches the current event
                .whereEqualTo("status", "accepted") // Filter for accepted status
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        profileList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            String name = document.getString("profile.name");
                            String imageUrl = document.getString("profile.imageUrl");
                            Profile profile = new Profile(name, imageUrl);
                            profileList.add(profile);
                            Log.d("AcceptedEntrants", "Added profile: Name = " + name + ", ImageUrl = " + imageUrl);
                        }
                        adapter.updateData(new ArrayList<>(profileList));
                        adapter.notifyDataSetChanged();
                        Log.d("AcceptedEntrants", "Profile list size: " + profileList.size());

                        if (profileList.isEmpty()) {
                            Toast.makeText(this, "No accepted entrants found for this event.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.w("Firestore", "Error fetching accepted entrants", task.getException());
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
