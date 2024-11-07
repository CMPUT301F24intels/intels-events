/**
 * Displays a list of all events for the admin view.
 * @author Janan Panchal
 * @see com.example.intels_app.MainPageActivity Back button leads to main page
 * @see com.example.intels_app.AdminProfiles Clicking the profiles tab leads to the admin profiles page
 * @see com.example.intels_app.CustomAdapterOrganizer Custom adapter for the grid view
 */
package com.example.intels_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.intels_app.AdminProfiles;
import com.example.intels_app.CustomAdapterOrganizer;
import com.example.intels_app.MainPageActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminEvents extends AppCompatActivity {
    ImageButton back_button;
    Button profile_button, events_button;
    GridView events_gridview;
    List<Event> list_event;

    /**
     * Displays a list of all events for the admin view.
     * Button Functionality:
     *      Back Button - return to the main page
     *      Profiles Button - switch to list of all profiles page
     * @param savedInstanceState Bundle to save the state of the activity
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_view);

        // Go back to main page if back button clicked
        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminEvents.this, MainPageActivity.class);
                startActivity(intent);
            }
        });

        // Find the grid view and initialize a list of events
        events_gridview = findViewById(R.id.events_gridview);
        list_event = new ArrayList<>();

        // Inflate the custom adapter with the list of events
        CustomAdapterOrganizer adapter = new CustomAdapterOrganizer(this, list_event);
        events_gridview.setAdapter(adapter);

        // Get all events from FireStore "events" collection and add them to the list
        FirebaseFirestore.getInstance().collection("events").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {

                            // Convert document to Event object and add to eventData list
                            Event event = document.toObject(Event.class);
                            list_event.add(event);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.d("Firestore", "No documents found in this collection.");
                    }
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error fetching documents", e));

        // Initialize buttons
        profile_button = findViewById(R.id.profile_button);
        events_button = findViewById(R.id.events_button);

        // Set button colors
        events_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
        profile_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));

        // If profile button clicked, switch to profile page
        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Switch button colors to mimic switching of tabs
                events_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
                profile_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));

                Intent intent = new Intent(AdminEvents.this, AdminProfiles.class);
                startActivity(intent);
            }
        });
    }
}
