package com.example.intels_app;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetailsOrganizer extends AppCompatActivity {

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

        // Initialize the views
        backButton = findViewById(R.id.back_button);
        drawButton = findViewById(R.id.drawButton);
        posterImageView = findViewById(R.id.posterImageView);
        eventNameEditText = findViewById(R.id.eventNameEditText);
        facilityEditText = findViewById(R.id.facilityEditText);
        locationEditText = findViewById(R.id.locationEditText);
        dateTimeEditText = findViewById(R.id.dateTimeEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        maxAttendeesTextView = findViewById(R.id.max_attendees_textview);
        geolocationRequirementTextView = findViewById(R.id.geolocationRequirementTextView);
        notificationPreferenceTextView = findViewById(R.id.notificationPreferenceTextView);

        // Load event details
        loadEventDetails();

        // Set up the back button click listener
        backButton.setOnClickListener(view -> finish());

        // Set up the draw button click listener
        drawButton.setOnClickListener(view -> {
            // Navigate to waitlist_with_entrants.xml when Draw button is clicked
            Intent intent = new Intent(EventDetailsOrganizer.this, EntrantInWaitlist.class);
            intent.putExtra("eventId", eventId);  // Pass the event ID to the next activity
            startActivity(intent);
        });
    }

    private void loadEventDetails() {
        db = FirebaseFirestore.getInstance();
        DocumentReference documentRef = db.collection("events").document(eventId);
        documentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
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
            }
        }).addOnFailureListener(e -> Log.w(TAG, "Error getting document", e));
    }
}
