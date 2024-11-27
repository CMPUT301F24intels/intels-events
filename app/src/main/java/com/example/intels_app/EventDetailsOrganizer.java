/**
 * This class displays the details of a specific event to the organizer,
 * allowing them to view event information, view the event poster, and
 * perform a lottery draw for selecting attendees. The class retrieves data
 * from Firebase Firestore and provides functionality to notify both selected
 * and unselected entrants.
 * @author Janan Panchal, Aayushi Shah, Katrina Alejo
 * @see com.example.intels_app.Profile Profile object
 * @see com.example.intels_app.ManageEventsActivity Manage events screen
 * @see com.example.intels_app.DrawCompleteActivity Draw completed activity
 */

package com.example.intels_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDetailsOrganizer extends AppCompatActivity {

    private static final String TAG = "EventDetailsOrganizer";

    private ImageButton backButton, drawButton, editButton;
    private ImageView posterImageView, qrImageView;
    private TextView eventNameEditText, facilityEditText, locationEditText, dateTimeEditText,
            descriptionEditText, maxAttendeesTextView, geolocationRequirementTextView, notificationPreferenceTextView;

    private FirebaseFirestore db;
    private String eventName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details);

        // Get the event name from the intent
        eventName = getIntent().getStringExtra("Event Name");

        if (eventName == null) {
            Log.e(TAG, "Event Name is missing");
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
        qrImageView = findViewById(R.id.qrImageView);
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

        editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(view -> {
            Intent intent = new Intent(EventDetailsOrganizer.this, EditEventActivity.class);
            intent.putExtra("Event Name", eventName);
            startActivity(intent);
        });
    }

    private void loadEventDetails() {
        DocumentReference documentRef = db.collection("events").document(eventName);
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

                    // Load event poster image using Glide
                    if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
                        Glide.with(getApplicationContext())
                                .load(event.getPosterUrl())
                                .placeholder(R.drawable.pfp_placeholder_image)
                                .error(R.drawable.person_image)
                                .into(posterImageView);
                    } else {
                        Log.w(TAG, "No poster URL found in the document");
                        posterImageView.setImageResource(R.drawable.person_image);
                    }

                    // Load qr code image using Glide
                    if (event.getQrCodeUrl() != null && !event.getQrCodeUrl().isEmpty()) {
                        Glide.with(getApplicationContext())
                                .load(event.getQrCodeUrl())
                                .placeholder(R.drawable.pfp_placeholder_image)
                                .error(R.drawable.person_image)
                                .into(qrImageView);
                    } else {
                        Log.w(TAG, "No poster URL found in the document");
                        qrImageView.setImageResource(R.drawable.pfp_placeholder_image);
                    }
                }
            } else {
                Log.e(TAG, "No such document exists");
            }
        }).addOnFailureListener(e -> Log.w(TAG, "Error getting document", e));
    }

    private void performLotteryDraw() {
        CollectionReference waitlistedEntrantsRef = db.collection("waitlisted_entrants");
        CollectionReference selectedEntrantsRef = db.collection("selected_entrants");
        CollectionReference notSelectedEntrantsRef = db.collection("not_selected_entrants");

        // Clear previous selected_entrants and not_selected_entrants
        selectedEntrantsRef.whereEqualTo("eventName", eventName)
                .get()
                .addOnSuccessListener(selectedSnapshot -> {
                    WriteBatch batch = db.batch();

                    for (DocumentSnapshot doc : selectedSnapshot) {
                        batch.delete(doc.getReference());
                    }

                    batch.commit().addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Cleared previous selected entrants for event: " + eventName);

                        notSelectedEntrantsRef.whereEqualTo("eventName", eventName)
                                .get()
                                .addOnSuccessListener(notSelectedSnapshot -> {
                                    WriteBatch notSelectedBatch = db.batch();

                                    for (DocumentSnapshot doc : notSelectedSnapshot) {
                                        notSelectedBatch.delete(doc.getReference());
                                    }

                                    notSelectedBatch.commit().addOnSuccessListener(clearVoid -> {
                                        Log.d(TAG, "Cleared previous not_selected entrants for event: " + eventName);

                                        // Fetch waitlisted entrants
                                        waitlistedEntrantsRef.whereEqualTo("eventName", eventName)
                                                .get()
                                                .addOnSuccessListener(waitlistQuery -> {
                                                    List<DocumentSnapshot> waitlist = waitlistQuery.getDocuments();

                                                    if (waitlist.isEmpty()) {
                                                        Toast.makeText(this, "No waitlisted profiles for this event.", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }

                                                    int numberOfSpots = Integer.parseInt(maxAttendeesTextView.getText().toString().split(": ")[1]);
                                                    Collections.shuffle(waitlist);

                                                    // Select only up to maxAttendees entrants
                                                    List<DocumentSnapshot> selectedProfiles = waitlist.subList(0, Math.min(numberOfSpots, waitlist.size()));
                                                    List<DocumentSnapshot> notSelectedProfiles = waitlist.subList(Math.min(numberOfSpots, waitlist.size()), waitlist.size());

                                                    sendNotificationsToProfiles(waitlist, selectedProfiles);
                                                    saveSelectedProfiles(selectedProfiles);
                                                    storeNotSelectedEntrants(db, notSelectedProfiles, eventName);

                                                    // After the draw, redirect to the DrawCompleteActivity
                                                    Intent intent = new Intent(EventDetailsOrganizer.this, DrawCompleteActivity.class);
                                                    intent.putExtra("eventName", eventName);
                                                    startActivity(intent);
                                                }).addOnFailureListener(e -> {
                                                    Log.e(TAG, "Error fetching waitlisted entrants", e);
                                                    Toast.makeText(this, "Failed to fetch waitlisted entrants for this event.", Toast.LENGTH_SHORT).show();
                                                });
                                    }).addOnFailureListener(e -> Log.e(TAG, "Failed to clear not_selected_entrants", e));
                                }).addOnFailureListener(e -> Log.e(TAG, "Error fetching not_selected_entrants", e));
                    }).addOnFailureListener(e -> Log.e(TAG, "Failed to delete old selected entrants", e));
                }).addOnFailureListener(e -> Log.e(TAG, "Error loading previous selected entrants", e));
    }

    private void storeNotSelectedEntrants(FirebaseFirestore db, List<DocumentSnapshot> notSelectedProfiles, String eventName) {
        for (DocumentSnapshot document : notSelectedProfiles) {
            String deviceId = document.getString("deviceId");
            String profileId = document.getId();

            if (deviceId != null && profileId != null) {
                Map<String, Object> notSelectedData = new HashMap<>();
                notSelectedData.put("eventName", eventName);
                notSelectedData.put("deviceId", deviceId);
                notSelectedData.put("profileId", profileId);
                notSelectedData.put("reconsiderForDraw", false); // Default to false
                notSelectedData.put("timestamp", FieldValue.serverTimestamp());

                db.collection("not_selected_entrants")
                        .document(profileId) // Use profile ID as the document ID
                        .set(notSelectedData)
                        .addOnSuccessListener(aVoid -> Log.d("storeNotSelectedEntrants", "Stored not_selected entrant: " +
                                "Profile ID: " + profileId + ", Event Name: " + eventName))
                        .addOnFailureListener(e -> Log.e("storeNotSelectedEntrants", "Error storing not_selected entrant: ", e));
            } else {
                Log.w("storeNotSelectedEntrants", "Missing deviceId or profileId for event: " + eventName);
            }
        }
    }

    private void sendNotificationsToProfiles(List<DocumentSnapshot> allProfiles, List<DocumentSnapshot> selectedProfiles) {
        List<DocumentSnapshot> unselectedProfiles = new ArrayList<>(allProfiles);
        unselectedProfiles.removeAll(selectedProfiles);

        for (DocumentSnapshot selectedProfile : selectedProfiles) {
            String profileId = selectedProfile.getId();
            String deviceId = selectedProfile.getString("deviceId");
            Log.d(TAG, "Selected Profile ID: " + profileId + ", Device ID: " + deviceId);

            String message = "Congratulations! You have been selected for the event!";
            sendNotificationToProfile(deviceId, profileId, eventName, message, "selected");
        }

        for (DocumentSnapshot unselectedProfile : unselectedProfiles) {
            String profileId = unselectedProfile.getId();
            String deviceId = unselectedProfile.getString("deviceId");
            Log.d(TAG, "Unselected Profile ID: " + profileId + ", Device ID: " + deviceId);

            String message = "Sorry, you were not selected this time. Please stay tuned for more events.";
            sendNotificationToProfile(deviceId, profileId, eventName, message, "not_selected");
        }

        Toast.makeText(this, "Lottery draw complete. Notifications sent.", Toast.LENGTH_SHORT).show();
    }

    private void sendNotificationToProfile(String deviceId, String profileId, String eventName, String message, String type) {
        if (deviceId == null || deviceId.isEmpty()) {
            Log.w(TAG, "Device ID is missing for profile: " + profileId);
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("deviceId", deviceId);
        notificationData.put("profileId", profileId);
        notificationData.put("eventName", eventName);
        notificationData.put("message", message);
        notificationData.put("timestamp", FieldValue.serverTimestamp());
        notificationData.put("type", type);

        db.collection("notifications")
                .add(notificationData)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Notification sent with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error sending notification", e));
    }

    private void saveSelectedProfiles(List<DocumentSnapshot> selectedProfiles) {
        CollectionReference selectedEntrantsRef = db.collection("selected_entrants");

        for (DocumentSnapshot profile : selectedProfiles) {
            Map<String, Object> entrantData = new HashMap<>();
            entrantData.put("profileId", profile.getId());
            entrantData.put("eventName", eventName);
            entrantData.put("timestamp", FieldValue.serverTimestamp());

            selectedEntrantsRef.add(entrantData)
                    .addOnSuccessListener(documentReference -> Log.d(TAG, "Selected entrant saved with ID: " + documentReference.getId()))
                    .addOnFailureListener(e -> Log.w(TAG, "Error saving selected entrant", e));
        }
    }


}
