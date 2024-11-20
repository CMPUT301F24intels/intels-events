package com.example.intels_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
/**
 * SelectRoleActivity allows users to choose between joining an event as an entrant or a guest.
 * It handles navigation to the appropriate activity based on the user's selection and updates
 * the event waitlist in Firestore when joining as a guest.
 *
 * This activity also retrieves and logs the device ID for tracking purposes.
 * Author: Dhanshri Patel
 */
public class SelectRoleActivity extends AppCompatActivity {
    ImageButton backButton;
    Button join_as_entrant_button;
    Button join_as_guest_button;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String deviceId;
    private String eventName;
    private CollectionReference waitlistRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_role);

        db = FirebaseFirestore.getInstance();
        waitlistRef = db.collection("waitlisted_entrants");

        FirebaseInstallations.getInstance().getId()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        deviceId = task.getResult();
                        Log.d("Device ID", "Device ID: " + deviceId);
                    } else {
                        Log.e("Device ID Error", "Unable to get Device ID", task.getException());
                        Toast.makeText(this, "Error generating Device ID", Toast.LENGTH_SHORT).show();
                    }
                });

        eventName = getIntent().getStringExtra("Event Name");

        // Set up back button
        backButton = findViewById(R.id.back_button_2);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectRoleActivity.this, JoinWaitlistActivity.class);
                startActivity(intent);
            }
        });

        join_as_entrant_button = findViewById(R.id.join_as_entrant);
        join_as_entrant_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectRoleActivity.this, SignUp.class);
                intent.putExtra("Device ID", deviceId);
                intent.putExtra("Event Name", eventName); // Event details
                startActivity(intent);
            }
        });

        join_as_guest_button = findViewById(R.id.guest_button);
        join_as_guest_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addGuestToWaitlist();
            }
        });

    }

    private void addGuestToWaitlist() {
        // Retrieve the event ID or name from the intent, passed from the previous activity
        eventName = getIntent().getStringExtra("Event Name");

        if (deviceId == null) {
            Toast.makeText(this, "Device ID not available. Try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> waitlistEntry = new HashMap<>();
        waitlistEntry.put("deviceId", deviceId);
        waitlistEntry.put("eventName", eventName);

        // Profile guestProfile = new Profile(deviceId);

        waitlistRef.document("Guest" + "_" + eventName)
                .set(waitlistEntry)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SelectRoleActivity.this, "Successfully joined event as Guest!", Toast.LENGTH_SHORT).show();
                    Log.d("Firestore", "Guest successfully added to waitlisted_events!");

                    // Navigate after the operation succeeds
                    Intent intent = new Intent(SelectRoleActivity.this, SuccessWaitlistJoin.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SelectRoleActivity.this, "Failed to join event as Guest.", Toast.LENGTH_SHORT).show();
                    Log.w("FirestoreError", "Error adding guest to waitlisted_events", e);
                });
    }
}
