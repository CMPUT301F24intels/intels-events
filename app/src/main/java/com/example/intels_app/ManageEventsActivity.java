package com.example.intels_app;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class ManageEventsActivity extends AppCompatActivity {
    ArrayList<Event> eventData;
    CustomAdapterManageEvents adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_events);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String deviceId = task.getResult();
                        fetchEventsForDevice(deviceId);
                    } else {
                        Log.e("DeviceID", "Failed to get Firebase Instance ID", task.getException());
                        Toast.makeText(this, "Error retrieving device ID. Please try again.", Toast.LENGTH_LONG).show();
                    }
                });

        // Initialize the GridView and set the adapter
        GridView gridview = findViewById(R.id.gridViewEvents);
        eventData = new ArrayList<>();

        adapter = new CustomAdapterManageEvents(this, eventData);
        gridview.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("events");

        collectionRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {

                            // Convert document to Event object and add to eventData list
                            Event event = document.toObject(Event.class);
                            eventData.add(event);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.d("Firestore", "No documents found in this collection.");
                    }
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error fetching documents", e));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event selectedEvent = (Event) parent.getItemAtPosition(position);

                Intent intent = new Intent(ManageEventsActivity.this, EventDetailsOrganizer.class);
                intent.putExtra("eventId", selectedEvent.getId()); // Use appropriate method to get ID
                startActivity(intent);
            }
        });

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageEventsActivity.this, MainPageActivity.class);
                startActivity(intent);
            }
        });

        ImageButton addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageEventsActivity.this, AddEvent.class);
                startActivity(intent);
            }
        });

        Button manageFacilityButton = findViewById(R.id.manageFacilityButton);
        manageFacilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageEventsActivity.this, ManageFacility.class);
                startActivity(intent);
            }
        });
    }

    private void fetchEventsForDevice(String deviceId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventsRef = db.collection("events");

        eventsRef.whereEqualTo("deviceId", deviceId)
                .get()  // Use `.get()` to fetch data once
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            Log.d("Firestore", "Data received: " + task.getResult().size() + " documents");

                            // Clear the list to avoid duplicates
                            eventData.clear();

                            // Loop through the documents in the query result
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                Event event = documentSnapshot.toObject(Event.class);
                                if (event != null) {
                                    event.setId(documentSnapshot.getId());  // Set the document ID
                                    Log.d("Firestore", "Event added: " + event.getId());
                                    eventData.add(event);  // Add event to the list
                                }
                            }
                            // Notify the adapter of the data change to refresh the ListView
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("Firestore", "No documents found.");
                        }
                    } else {
                        Log.w("Firestore", "Error fetching documents", task.getException());
                    }
                });

        /*
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
         */
    }
}