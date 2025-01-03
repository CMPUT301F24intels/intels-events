package com.example.intels_app;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class EventDetailsOrganizer extends AppCompatActivity {

    private static final String TAG = "EventDetailsOrganizer";

    private ImageButton backButton, drawButton, editButton, deleteButton;
    private ImageView posterImageView, qrImageView;
    private TextView eventNameEditText, facilityEditText, locationEditText, dateTimeEditText,
            descriptionEditText, maxAttendeesTextView, geolocationRequirementTextView;

    private FirebaseFirestore db;
    private String eventName;

    /**
     * Initializes the layout, retrieves event details from Firestore, and sets up UI elements and click listeners.
     * @param savedInstanceState Bundle contains the saved data.
     */
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

        deleteButton = findViewById(R.id.infoButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(EventDetailsOrganizer.this)
                        .setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete this event?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Delete event-related operations
                            deleteEvent();
                            Intent intent = new Intent(EventDetailsOrganizer.this, ManageEventsActivity.class);
                            startActivity(intent);
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            // Dismiss the dialog if the user cancels
                            dialog.dismiss();
                        })
                        .show();
            }
        });

        posterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageDialog(((ImageView) view).getDrawable());
            }
        });

        qrImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageDialog(((ImageView) view).getDrawable());
            }
        });

        // Get the navigation button
        ImageButton navigationButton = findViewById(R.id.navigationButton);
        navigationButton.setOnClickListener(v -> {
            if (eventName != null && !eventName.isEmpty()) {
                Intent intent = new Intent(EventDetailsOrganizer.this, MapsActivity.class);
                intent.putExtra("eventName", eventName);
                startActivity(intent);
            } else {
                Toast.makeText(EventDetailsOrganizer.this, "Event name is missing. Cannot open map.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Event name is missing for navigation to MapsActivity.");
            }
        });

    }

    /**
     * Loads event details from Firestore and populates the UI components with the retrieved data.
     */
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

    /**
     * Deletes the event from Firestore and Firebase Storage.
     */
    private void deleteEvent() {
        Log.d(TAG, "Deleting event: " + eventName);
        String eventToDelete = eventName;

        FirebaseFirestore.getInstance().collection("events")
                .document(eventToDelete)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Event event = documentSnapshot.toObject(Event.class);
                    Log.d(TAG, "Event data retrieved: " + event.getEventName());

                    if (event != null) {
                        Log.d(TAG, "Event data is not null.");

                        if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
                            // Delete poster and QR code from Firebase Storage
                            FirebaseStorage.getInstance().getReferenceFromUrl(event.getPosterUrl()).delete()
                                    .addOnSuccessListener(unused -> Log.d(TAG, "Poster successfully deleted."))
                                    .addOnFailureListener(e -> Log.w(TAG, "Failed to delete poster.", e));
                        }

                        if (event.getQrCodeUrl() != null && !event.getQrCodeUrl().isEmpty()) {
                            FirebaseStorage.getInstance().getReferenceFromUrl(event.getQrCodeUrl()).delete()
                                    .addOnSuccessListener(unused -> Log.d(TAG, "QR successfully deleted."))
                                    .addOnFailureListener(e -> Log.w(TAG, "Failed to delete QR code.", e));
                        }

                        // Delete event from Firestore
                        FirebaseFirestore.getInstance().collection("events")
                                .document(eventToDelete)
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    Toast.makeText(EventDetailsOrganizer.this, "Event deleted", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
                    } else {
                        Log.w(TAG, "Event data is null, cannot delete.");
                        Toast.makeText(EventDetailsOrganizer.this, "Failed to retrieve event data", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.w(TAG, "Failed to fetch event details for deletion", e));
    }

    /**
     * Performs a lottery draw to select entrants for the event.
     * It selects entrants from the waitlisted profiles and stores the selected and not-selected entrants separately.
     */
    private void performLotteryDraw() {
        CollectionReference waitlistedEntrantsRef = db.collection("waitlisted_entrants");
        CollectionReference selectedEntrantsRef = db.collection("selected_entrants");
        CollectionReference notSelectedEntrantsRef = db.collection("not_selected_entrants");

        // Clear previous `selected_entrants` and `not_selected_entrants`
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

                                        // Fetch all waitlisted entrants and filter locally
                                        waitlistedEntrantsRef.get().addOnSuccessListener(waitlistQuery -> {
                                            List<DocumentSnapshot> waitlist = new ArrayList<>();

                                            for (DocumentSnapshot document : waitlistQuery.getDocuments()) {
                                                // Extract the `events` array and filter locally
                                                List<Map<String, Object>> events = (List<Map<String, Object>>) document.get("events");
                                                if (events != null) {
                                                    for (Map<String, Object> event : events) {
                                                        if (eventName.equals(event.get("eventName"))) {
                                                            waitlist.add(document);
                                                            break; // Stop checking other events for this document
                                                        }
                                                    }
                                                }
                                            }

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

    /**
     * Displays an enlarged view of the selected image.
     * @param imageDrawable The Drawable object of the image to be displayed.
     */
    private void showImageDialog(Drawable imageDrawable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_expand_image, null);
        ImageView enlargedImageView = dialogView.findViewById(R.id.enlargedImageView);

        enlargedImageView.setImageDrawable(imageDrawable);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Close dialog when clicked
        enlargedImageView.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /**
     * Stores the profiles that were not selected during the lottery draw in the "not_selected_entrants" Firestore collection.
     * @param db                The Firestore database instance.
     * @param notSelectedProfiles The list of profiles that were not selected.
     * @param eventName         The name of the event for which the draw was conducted.
     */
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

    /**
     * Sends notifications to both the selected and unselected profiles after a lottery draw.
     * @param allProfiles     The list of all profiles that were considered in the lottery draw.
     * @param selectedProfiles The list of profiles that were selected during the draw.
     */
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

    /**
     * Sends a notification to a specific profile.
     * @param deviceId   The unique device ID of the recipient.
     * @param profileId  The ID of the profile receiving the notification.
     * @param eventName  The name of the event for which the notification is being sent.
     * @param message    The message to be sent in the notification.
     * @param type       The type of the notification (e.g., "selected", "not_selected").
     */
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

    /**
     * Saves the profiles that were selected during the lottery draw in the "selected_entrants" Firestore collection.
     * @param selectedProfiles The list of profiles that were selected.
     */
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
