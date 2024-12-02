package com.example.intels_app;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Displays a list of all events for the admin view.
 * @author Janan Panchal
 * @see com.example.intels_app.MainActivity Back button leads to main page
 * @see com.example.intels_app.AdminProfiles Clicking the profiles tab leads to the admin profiles page
 * @see com.example.intels_app.CustomAdapterOrganizer Custom adapter for the grid view
 */

public class AdminEvents extends AppCompatActivity {
    private Button profile_button;
    private Button events_button;
    private Button facilities_button;
    private ArrayList<Event> list_event;
    private Dialog progressDialog;

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
        ImageButton back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminEvents.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Find the grid view and initialize a list of events
        GridView events_gridview = findViewById(R.id.events_gridview);

        list_event = new ArrayList<>();

        Button BrowserButton = findViewById(R.id.image_browser_button);
        BrowserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminEvents.this, ImageEventBrowserActivity.class);
                startActivity(intent);
            }
        });

        // Inflate the custom adapter with the list of events
        CustomAdapterManageEvents adapter = new CustomAdapterManageEvents(this, list_event, position -> {
            Intent intent = new Intent(AdminEvents.this, EventDetailsAdmin.class);
            intent.putExtra("Event Name", list_event.get(position).getEventName());
            startActivity(intent);
        });
        events_gridview.setAdapter(adapter);

        showProgressDialog();
        // Get all events from FireStore "events" collection and add them to the list
        FirebaseFirestore.getInstance().collection("events").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    dismissProgressDialog();

                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {

                            // Convert document to Event object and add to eventData list
                            Event event = document.toObject(Event.class);
                            list_event.add(event);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        dismissProgressDialog();
                        Log.d("Firestore", "No documents found in this collection.");
                    }
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error fetching documents", e));

        // Initialize buttons
        profile_button = findViewById(R.id.profile_button);
        events_button = findViewById(R.id.events_button);
        facilities_button = findViewById(R.id.facilities_button);

        // Set button colors
        events_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
        profile_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
        facilities_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));

        // If profile button clicked, switch to profile page
        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Switch button colors to mimic switching of tabs
                events_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
                profile_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
                facilities_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));

                Intent intent = new Intent(AdminEvents.this, AdminProfiles.class);
                startActivity(intent);
            }
        });

        // If facilities button clicked, switch to facilities page
        facilities_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Change button colour to mimic switching of tabs
                events_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
                profile_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
                facilities_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));

                Intent intent = new Intent(AdminEvents.this, AdminFacilities.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Displays the dialog with the loading circle while data is being fetched from Firestore
     */
    private void showProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.dialog_progress_bar, null);
        builder.setView(customLayout);
        builder.setCancelable(false);

        // Create and show the dialog
        progressDialog = builder.create();
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Ensure the dialog appears as a square
        progressDialog.setOnShowListener(dialog -> {
            if (progressDialog.getWindow() != null) {
                progressDialog.getWindow().setLayout(400, 400); // Set width and height to match layout
            }
        });

        progressDialog.show();
    }

    /**
     * Stops the dialog with the loading circle
     */
    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
