package com.example.intels_app;

import static android.content.ContentValues.TAG;

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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.ArrayList;
/**
 * This class displays the manage events page which shows the events of an organizer
 * @author Janan Panchal
 * @see com.example.intels_app.EventDetailsOrganizer Event details page for event just created
 * @see com.example.intels_app.AddEvent Add event page
 * @see com.example.intels_app.ManageFacility Manage facility page
 * @see com.example.intels_app.MainActivity Main page
 * @see com.example.intels_app.Event Event object
 * @see com.example.intels_app.CustomAdapterManageEvents Custom adapter for dispaying events
 */
public class ManageEventsActivity extends AppCompatActivity {
    ArrayList<Event> eventData;
    CustomAdapterManageEvents adapter;
    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_events);

        FirebaseInstallations.getInstance().getId()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String currentDeviceId = task.getResult();
                        Log.d(TAG, "Device ID: " + currentDeviceId);
                        checkIfFacilityExists(currentDeviceId);
                        fetchEventsForDevice(currentDeviceId);
                    } else {
                        Log.e("FirebaseInstallations", "Unable to get device ID", task.getException());
                    }
                });

        // Initialize the GridView and set the adapter
        GridView gridview = findViewById(R.id.gridViewEvents);
        eventData = new ArrayList<>();

        adapter = new CustomAdapterManageEvents(this, eventData, position -> {
            Intent intent = new Intent(ManageEventsActivity.this, EventDetailsOrganizer.class);
            intent.putExtra("Event Name", eventData.get(position).getEventName());
            startActivity(intent);
        });
        gridview.setAdapter(adapter);

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

        Button manageFacilityButton = findViewById(R.id.manageFacilityButton);
        manageFacilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageEventsActivity.this, ManageFacility.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Checks whether a user profile exists for the given device ID.
     * If a user profile exists, proceeds to the main functionality.
     * If not, redirects the user to the profile creation screen.
     *
     * @param deviceId The unique device ID retrieved from Firebase.
     */
    private void checkIfFacilityExists(String deviceId) {
        FirebaseFirestore.getInstance().collection("facilities")
                .whereEqualTo("deviceId", deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful() || task.getResult().isEmpty() || task.getResult() == null) {

                        // User does not exist, redirect to profile creation
                        // redirectToCreateEntrantProfile(deviceId); // Uncomment line to implement
                        redirectToCreateOrganizerProfile(deviceId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error checking user existence", e);
                    Toast.makeText(this, "Error accessing user information. Please try again.", Toast.LENGTH_LONG).show();
                });
    }

    /**
     * Redirects the user to the organizer profile creation activity if they are new.
     * Passes the device ID to the profile creation activity.
     *
     * @param deviceId The unique device ID to be used in {@link CreateFacility}.
     */
    // Method to redirect to create profile if user is new
    private void redirectToCreateOrganizerProfile(String deviceId) {
        Intent intent = new Intent(ManageEventsActivity.this, CreateFacility.class); //Changed this line make it back to CreateFacility
        Log.d("Firestore", "Redirecting to CreateProfileActivity with device ID: " + deviceId);
        intent.putExtra("deviceId", deviceId); // Pass device ID if needed in CreateProfileActivity
        startActivity(intent);
        finish(); // Close MainActivity
    }

    private void fetchEventsForDevice(String deviceId) {
        showProgressDialog(); // Show progress dialog

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventsRef = db.collection("events");

        Log.d("Firestore", "Fetching events for device: " + deviceId);
        eventsRef.whereEqualTo("deviceId", deviceId)
                .get()  // Use `.get()` to fetch data once instead of listening for changes
                .addOnCompleteListener(task -> {
                    dismissProgressDialog(); // Dismiss progress dialog when done

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
                                }
                            }
                            // Notify the adapter of the data change to refresh the UI
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("Firestore", "No documents found.");
                        }
                    } else {
                        dismissProgressDialog(); // Ensure dismissal on failure
                        Log.w("Firestore", "Error fetching documents", task.getException());
                    }
                });
    }

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

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}