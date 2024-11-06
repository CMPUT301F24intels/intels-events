package com.example.intels_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Step 1: Get Firebase Device ID
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String deviceId = task.getResult();
                        checkUserExists(deviceId);
                    } else {
                        Log.e("DeviceID", "Failed to get Firebase Instance ID", task.getException());
                        Toast.makeText(this, "Error retrieving device ID. Please try again.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Step 2: Check if user profile exists based on device ID
    private void checkUserExists(String deviceId) {
        FirebaseFirestore.getInstance().collection("facilities")
                .whereEqualTo("deviceId", deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // User exists, proceed to the main functionality
                        proceedToApp();
                    } else {
                        // User does not exist, redirect to profile creation
                        // redirectToCreateEntrantProfile(deviceId); // Uncomment line to implement
                        redirectToCreateOrganizerProfile(deviceId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error checking user existence", e);
                    Toast.makeText(this, "Error accessing user information. Please try again.", Toast.LENGTH_LONG).show();
                });
    }

    // Method to start the main app if user exists
    private void proceedToApp() {
        Intent intent = new Intent(MainActivity.this, MainPageActivity.class);
        startActivity(intent);
        finish(); // Close MainActivity
    }

    // Method to redirect to create profile if user is new
    private void redirectToCreateOrganizerProfile(String deviceId) {
        Intent intent = new Intent(MainActivity.this, CreateFacility.class);
        intent.putExtra("deviceId", deviceId); // Pass device ID if needed in CreateProfileActivity
        startActivity(intent);
        finish(); // Close MainActivity
    }

    private void redirectToCreateEntrantProfile(String deviceId) {
        // Redirect to sign up page
        // Intent intent = new Intent(MainActivity.this, CreateEntrantProfile.class);
        // intent.putExtra("deviceId", deviceId);
        // startActivity(intent);
        // finish(); // Close MainActivity
    }
}
