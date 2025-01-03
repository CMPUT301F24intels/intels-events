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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * This class extends AppCompatActivity and provides a grid view of events for
 * the entrant, displaying events they are signed up for. This activity retrieves
 * and displays the events associated with the current device ID from Firestore
 * and includes navigation options between entrant and organizer views."
 * @author Aayushi Shah
 * @see com.example.intels_app.Event Event object
 * @see com.example.intels_app.CustomAdapterEntrant Adapter for Entrant gridview
 * @see com.example.intels_app.EventGridOrganizerActivity Organizer Event gridview
 * @see com.example.intels_app.MainActivity Main screen of app
 * @see FirebaseFirestore
 */
public class EventGridEntrantActivity extends AppCompatActivity {

    private Button entrant_button, organizer_button;
    private CustomAdapterEntrant adapter;
    private List<Event> eventData;
    private Dialog progressDialog;

    /**
     * Called when the activity is starting. Initializes the layout, views, and fetches events
     * that the current device has signed up for.
     * @param savedInstanceState Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_with_grid);

        // Initialize the GridView and set the adapter
        GridView gridView = findViewById(R.id.grid_view);
        eventData = new ArrayList<>();
        adapter = new CustomAdapterEntrant(this, eventData, null);
        gridView.setAdapter(adapter);

        FirebaseInstallations.getInstance().getId()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String currentDeviceId = task.getResult();
                        fetchSignedUpEvents(currentDeviceId);
                        adapter = new CustomAdapterEntrant(this, eventData, currentDeviceId);
                        gridView.setAdapter(adapter);
                    } else {
                        Log.e("FirebaseInstallations", "Unable to get device ID", task.getException());
                    }
                });

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventGridEntrantActivity.this, MainActivity.class); // Replace 'CurrentActivity' with your current activity's name and 'TargetActivity' with the name of the specific activity you want to return to.
                startActivity(intent);
            }
        });

        entrant_button = findViewById(R.id.entrant_button);
        organizer_button = findViewById(R.id.organizer_button);

        // Set up initial button states
        entrant_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
        organizer_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));

        // Set click listeners
        entrant_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entrant_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
                organizer_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));

                Intent intent = new Intent(EventGridEntrantActivity.this, EventGridEntrantActivity.class);
                startActivity(intent);
            }
        });

        organizer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                organizer_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
                entrant_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));

                Intent intent = new Intent(EventGridEntrantActivity.this, EventGridOrganizerActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Fetches the events that the current device has signed up for from Firestore.
     * Updates the GridView with the list of signed-up events.
     * @param deviceId The ID of the current device.
     */
    private void fetchSignedUpEvents(String deviceId) {
        showProgressDialog();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference waitlistedEventsRef = db.collection("waitlisted_entrants");

        // Query to get events where the current device is signed up
        waitlistedEventsRef.whereEqualTo("deviceId", deviceId)
                .get()  // Fetch data once instead of listening for real-time updates
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            // Clear the list to avoid duplicates
                            eventData.clear();

                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                List<Map<String, Object>> events = (List<Map<String, Object>>) documentSnapshot.get("events");
                                if (events != null) {
                                    for (Map<String, Object> eventMap : events) {
                                        // Extract event details from the map
                                        String eventName = (String) eventMap.get("eventName");

                                        // Create an Event object (adjust based on your Event class structure)
                                        Event event = new Event();
                                        event.setEventName(eventName);

                                        // Add the event to the eventData list
                                        eventData.add(event);
                                        dismissProgressDialog();
                                    }
                                }
                            }

                            // Notify adapter of the data change
                            adapter.notifyDataSetChanged();
                        } else {
                            dismissProgressDialog();
                            Log.d("Firestore", "No documents found.");
                        }
                    } else {
                        dismissProgressDialog();
                        Log.w("Firestore", "Error fetching documents", task.getException());
                    }
                });
        /*
        waitlistedEventsRef.whereEqualTo("deviceId", deviceId)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.w("Firestore", "Listen failed.", e);
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        eventData.clear(); // Clear list to avoid duplicates
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Event event = documentSnapshot.toObject(Event.class);
                            if (event != null) {
                                event.setId(documentSnapshot.getId());
                                eventData.add(event);
                            }
                        }
                        adapter.notifyDataSetChanged(); // Update the adapter with new data
                    } else {
                        Log.d("Firestore", "No documents found.");
                    }
                });
         */
    }

    /**
     * Displays a custom progress dialog to indicate that data is being loaded.
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
     * Dismisses the progress dialog if it is currently showing.
     */
    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}





