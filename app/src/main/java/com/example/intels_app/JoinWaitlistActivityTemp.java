package com.example.intels_app;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import android.Manifest;

/**
 * This activity allows users to join an event's waitlist. It retrieves event details from the intent
 * and displays them to the user. If the event has a geolocation requirement, the user is prompted
 * to allow location access before joining the waitlist. The user can also be redirected to create
 * a profile if one doesn't exist. Upon successful addition to the waitlist, the user is navigated
 * to a success screen.
 *
 * @author Aayuhsi Shah
 */

public class JoinWaitlistActivityTemp extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;

    private FirebaseFirestore db;
    private CollectionReference profilesRef;
    private CollectionReference waitlistRef;

    private boolean geolocationRequirement;
    private String eventName;
    private String facilityName, location, dateTime, description, posterUrl;
    private int maxAttendees;

    /**
     * Called when the activity is first created.
     * Initializes UI components, retrieves event details, and sets up click listeners for joining the waitlist.
     * @param savedInstanceState Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_waitlist);

        db = FirebaseFirestore.getInstance();
        profilesRef = db.collection("profiles");
        waitlistRef = db.collection("waitlisted_entrants");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Retrieve event details from the intent
        eventName = getIntent().getStringExtra("eventName");
        facilityName = getIntent().getStringExtra("facilityName");
        location = getIntent().getStringExtra("location");
        dateTime = getIntent().getStringExtra("dateTime");
        description = getIntent().getStringExtra("description");
        maxAttendees = getIntent().getIntExtra("maxAttendees", 0);
        geolocationRequirement = getIntent().getBooleanExtra("geolocationRequirement", false);
        posterUrl = getIntent().getStringExtra("posterUrl");

        setupEventDetailsUI(eventName, facilityName, location, dateTime, description, maxAttendees, geolocationRequirement, posterUrl);

        Button joinWaitlistButton = findViewById(R.id.join_waitlist_button);
        joinWaitlistButton.setOnClickListener(view -> {
            FirebaseInstallations.getInstance().getId()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String deviceId = task.getResult();
                            Log.d("JoinWaitlist", "Retrieved Device ID: " + deviceId);

                            if (geolocationRequirement) {
                                new AlertDialog.Builder(JoinWaitlistActivityTemp.this)
                                        .setTitle("Confirm Join")
                                        .setMessage("This event tracks your geolocation. Are you sure you want to join this event?")
                                        .setPositiveButton("Yes", (dialog, which) -> {
                                            checkLocationPermissionAndFetchLocation(deviceId);
                                        })
                                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                                        .show();
                            } else {
                                checkIfProfileExists(deviceId, null);
                            }
                        } else {
                            Log.e("JoinWaitlist", "Device ID retrieval failed", task.getException());
                            Toast.makeText(this, "Error retrieving device ID. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        ImageButton backButton = findViewById(R.id.back_button_1);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(JoinWaitlistActivityTemp.this, MainActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Displays the event details in the UI, including event name, facility, location, date/time,
     * description, and geolocation requirement.
     * @param eventName            The name of the event.
     * @param facilityName         The name of the facility where the event takes place.
     * @param location             The location of the event.
     * @param dateTime             The date and time of the event.
     * @param description          The description of the event.
     * @param maxAttendees         The maximum number of attendees allowed for the event.
     * @param geolocationRequirement Whether geolocation is required for the event.
     * @param posterUrl            URL for the poster image of the event.
     */
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

        ImageView posterImageView = findViewById(R.id.camera_image);
        if (posterUrl != null && !posterUrl.isEmpty()) {
            Glide.with(this).load(posterUrl).into(posterImageView);
        }
    }

    /**
     * Checks if a user profile already exists in Firestore and either joins the waitlist or
     * redirects to profile creation if it doesn't exist.
     * @param deviceId        The device ID of the user.
     * @param geolocationData Optional geolocation data (latitude and longitude) if the event requires geolocation tracking.
     */
    private void checkIfProfileExists(String deviceId, @Nullable Map<String, Object> geolocationData) {
        profilesRef.document(deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            Profile profile = documentSnapshot.toObject(Profile.class);
                            if (profile != null) {
                                // Only call joinWaitlist if the profile exists
                                joinWaitlist(profile, geolocationData);
                            }
                        } else {
                            // Redirect to create a profile, do not call joinWaitlist here
                            redirectToCreateProfile(deviceId, geolocationData);
                        }
                    } else {
                        Log.e("JoinWaitlist", "Error checking profile existence", task.getException());
                        Toast.makeText(this, "Error accessing profile. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Adds a user profile to the event's waitlist in Firestore.
     * @param profile         The user's profile details.
     * @param geolocationData Optional geolocation data if required by the event.
     */
    private void joinWaitlist(Profile profile, @Nullable Map<String, Object> geolocationData) {
        String documentId = profile.getName();
        DocumentReference entrantDocRef = waitlistRef.document(documentId);

        // Construct event data
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventName", eventName); // Ensure eventName is included
        if (geolocationData != null) {
            eventData.putAll(geolocationData);
        }

        Log.d("JoinWaitlist", "Using eventName: " + eventName); // Debug log for eventName
        Log.d("JoinWaitlist", "Event Data: " + eventData); // Debug log for eventData

        // Construct profile data
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("deviceId", profile.getDeviceId());
        profileData.put("email", profile.getEmail());
        profileData.put("phone_number", profile.getPhone_number());
        profileData.put("imageUrl", profile.getImageUrl());
        profileData.put("name", profile.getName());
        profileData.put("notifPref", profile.isNotifPref());

        entrantDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // If the document exists, update the events array
                    entrantDocRef.update("events", FieldValue.arrayUnion(eventData))
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Successfully joined the waitlist!", Toast.LENGTH_SHORT).show();
                                navigateToSuccessScreen();
                            });
                } else {
                    // Create a new document
                    Map<String, Object> waitlistEntry = new HashMap<>();
                    waitlistEntry.put("profile", profileData);
                    waitlistEntry.put("events", Collections.singletonList(eventData)); // Add event to the list

                    entrantDocRef.set(waitlistEntry)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Successfully joined the waitlist!", Toast.LENGTH_SHORT).show();
                                navigateToSuccessScreen();
                            });
                }
            }
        });
    }

    /**
     * Redirects the user to the profile creation activity if their profile doesn't exist.
     * @param deviceId        The device ID of the user.
     * @param geolocationData Optional geolocation data if required by the event.
     */
    private void redirectToCreateProfile(String deviceId, @Nullable Map<String, Object> geolocationData) {
        Intent intent = new Intent(JoinWaitlistActivityTemp.this, SignUp.class);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("eventName", eventName);
        if (geolocationData != null) {
            intent.putExtra("latitude", (Double) geolocationData.get("latitude"));
            intent.putExtra("longitude", (Double) geolocationData.get("longitude"));
        }
        startActivityForResult(intent, 1);
    }

    /**
     * Navigates to the success screen after the user has successfully joined the waitlist.
     */
    private void navigateToSuccessScreen() {
        Intent intent = new Intent(JoinWaitlistActivityTemp.this, SuccessWaitlistJoin.class);
        startActivity(intent);
        finish();
    }

    /**
     * Handles the result from the profile creation activity.
     * @param requestCode The request code for the activity result.
     * @param resultCode  The result code returned by the profile creation activity.
     * @param data        The intent data containing profile information.
     */
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

            Double latitude = data.getDoubleExtra("latitude", 0.0);
            Double longitude = data.getDoubleExtra("longitude", 0.0);
            Map<String, Object> geolocationData = null;
            if (latitude != 0.0 && longitude != 0.0) {
                geolocationData = new HashMap<>();
                geolocationData.put("latitude", latitude);
                geolocationData.put("longitude", longitude);
            }

            // Create a Profile object
            Profile newProfile = new Profile(deviceId, name, email, phoneNumber, profilePicUrl);

            // Call joinWaitlist with the new profile and no geolocation data
            joinWaitlist(newProfile, geolocationData);
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Profile creation canceled.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Checks if location permissions are granted, and if so, fetches the user's location.
     * If permissions are not granted, requests them.
     * @param deviceId The device ID of the user.
     */
    private void checkLocationPermissionAndFetchLocation(String deviceId) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fetchLocation(deviceId);
        }
    }

    /**
     * Fetches the user's current location using the fused location client. Passes the location data
     * to profile checking logic if available.
     * @param deviceId The device ID of the user.
     */
    private void fetchLocation(String deviceId) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        // Create a map for geolocation data
                        Map<String, Object> geolocationData = new HashMap<>();
                        geolocationData.put("latitude", location.getLatitude());
                        geolocationData.put("longitude", location.getLongitude());
                        Log.d("JoinWaitlist", "Fetched Location: " + geolocationData);

                        // Pass geolocation data to checkIfProfileExists
                        checkIfProfileExists(deviceId, geolocationData);
                    } else {
                        // No location available, pass null for geolocation data
                        Log.d("JoinWaitlist", "No location available");
                        checkIfProfileExists(deviceId, null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("JoinWaitlist", "Failed to fetch location", e);
                    // Pass null for geolocation data in case of failure
                    checkIfProfileExists(deviceId, null);
                });
    }

}


