/**
 * This class displays a list of selected entrants for a lottery event
 * in a RecyclerView based on the event ID and displays each entrant's
 * profile information.
 * Author: Katrina Alejo
 * @see com.example.intels_app.Profile Profiles class
 * @see com.example.intels_app.SelectedEntrantAdapter Adapter for profiles
 */

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class LotteryList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SelectedEntrantAdapter adapter;
    private List<Profile> selectedEntrants;
    private FirebaseFirestore db;
    private String eventId;
    private CheckBox sendNotifications;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lottery_list);

        // Get the event ID from the intent
        eventId = getIntent().getStringExtra("eventId");

        if (eventId == null) {
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
            intent.putExtra("eventId", eventId); // Pass the eventId back if needed
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });



        // Search functionality
        EditText searchBar = findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);  // Filter adapter based on search input
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Send Notifications Checkbox
        sendNotifications = findViewById(R.id.send_notifications);
        sendNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showCustomNotificationDialog();
            }
        });
    }



    private void loadSelectedEntrants() {
        db.collection("selected_entrants")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    selectedEntrants.clear();
                    List<Task<DocumentSnapshot>> tasks = new ArrayList<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String profileId = doc.getString("profileId");

                        // Retrieve profile data based on profileId
                        Task<DocumentSnapshot> task = db.collection("profiles").document(profileId).get()
                                .addOnSuccessListener(profileDoc -> {
                                    if (profileDoc.exists()) {
                                        Profile profile = profileDoc.toObject(Profile.class);
                                        if (profile != null) {
                                            selectedEntrants.add(profile);
                                        }
                                    } else {
                                        Log.w("LotteryList", "Profile not found for ID: " + profileId);
                                    }
                                })
                                .addOnFailureListener(e -> Log.w("LotteryList", "Error loading profile for ID: " + profileId, e));

                        tasks.add(task);
                    }

                    // Wait for all tasks to complete, then update the adapter
                    Tasks.whenAllComplete(tasks).addOnCompleteListener(task -> {
                        adapter.notifyDataSetChanged();
                        if (!task.isSuccessful()) {
                            Log.e("LotteryList", "One or more profile load operations failed.");
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.w("LotteryList", "Error fetching selected entrants", e);
                    Toast.makeText(this, "Failed to load selected entrants.", Toast.LENGTH_SHORT).show();
                });
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
        Toast.makeText(this, "Notification sent: " + message, Toast.LENGTH_LONG).show();
        // This is where you could add code to send notifications to each profile in selectedEntrants if needed.
    }
}

