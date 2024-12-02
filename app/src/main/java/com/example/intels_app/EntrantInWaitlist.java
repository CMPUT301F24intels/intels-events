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
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import java.util.Map;

public class EntrantInWaitlist extends AppCompatActivity {
    private ListView listView;
    private List<Entrant> entrantList;
    private EntrantAdapter adapter;
    private String eventName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waitlist_with_entrants);

        // Retrieve event name from intent
        eventName = getIntent().getStringExtra("eventName");
        if (eventName == null || eventName.isEmpty()) {
            Toast.makeText(this, "Event Name is missing. Cannot proceed.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("EntrantInWaitlist", "Retrieved eventName: " + eventName);

        // Initialize views
        EditText searchBar = findViewById(R.id.search_bar);
        listView = findViewById(R.id.profile_list);
        ImageButton backButton = findViewById(R.id.back_button);

        // Initialize entrant list and adapter
        entrantList = new ArrayList<>();
        adapter = new EntrantAdapter(this, entrantList);
        listView.setAdapter(adapter);

        // Fetch entrants from Firestore
        fetchEntrants();

        // Back button action
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(EntrantInWaitlist.this, EventGridOrganizerActivity.class);
            startActivity(intent);
        });

        // Search bar listener for filtering
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s); // Filter entrants based on input
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });
    }

    /**
     * Fetches entrants from Firestore and populates the entrant list.
     */
    private void fetchEntrants() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference waitlistRef = db.collection("waitlisted_entrants");

        waitlistRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        entrantList.clear(); // Clear existing data to avoid duplicates

                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                List<Map<String, Object>> events = (List<Map<String, Object>>) documentSnapshot.get("events");
                                if (events != null) {
                                    for (Map<String, Object> event : events) {
                                        if (eventName.equals(event.get("eventName"))) {
                                            // If the event matches, fetch the profile
                                            Map<String, Object> profile = (Map<String, Object>) documentSnapshot.get("profile");
                                            if (profile != null) {
                                                String name = (String) profile.get("name");
                                                String imageUrl = (String) profile.get("imageUrl");
                                                entrantList.add(new Entrant(name, imageUrl)); // Add entrant to the list
                                                Log.d("Firestore", "Entrant added: " + name);
                                            }
                                            break; // No need to check further events for this document
                                        }
                                    }
                                }
                            }

                            if (!entrantList.isEmpty()) {
                                adapter.notifyDataSetChanged(); // Refresh the ListView
                            } else {
                                Toast.makeText(this, "No entrants found for this event.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "No entrants found for this event.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("Firestore", "Error fetching entrants", task.getException());
                        Toast.makeText(this, "Error retrieving data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
