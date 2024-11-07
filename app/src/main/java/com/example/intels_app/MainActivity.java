/**
 * When the user uses the app for the first time (new device ID), prompt them to create a facility profile
 * If they are opening the as an existing user (registered device ID), open the app's main page
 */
package com.example.intels_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.intels_app.CreateFacility;
import com.example.intels_app.MainPageActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    /**
     * When the user uses the app for the first time (new device ID), prompt them to create a facility profile
     * @param savedInstanceState Bundle to save the state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get Firebase Device ID
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

    /**
     * Check if the user exists in FireStore based on their device ID
     * @param deviceId Device ID of the user
     */
    private void checkUserExists(String deviceId) {
        FirebaseFirestore.getInstance().collection("facilities")
                .whereEqualTo("deviceId", deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // User exists, proceed to the main page
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

    /**
     * Proceed to the main page if the user exists
     */
    private void proceedToApp() {
        Intent intent = new Intent(MainActivity.this, MainPageActivity.class);
        startActivity(intent);
        finish(); // Close MainActivity
    }

    /**
     * Redirect to the profile creation page if the user does not exist
     * @param deviceId Device ID of the user
     */
    private void redirectToCreateOrganizerProfile(String deviceId) {
        Intent intent = new Intent(MainActivity.this, CreateFacility.class); //Changed this line make it back to CreateFacility
        intent.putExtra("deviceId", deviceId); // Pass device ID if needed in CreateProfileActivity
        startActivity(intent);
        finish(); // Close MainActivity
    }

    // Implement in future
    private void redirectToCreateEntrantProfile(String deviceId) {
        // Redirect to sign up page
        // Intent intent = new Intent(MainActivity.this, CreateEntrantProfile.class);
        // intent.putExtra("deviceId", deviceId);
        // startActivity(intent);
        // finish(); // Close MainActivity
    }
}
