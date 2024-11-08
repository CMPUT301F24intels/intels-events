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

/**
 * Main activity that initializes the app and determines if a user profile exists.
 * This activity retrieves the device ID using Firebase Messaging and checks
 * Firebase Firestore to see if a profile is associated with the device.
 * If a profile exists, the user is directed to the main page; otherwise,
 * they are redirected to the profile creation screen.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     * Attempts to retrieve the Firebase Device ID, and uses it to
     * check if the user's profile exists.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down, this Bundle contains the most recent data.
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
     * Checks whether a user profile exists for the given device ID.
     * If a user profile exists, proceeds to the main functionality.
     * If not, redirects the user to the profile creation screen.
     *
     * @param deviceId The unique device ID retrieved from Firebase.
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
     * Proceeds to the main application functionality if a user profile exists.
     * Starts {@link MainPageActivity} and finishes this activity.
     */
    private void proceedToApp() {
        Intent intent = new Intent(MainActivity.this, MainPageActivity.class);
        startActivity(intent);
        finish(); // Close MainActivity
    }

    /**
     * Redirects the user to the organizer profile creation activity if they are new.
     * Passes the device ID to the profile creation activity.
     *
     * @param deviceId The unique device ID to be used in {@link CreateFacility}.
     */
    // Method to redirect to create profile if user is new
    private void redirectToCreateOrganizerProfile(String deviceId) {
        Intent intent = new Intent(MainActivity.this, CreateFacility.class); //Changed this line make it back to CreateFacility
        intent.putExtra("deviceId", deviceId); // Pass device ID if needed in CreateProfileActivity
        startActivity(intent);
        finish(); // Close MainActivity
    }
  
    /**
     * Redirects the user to the entrant profile creation activity if they are new.
     * Passes the device ID to the profile creation activity.
     * This method is currently commented out for future implementation.
     *
     * @param deviceId
     */
    private void redirectToCreateEntrantProfile(String deviceId) {
        // Redirect to sign up page
        // Intent intent = new Intent(MainActivity.this, CreateEntrantProfile.class);
        // intent.putExtra("deviceId", deviceId);
        // startActivity(intent);
        // finish(); // Close MainActivity
    }
}
