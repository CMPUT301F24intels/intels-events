package com.example.intels_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.ArrayList;
import java.util.List;

public class EventGridOrganizerActivity extends AppCompatActivity {

    private Button entrant_button, organizer_button;
    private CustomAdapterOrganizer adapter;
    private List<Event> eventData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_with_grid);

        FirebaseInstallations.getInstance().getId()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String currentDeviceId = task.getResult();
                        fetchEventsForDevice(currentDeviceId);
                    } else {
                        Log.e("FirebaseInstallations", "Unable to get device ID", task.getException());
                    }
                });

        // Initialize the GridView and set the adapter
        GridView gridView = findViewById(R.id.grid_view);
        eventData = new ArrayList<>();
        adapter = new CustomAdapterOrganizer(this, eventData);
        gridView.setAdapter(adapter);

        /*List<Event> eventData = new ArrayList<>();
        eventData.add(new Event("1", "Sample Event 1"));
        eventData.add(new Event("2", "Sample Event 2"));
        eventData.add(new Event("3", "Sample Event 3"));
        eventData.add(new Event("4", "Sample Event 4"));
        eventData.add(new Event("5", "Sample Event 5"));*/

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event selectedEvent = (Event) parent.getItemAtPosition(position);
                String selectedEventId = selectedEvent.getId(); // Get event ID

                Log.d("EventGridOrganizerActivity", "Selected Event ID: " + selectedEventId);

                Intent intent = new Intent(EventGridOrganizerActivity.this, EntrantInWaitlist.class);
                intent.putExtra("eventId", selectedEventId); // Pass the event ID
                startActivity(intent);
            }
        });

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventGridOrganizerActivity.this, MainPageActivity.class);
                startActivity(intent);
            }
        });

        entrant_button = findViewById(R.id.entrant_button);
        organizer_button = findViewById(R.id.organizer_button);

        // Set up initial button states
        entrant_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
        organizer_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));

        // Set click listeners
        entrant_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entrant_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
                organizer_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));

                Intent intent = new Intent(EventGridOrganizerActivity.this, EventGridEntrantActivity.class);
                startActivity(intent);
            }
        });

        organizer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                organizer_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
                entrant_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
            }
        });
    }

    private void fetchEventsForDevice(String deviceId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventsRef = db.collection("events");

        eventsRef.whereEqualTo("deviceId", deviceId)
            .addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (e != null) {
                    Log.w("Firestore", "Listen failed.", e);
                    return;
                }

                if (queryDocumentSnapshots != null) {
                    Log.d("Firestore", "Data received: " + queryDocumentSnapshots.size() + " documents");
                    eventData.clear(); // Clear list to avoid duplicates
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Event event = documentSnapshot.toObject(Event.class);
                        if (event != null) {
                            event.setId(documentSnapshot.getId()); // Set ID from Firestore document ID
                            Log.d("Firestore", "Event added: " + event.getId());
                            eventData.add(event); // Add event to the list
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("Firestore", "No documents found.");
                }
            });
    }
}