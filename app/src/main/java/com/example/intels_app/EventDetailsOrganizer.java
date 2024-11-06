package com.example.intels_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDetailsOrganizer extends AppCompatActivity {

    private static final String TAG = "EventDetailsOrganizer";

    private ImageButton backButton, drawButton;
    private ImageView posterImageView;
    private TextView eventNameEditText, facilityEditText, locationEditText, dateTimeEditText,
            descriptionEditText, maxAttendeesTextView, geolocationRequirementTextView, notificationPreferenceTextView;

    private FirebaseFirestore db;
    private String eventId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details);

        // Get the event ID from the intent
        eventId = getIntent().getStringExtra("eventId");

        if (eventId == null) {
            Log.e(TAG, "Event ID is missing");
            finish();
            return;
        }

        // Get views from layout
        eventNameEditText = findViewById(R.id.eventNameEditText);
        facilityEditText = findViewById(R.id.facilityEditText);
        locationEditText = findViewById(R.id.locationEditText);
        dateTimeEditText = findViewById(R.id.dateTimeEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        maxAttendeesTextView = findViewById(R.id.max_attendees_textview);
        geolocationRequirementTextView = findViewById(R.id.geolocationRequirementTextView);
        notificationPreferenceTextView = findViewById(R.id.notificationPreferenceTextView);
        posterImageView = findViewById(R.id.posterImageView);
        drawButton = findViewById(R.id.drawButton);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Load event details from Firestore
        loadEventDetails();

        // Set up Draw Button functionality
        drawButton.setOnClickListener(view -> performLotteryDraw());

        // Back button to navigate back to the manage events screen
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(EventDetailsOrganizer.this, ManageEventsActivity.class);
            startActivity(intent);
        });
    }

    private void loadEventDetails() {
        DocumentReference documentRef = db.collection("events").document(eventId);
        documentRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Event event = documentSnapshot.toObject(Event.class);
                if (event != null) {
                    // Populate the UI with event details
                    eventNameEditText.setText("Event Name: " + event.getEventName());
                    facilityEditText.setText("Facility: " + event.getFacilityName());
                    locationEditText.setText("Location: " + event.getLocation());
                    dateTimeEditText.setText("Date and Time: " + event.getDateTime());
                    descriptionEditText.setText("Description: " + event.getDescription());
                    maxAttendeesTextView.setText("Max Attendees: " + event.getMaxAttendees());
                    geolocationRequirementTextView.setText("Geolocation Requirement: " + event.isGeolocationRequirement());
                    notificationPreferenceTextView.setText("Notification Preference: " + event.isNotifPreference());

                    if (event.getPosterUrl() != null) {
                        Glide.with(getApplicationContext()).load(event.getPosterUrl()).into(posterImageView);
                    } else {
                        Log.w(TAG, "No poster URL found in the document");
                    }
                }
            } else {
                Log.e(TAG, "No such document exists");
            }
        }).addOnFailureListener(e -> Log.w(TAG, "Error getting document", e));
    }

    private void performLotteryDraw() {
        CollectionReference profilesRef = db.collection("profiles");

        profilesRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<DocumentSnapshot> profileList = queryDocumentSnapshots.getDocuments();

            if (profileList.isEmpty()) {
                Toast.makeText(this, "No profiles in the database.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get the number of spots from the max attendees field
            int numberOfSpots = Integer.parseInt(maxAttendeesTextView.getText().toString().split(": ")[1]);

            // Randomly shuffle the list and select the required number of participants
            Collections.shuffle(profileList);
            List<DocumentSnapshot> selectedProfiles = profileList.subList(0, Math.min(numberOfSpots, profileList.size()));

            for (DocumentSnapshot profile : selectedProfiles) {
                // selected profiles in the database
                profile.getReference().update("status", "selected")
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Profile selected: " + profile.getId()))
                        .addOnFailureListener(e -> Log.w(TAG, "Failed to update profile status", e));
            }

            // Send notifications to selected and unselected profiles
            sendNotificationsToProfiles(profileList, selectedProfiles);

            // After the draw, redirect to the waiting list with selected entrants
            Intent intent = new Intent(EventDetailsOrganizer.this, EntrantInWaitlist.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching profiles", e);
            Toast.makeText(this, "Failed to fetch profiles.", Toast.LENGTH_SHORT).show();
        });
    }

    private void sendNotificationsToProfiles(List<DocumentSnapshot> allProfiles, List<DocumentSnapshot> selectedProfiles) {
        List<DocumentSnapshot> unselectedProfiles = new ArrayList<>(allProfiles);
        unselectedProfiles.removeAll(selectedProfiles);

        // Notify selected profiles
        for (DocumentSnapshot selectedProfile : selectedProfiles) {
            String profileId = selectedProfile.getId();
            String message = "Congratulations! You have been selected for the event!";
            sendNotificationToProfile(profileId, eventId, message, "selected");
        }

        // Notify unselected profiles
        for (DocumentSnapshot unselectedProfile : unselectedProfiles) {
            String profileId = unselectedProfile.getId();
            String message = "Sorry, you were not selected this time. Please stay tuned for more events.";
            sendNotificationToProfile(profileId, eventId, message, "not_selected");
        }

        Toast.makeText(this, "Lottery draw complete. Notifications sent.", Toast.LENGTH_SHORT).show();
    }

    private void sendNotificationToProfile(String profileId, String eventId, String message, String type) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create notification data
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("profileId", profileId);
        notificationData.put("eventId", eventId);
        notificationData.put("message", message);
        notificationData.put("timestamp", FieldValue.serverTimestamp());
        notificationData.put("type", type);

        // Add notification to Firestore under "notifications" collection
        db.collection("notifications")
                .add(notificationData)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Notification sent with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error sending notification", e));
    }
}

