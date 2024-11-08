/**
 * This class displays a list of selected entrants for a lottery event
 * in a ListView. This activity enables organizers to search for entrants,
 * view entrant profiles, and send custom notifications to selected entrants.
 * @author Katrina Alejo
 * @see com.example.intels_app.Profile Profiles class
 * @see com.example.intels_app.ProfileAdapter Adapter for profiles
 * @see com.example.intels_app.EntrantInWaitlist Entrant information for an event
 *
 */

package com.example.intels_app;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class LotteryList extends AppCompatActivity {
    private ListView entrantList;
    private List<Profile> entrantDataList;
    private ImageButton backButton;
    private CheckBox sendNotifications;
    private ProfileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lottery_list);

        // Initialize views
        entrantList = findViewById(R.id.entrant_list);
        backButton = findViewById(R.id.back_button);
        sendNotifications = findViewById(R.id.send_notifications);

        // Set up the list and adapter
        entrantDataList = new ArrayList<>();
        adapter = new ProfileAdapter(this, entrantDataList);
        entrantList.setAdapter(adapter);

        // Set up back button to go to previous activity
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(LotteryList.this, EntrantInWaitlist.class);
            startActivity(intent);
            finish();
        });

        // Set up the search bar
        EditText searchBar = findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s); // Filter adapter based on search input
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Set up the checkbox for sending notifications
        sendNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showCustomNotificationDialog();
            }
        });

        // Fetch the selected entrants from Firestore
        fetchSelectedEntrants();
    }

    /**
     * Fetch entrants from Firestore with status "selected".
     */
    private void fetchSelectedEntrants() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("waitlisted_entrants")
                .whereEqualTo("status", "selected")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        entrantDataList.clear();  // Clear the existing data
                        for (DocumentSnapshot document : task.getResult()) {
                            String name = (String) document.get("name");
                            String imageUrl = (String) document.get("imageUrl");

                            // Create a Profile object and add it to the list
                            Profile profile = new Profile(name, imageUrl);
                            entrantDataList.add(profile);
                        }
                        adapter.notifyDataSetChanged(); // Notify adapter to refresh the ListView
                    } else {
                        Toast.makeText(this, "Failed to fetch selected entrants.", Toast.LENGTH_SHORT).show();
                    }
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
                        sendNotifications.setChecked(false); // Uncheck the checkbox after sending
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
    }
}
