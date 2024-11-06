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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class SelectRoleActivity extends AppCompatActivity {
    ImageButton backButton;
    Button join_as_entrant_button;
    Button join_as_guest_button;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_role);

        // Retrieve device ID for unique identification
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        deviceId = task.getResult();
                    } else {
                        Toast.makeText(this, "Failed to retrieve Device ID", Toast.LENGTH_SHORT).show();
                    }
                });

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
                intent.putExtra("Event Name", getIntent().getStringExtra("Event Name")); // Event details
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
        String eventId = getIntent().getStringExtra("Event Name");

        if (deviceId == null) {
            Toast.makeText(this, "Device ID not available. Try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        Profile guestProfile = new Profile(deviceId);

        db.collection("events")
                .document(eventId) // Use the event ID or name as the document ID
                .collection("waitlist")
                .document(deviceId) // Using device ID as document ID for easy reference
                .set(guestProfile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SelectRoleActivity.this, "Joined as Guest!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SelectRoleActivity.this, "Failed to join as Guest.", Toast.LENGTH_SHORT).show();
                    Log.e("FirestoreError", "Error adding guest to waitlist", e);
                });
    }
}
