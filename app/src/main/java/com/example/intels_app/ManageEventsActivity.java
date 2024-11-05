package com.example.intels_app;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;

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

import java.util.ArrayList;

public class ManageEventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_events);

        // Initialize the GridView and set the adapter
        GridView gridview = findViewById(R.id.gridViewEvents);
        ArrayList<Event> eventData = new ArrayList<>();
        CustomAdapterManageEvents adapter = new CustomAdapterManageEvents(this, eventData);
        gridview.setAdapter(adapter);

        // Get info from Firestore
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

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageEventsActivity.this, MainActivity.class);
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

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event selectedEvent = (Event) parent.getItemAtPosition(position);
                Log.d("ManageEventsActivity", "Clicked event: " + selectedEvent.getEventName());

                Intent intent = new Intent(ManageEventsActivity.this, EventDetailsOrganizer.class);
                intent.putExtra("Event Name", selectedEvent.getEventName()); // Send event info
                startActivity(intent);
            }
        });
    }
}