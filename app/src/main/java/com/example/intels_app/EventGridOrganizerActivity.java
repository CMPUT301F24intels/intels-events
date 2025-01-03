package com.example.intels_app;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
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
/**
 * This class extends AppCompatActivity and provides a grid view of events for
 * the organizer, displaying events they created in Manage Events. This activity
 * retrieves and displays the events associated with the current device ID from
 * Firestore and includes navigation options between entrant and organizer views."
 * @author Aayushi Shah
 * @see com.example.intels_app.Event Event object
 * @see com.example.intels_app.CustomAdapterOrganizer Adapter for Organizer gridview
 * @see com.example.intels_app.EventGridEntrantActivity Entrant Event gridview
 * @see com.example.intels_app.MainActivity Main screen of app
 * @see FirebaseFirestore
 */
public class EventGridOrganizerActivity extends AppCompatActivity {

    private Button entrant_button, organizer_button;
    private CustomAdapterOrganizer adapter;
    private ArrayList<Event> eventData;
    private Dialog progressDialog;

    /**
     * Called when the activity is starting. Initializes the layout, views, and fetches events
     * created by the current device (organizer).
     * @param savedInstanceState Bundle contains the data it most recently supplied.
     */
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
        adapter = new CustomAdapterOrganizer(this, eventData, position -> {
            Intent intent = new Intent(EventGridOrganizerActivity.this, EntrantInWaitlist.class);
            intent.putExtra("eventName", eventData.get(position).getEventName());
            startActivity(intent);
        });
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
                String selectedEventName = selectedEvent.getEventName(); // Get event ID

                Log.d("EventGridOrganizerActivity", "Selected Event ID: " + selectedEventName);

                Intent intent = new Intent(EventGridOrganizerActivity.this, EntrantInWaitlist.class);
                intent.putExtra("eventName", selectedEventName); // Pass the event ID
                startActivity(intent);
            }
        });

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventGridOrganizerActivity.this, MainActivity.class);
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

    /**
     * Fetches the events that the current device has created from Firestore.
     * Updates the GridView with the list of events.
     * @param deviceId The ID of the current device.
     */
    private void fetchEventsForDevice(String deviceId) {
        showProgressDialog();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventsRef = db.collection("events");

        eventsRef.whereEqualTo("deviceId", deviceId)
                .get()  // Use `.get()` to fetch data once instead of listening for changes
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
                                    event.setId(documentSnapshot.getId()); // Set the document ID
                                    Log.d("Firestore", "Event added: " + event.getId());
                                    eventData.add(event); // Add the event to the list
                                    dismissProgressDialog();
                                }
                            }
                            // Notify the adapter of the data change to refresh the UI
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