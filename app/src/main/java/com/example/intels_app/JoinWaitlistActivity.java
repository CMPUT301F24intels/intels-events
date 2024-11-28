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

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class JoinWaitlistActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseFirestore db;

    private String eventName, facilityName, location, dateTime, description;
    private int maxAttendees;
    private boolean geolocationRequirement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_waitlist);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        db = FirebaseFirestore.getInstance();

        // Retrieve event details from the intent
        eventName = getIntent().getStringExtra("eventName");
        facilityName = getIntent().getStringExtra("facilityName");
        location = getIntent().getStringExtra("location");
        dateTime = getIntent().getStringExtra("dateTime");
        description = getIntent().getStringExtra("description");
        maxAttendees = getIntent().getIntExtra("maxAttendees", 0);
        geolocationRequirement = getIntent().getBooleanExtra("geolocationRequirement", false);
        String posterUrl = getIntent().getStringExtra("posterUrl");

        // Set the retrieved data to the UI elements
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

        // Load the poster image
        ImageView posterImageView = findViewById(R.id.qrCodeImage_2);
        if (posterUrl != null && !posterUrl.isEmpty()) {
            Glide.with(this)
                    .load(posterUrl)
                    .into(posterImageView);
        }

        Button joinWaitlistButton = findViewById(R.id.join_waitlist_button);
        joinWaitlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (geolocationRequirement) {
                    checkLocationPermissionAndJoinWaitlist();
                } else {
                    joinWaitlistWithoutLocation();
                }
            }
        });

        ImageButton backButton = findViewById(R.id.back_button_1);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JoinWaitlistActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkLocationPermissionAndJoinWaitlist() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocationAndJoinWaitlist();
        }
    }

    private void getLocationAndJoinWaitlist() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        joinWaitlistWithLocation(location);
                    } else {
                        joinWaitlistWithoutLocation();
                    }
                });
    }

    private void joinWaitlistWithLocation(Location location) {
        Map<String, Object> entrantData = new HashMap<>();
        Log.d("JoinWaitlistActivity", "Location: " + location.getLatitude() + ", " + location.getLongitude());
        Toast.makeText(this, "Location: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();
        entrantData.put("latitude", location.getLatitude());
        entrantData.put("longitude", location.getLongitude());
        entrantData.put("dateTime", dateTime);

        db.collection("waitlist")
                .document(eventName)
                .collection("entrants")
                .add(entrantData)
                .addOnSuccessListener(documentReference -> {
                    Intent intent = new Intent(JoinWaitlistActivity.this, SelectRoleActivity.class);
                    intent.putExtra("Event Name", eventName);
                    intent.putExtra("Facility", facilityName);
                    intent.putExtra("Location", location);
                    intent.putExtra("DateTime", dateTime);
                    intent.putExtra("Description", description);
                    intent.putExtra("Max Attendees", maxAttendees);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error adding to waitlist", Toast.LENGTH_SHORT).show();
                });
    }


    private void joinWaitlistWithoutLocation() {
        Map<String, Object> waitlistEntry = new HashMap<>();
        waitlistEntry.put("eventName", eventName);
        waitlistEntry.put("facilityName", facilityName);
        waitlistEntry.put("location", location);
        waitlistEntry.put("dateTime", dateTime);
        waitlistEntry.put("description", description);
        waitlistEntry.put("maxAttendees", maxAttendees);

        db.collection("waitlist")
                .document(eventName)  // This sets the document name to the event name
                .set(waitlistEntry)
                .addOnSuccessListener(aVoid -> {
                    Intent intent = new Intent(JoinWaitlistActivity.this, SelectRoleActivity.class);
                    intent.putExtra("Event Name", eventName);
                    intent.putExtra("Facility", facilityName);
                    intent.putExtra("Location", location);
                    intent.putExtra("DateTime", dateTime);
                    intent.putExtra("Description", description);
                    intent.putExtra("Max Attendees", maxAttendees);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error adding to waitlist", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndJoinWaitlist();
            } else {
                joinWaitlistWithoutLocation();
            }
        }
    }
}