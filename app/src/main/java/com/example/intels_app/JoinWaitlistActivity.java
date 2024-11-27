/**
 * This class allows users to view detailed information about an event
 * and provides an option to join the waitlist. This activity displays
 * event details such as name, location, facility, date and time, description,
 * maximum attendees, and geolocation requirements.
 *
 * @author Het Patel
 * @see com.example.intels_app.SelectRoleActivity Select role as entrant/guest
 */

package com.example.intels_app;

import android.app.AlertDialog;
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
import androidx.appcompat.widget.SwitchCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JoinWaitlistActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference profilesRef;
    private CollectionReference waitlistRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_waitlist);

        db = FirebaseFirestore.getInstance();
        profilesRef = db.collection("profiles");
        waitlistRef = db.collection("waitlisted_entrants");

        //        //This hardcoded data is only to TEST, REMOVE AFTER TO REAL CODE
        String eventName = "AayuJan Party";
        String facilityName = "Tech Auditorium";
        String location = "Whyte Ave, Edmonton";
        String dateTime = "2024-12-01 10:00 AM";
        String description = "A conference bringing together the brightest minds in tech.";
        int maxAttendees = 5;
        boolean geolocationRequirement = true;
        String posterUrl = "https://testingexample.com/poster.jpg";

        /*// Retrieve event details from the intent
        String eventName = getIntent().getStringExtra("eventName");
        String facilityName = getIntent().getStringExtra("facilityName");
        String location = getIntent().getStringExtra("location");
        String dateTime = getIntent().getStringExtra("dateTime");
        String description = getIntent().getStringExtra("description");
        int maxAttendees = getIntent().getIntExtra("maxAttendees", 0);
        boolean geolocationRequirement = getIntent().getBooleanExtra("geolocationRequirement", false);
        String posterUrl = getIntent().getStringExtra("posterUrl");*/

        setupEventDetailsUI(eventName, facilityName, location, dateTime, description, maxAttendees, geolocationRequirement, posterUrl);

        Button joinWaitlistButton = findViewById(R.id.join_waitlist_button);
        joinWaitlistButton.setOnClickListener(view -> {
            // Fetch the device ID
            FirebaseInstallations.getInstance().getId()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String deviceId = task.getResult();
                            Log.d("JoinWaitlist", "Retrieved Device ID: " + deviceId);

                            if (geolocationRequirement) {
                                new AlertDialog.Builder(JoinWaitlistActivity.this)
                                        .setTitle("Confirm Join")
                                        .setMessage("This event tracks your geolocation. Are you sure you want to join this event?")
                                        .setPositiveButton("Yes", (dialog, which) -> {
                                            checkIfProfileExists(deviceId, eventName);
                                        })
                                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                                        .show();
                            } else {
                                checkIfProfileExists(deviceId, eventName);
                            }
                        } else {
                            Log.e("JoinWaitlist", "Device ID retrieval failed", task.getException());
                            Toast.makeText(this, "Error retrieving device ID. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        ImageButton backButton = findViewById(R.id.back_button_1);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(JoinWaitlistActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void setupEventDetailsUI(String eventName, String facilityName, String location, String dateTime,
                                     String description, int maxAttendees, boolean geolocationRequirement, String posterUrl) {
        TextView eventNameTextView = findViewById(R.id.eventNameEdit);
        TextView facilityTextView = findViewById(R.id.facilityEdit);
        TextView locationTextView = findViewById(R.id.locationEdit);
        TextView dateTimeTextView = findViewById(R.id.dateTimeEdit);
        TextView descriptionTextView = findViewById(R.id.descriptionEdit);
        TextView maxAttendeesTextView = findViewById(R.id.max_attendees);
        SwitchCompat geolocationSwitch = findViewById(R.id.geolocationRequirementText);

        eventNameTextView.setText(String.format("Name: %s", eventName));
        facilityTextView.setText(String.format("Facility: %s", facilityName));
        locationTextView.setText(String.format("Location: %s", location));
        dateTimeTextView.setText(String.format("Date and Time: %s", dateTime));
        descriptionTextView.setText(String.format("Description: %s", description));
        maxAttendeesTextView.setText(String.format("Max Attendees: %d", maxAttendees));
        geolocationSwitch.setText(String.format("Geolocation Requirement: %s", geolocationRequirement ? "Yes" : "No"));
        geolocationSwitch.setChecked(geolocationRequirement);
        geolocationSwitch.setClickable(false);

        ImageView posterImageView = findViewById(R.id.qrCodeImage_2);
        if (posterUrl != null && !posterUrl.isEmpty()) {
            Glide.with(this).load(posterUrl).into(posterImageView);
        }
    }

    private void checkIfProfileExists(String deviceId, String eventName) {
        // Query Firestore to find a document where the `deviceId` matches
        profilesRef.whereEqualTo("deviceId", deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();

                        // Check if any documents were returned
                        if (!querySnapshot.isEmpty()) {
                            // Get the first document that matches
                            Log.d("JoinWaitlist", "Profile exists for device: " + deviceId);
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);

                            // Convert the document into a Profile object
                            Profile profile = documentSnapshot.toObject(Profile.class);
                            if (profile != null) {
                                // Pass the Profile object to joinWaitlist
                                joinWaitlist(profile, eventName);
                            } else {
                                Log.e("JoinWaitlist", "Profile object is null for device: " + deviceId);
                                Toast.makeText(this, "Error retrieving profile. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // No profile found for the given deviceId
                            Log.d("JoinWaitlist", "No profile found for device: " + deviceId);
                            redirectToCreateProfile(deviceId, eventName);
                        }
                    } else {
                        // Firestore query failed
                        Log.e("JoinWaitlist", "Error checking profile existence", task.getException());
                        Toast.makeText(this, "Error accessing profile. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("JoinWaitlist", "Error checking profile existence", e);
                    Toast.makeText(this, "Error accessing profile. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }

    private void joinWaitlist(Profile profile, String eventName) {
        // Use the profile name as the document ID
        String documentId = profile.getName();
        DocumentReference entrantDocRef = waitlistRef.document(documentId);

        // Event data to add to the events array
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventName", eventName);

        // Nested profile data
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("deviceId", profile.getDeviceId());
        profileData.put("email", profile.getEmail());
        profileData.put("phone_number", profile.getPhone_number());
        profileData.put("imageUrl", profile.getImageUrl());
        profileData.put("name", profile.getName());
        profileData.put("notifPref", profile.isNotifPref());

        // Check if the document already exists
        entrantDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("Waitlist", "Document exists. Updating events list.");

                    // If document exists, update the events array
                    entrantDocRef.update("events", FieldValue.arrayUnion(eventData))
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Waitlist", "Successfully added event to existing document.");
                                Toast.makeText(this, "Successfully joined the waitlist!", Toast.LENGTH_SHORT).show();
                                navigateToSuccessScreen();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("FirestoreError", "Error updating waitlist", e);
                                Toast.makeText(this, "Error joining the waitlist. Please try again.", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Log.d("Waitlist", "Document does not exist. Creating a new document.");

                    // If document does not exist, create a new one
                    Map<String, Object> waitlistEntry = new HashMap<>();
                    waitlistEntry.put("deviceId", profile.getDeviceId());
                    waitlistEntry.put("profile", profileData); // Add profile data
                    waitlistEntry.put("events", Collections.singletonList(eventData)); // Initialize with the first event

                    entrantDocRef.set(waitlistEntry)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Waitlist", "Successfully created new document and joined the waitlist.");
                                Toast.makeText(this, "Successfully joined the waitlist!", Toast.LENGTH_SHORT).show();
                                navigateToSuccessScreen();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("FirestoreError", "Error creating waitlist entry", e);
                                Toast.makeText(this, "Error joining the waitlist. Please try again.", Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Log.e("FirestoreError", "Error checking if document exists", task.getException());
                Toast.makeText(this, "Error joining the waitlist. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void redirectToCreateProfile(String deviceId, String eventName) {
        Intent intent = new Intent(JoinWaitlistActivity.this, SignUp.class);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("eventName", eventName);
        startActivityForResult(intent, 1); // Use a unique request code
    }

    private void navigateToSuccessScreen() {
        Intent intent = new Intent(JoinWaitlistActivity.this, SuccessWaitlistJoin.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Retrieve the created profile details
            String deviceId = data.getStringExtra("deviceId");
            String name = data.getStringExtra("name");
            String email = data.getStringExtra("email");
            String phoneNumber = data.getStringExtra("phoneNumber");
            String profilePicUrl = data.getStringExtra("profilePicUrl");
            String eventName = data.getStringExtra("eventName");

            // Create a Profile object
            Profile newProfile = new Profile(deviceId, name, email, phoneNumber, profilePicUrl);

            // Call joinWaitlist with the new profile
            joinWaitlist(newProfile, eventName);
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Profile creation canceled.", Toast.LENGTH_SHORT).show();
        }
    }
}
        /*if (eventNameTextView != null) {
            eventNameTextView.setText(eventName);
        }
        if (facilityTextView != null) {
            facilityTextView.setText(facilityName);
        }
        if (locationTextView != null) {
            locationTextView.setText(location);
        }
        if (dateTimeTextView != null) {
            dateTimeTextView.setText(dateTime);
        }
        if (descriptionTextView != null) {
            descriptionTextView.setText(description);
        }
        if (maxAttendeesTextView != null) {
            maxAttendeesTextView.setText(String.valueOf(maxAttendees));
        }*/

