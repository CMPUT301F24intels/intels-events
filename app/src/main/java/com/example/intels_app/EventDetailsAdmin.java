/**
 * This class displays the details of a specific event to the organizer,
 * allowing them to view event information, view the event poster, and delete images.
 * The class retrieves and removes data from FireStore.
 * @author Janan Panchal
 * @see com.example.intels_app.Event Event object
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
import com.google.android.gms.tasks.OnSuccessListener;
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

public class EventDetailsAdmin extends AppCompatActivity {

    private static final String TAG = "EventDetailsOrganizer";
    private Event event;
    private ImageButton backButton;
    private Button deleteQRButton, deletePosterButton;
    private ImageView posterImageView, qrImageView;
    private TextView eventNameEditText, facilityEditText, locationEditText, dateTimeEditText,
            descriptionEditText, maxAttendeesTextView, geolocationRequirementTextView, notificationPreferenceTextView;

    private FirebaseFirestore db;
    private String eventName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_event_details);

        // Get the event name from the intent
        eventName = getIntent().getStringExtra("Event Name");

        FirebaseFirestore.getInstance().collection("events").document(eventName).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        event = documentSnapshot.toObject(Event.class);
                    }
                });

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

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Load event details from Firestore
        loadEventDetails();

        // Back button to navigate back to the manage events screen
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(EventDetailsAdmin.this, AdminEvents.class);
            startActivity(intent);
        });

        deleteQRButton = findViewById(R.id.remove_qr_button);
        deleteQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Remove image from storage
                FirebaseStorage.getInstance().getReferenceFromUrl(event.getQrCodeUrl()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                // Remove QR Code URL from event details
                                FirebaseFirestore.getInstance().collection("events").document(eventName)
                                        .update("qrCodeUrl", null)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d(TAG, "QR code URL removed successfully");
                                                qrImageView.setImageResource(R.drawable.pfp_placeholder_image);

                                            }
                                        });
                            }
                        });
            }
        });

        deletePosterButton = findViewById(R.id.remove_poster_button);
        deletePosterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Remove image from storage
                FirebaseStorage.getInstance().getReferenceFromUrl(event.getPosterUrl()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                // Remove poster URL from event details
                                FirebaseFirestore.getInstance().collection("events").document(eventName)
                                        .update("posterUrl", null)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d(TAG, "Poster URL removed successfully");
                                                posterImageView.setImageResource(R.drawable.pfp_placeholder_image);
                                            }
                                        });
                            }
                        });
            }
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
}
