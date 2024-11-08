/**
 * Displays a list of all profiles for the admin view.
 * @author Janan Panchal
 * @see com.example.intels_app.MainPageActivity Back button leads to main page
 * @see com.example.intels_app.AdminEvents Clicking the events tab leads to the admin events page
 * @see com.example.intels_app.ProfileAdapterAdmin Custom adapter for the list view
 * @see com.example.intels_app.Profile Profile object
 */
package com.example.intels_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminProfiles extends AppCompatActivity {
    private ImageButton back_button;
    private Button profile_button;
    private Button events_button;
    private List<Profile> profileList;
    private Profile profile;

    /**
     * Displays a list of all profiles for the admin view.
     * Button Functionality:
     *      Back button - return to the main page
     *      Profile button - switch to list of all profiles page
     *
     * @param savedInstanceState Bundle to save the state of the activity
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);

        // Initialize the list to store all profiles
        ListView profile_list = findViewById(R.id.profile_list);
        profileList = new ArrayList<>();

        // Initialize the adapter to display the profiles
        ProfileAdapterAdmin adapter = new ProfileAdapterAdmin(this, profileList);
        profile_list.setAdapter(adapter);

        // Retrieve all profile data from FireStore and assign it to profile arraylist
        FirebaseFirestore.getInstance().collection("profiles").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {

                            // Convert document to Profile object and add to profileList
                            profile = document.toObject(Profile.class);
                            profileList.add(profile);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.d("Firestore", "No documents found in this collection.");
                    }
                }).addOnFailureListener(e -> Log.w("Firestore", "Error fetching documents", e));

        // Initialize buttons
        profile_button = findViewById(R.id.profile_button);
        events_button = findViewById(R.id.events_button);

        // Set button colors
        profile_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
        events_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));

        // If event button clicked, switch to events page that shows all events to admin
        events_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Change button colour to mimic switching of tabs
                events_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
                profile_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));

                Intent intent = new Intent(AdminProfiles.this, AdminEvents.class);
                startActivity(intent);
            }
        });

        // Go back to main page if back button clicked
        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminProfiles.this, MainPageActivity.class);
                startActivity(intent);
            }
        });
    }
}
