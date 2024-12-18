package com.example.intels_app;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Displays a list of all profiles for the admin view.
 * @author Janan Panchal, Dhanshri Patel
 * @see com.example.intels_app.MainActivity Back button leads to main page
 * @see com.example.intels_app.AdminEvents Clicking the events tab leads to the admin events page
 * @see com.example.intels_app.ProfileAdapterAdmin Custom adapter for the list view
 * @see com.example.intels_app.Profile Profile object
 */

public class AdminProfiles extends AppCompatActivity {
    private ImageButton back_button;
    private Button profile_button;
    private Button events_button;
    private Button facilities_button;
    private List<Profile> profileList;
    private Profile profile;
    private ImageButton imageButton22;
    private Dialog progressDialog;

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


        Button BrowserButton = findViewById(R.id.image_browser_button);
        BrowserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminProfiles.this, ImageBrowserActivity.class);
                startActivity(intent);
            }
        });

        /*
        // Initialize the imageButton22
        imageButton22 = findViewById(R.id.imageButton22);

        // Set click listener for imageButton22
        imageButton22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });*/

        showProgressDialog();
        // Retrieve all profile data from FireStore and assign it to profile arraylist
        FirebaseFirestore.getInstance().collection("profiles").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    dismissProgressDialog();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {

                            // Convert document to Profile object and add to profileList
                            profile = document.toObject(Profile.class);
                            profileList.add(profile);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        dismissProgressDialog();
                        Log.d("Firestore", "No documents found in this collection.");
                    }
                }).addOnFailureListener(e -> Log.w("Firestore", "Error fetching documents", e));

        // Initialize buttons
        profile_button = findViewById(R.id.profile_button);
        events_button = findViewById(R.id.events_button);
        facilities_button = findViewById(R.id.facilities_button);

        // Set button colors
        profile_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
        events_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
        facilities_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));

        // If event button clicked, switch to events page that shows all events to admin
        events_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Change button colour to mimic switching of tabs
                events_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
                profile_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
                facilities_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));

                Intent intent = new Intent(AdminProfiles.this, AdminEvents.class);
                startActivity(intent);
            }
        });

        // If facilities button clicked, switch to facilities page
        facilities_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Change button colour to mimic switching of tabs
                events_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
                profile_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
                facilities_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));

                Intent intent = new Intent(AdminProfiles.this, AdminFacilities.class);
                startActivity(intent);

            }
        });

        // Go back to main page if back button clicked
        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminProfiles.this, MainActivity.class);
                startActivity(intent);
            }
        });

        profile_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Profile selectedProfile = (Profile) parent.getItemAtPosition(position);

                Intent intent = new Intent(AdminProfiles.this, ProfileDetailsAdmin.class);
                intent.putExtra("deviceId", selectedProfile.getDeviceId());
                startActivity(intent);
            }
        });
    }

    /**
     * Displays the dialog with the loading circle while data is being fetched from Firestore
     */
    private void showProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.dialog_progress_bar, null);
        builder.setView(customLayout);
        builder.setCancelable(false);

        // Create and show the dialog
        progressDialog = builder.create();
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Ensure the dialog appears as a square
        progressDialog.setOnShowListener(dialog -> {
            if (progressDialog.getWindow() != null) {
                progressDialog.getWindow().setLayout(400, 400); // Set width and height to match layout
            }
        });

        progressDialog.show();
    }

    /**
     * Stops the dialog with the loading circle
     */
    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * Deletes a random facility from the FireStore "facilities" collection.
     * @deprecated Facilities are no longer deleted from the profiles page. They should not be deleted randomly.
     */
    private void deleteFacility() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference facilitiesRef = db.collection("facilities");

        // Fetch all facilities from Firestore
        facilitiesRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Get all documents in a list
                        List<DocumentSnapshot> facilities = queryDocumentSnapshots.getDocuments();

                        // Select a document
                        int randomIndex = new Random().nextInt(facilities.size());
                        DocumentSnapshot randomFacility = facilities.get(randomIndex);

                        // Get the facility name to display after deletion
                        String facilityName = randomFacility.getString("facilityName");

                        // Delete the selected facility
                        randomFacility.getReference().delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(AdminProfiles.this, "Removed facility: " + facilityName, Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(AdminProfiles.this, "Error removing facility: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(AdminProfiles.this, "No facilities to remove", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminProfiles.this, "Error fetching facilities: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * @deprecated This function was used to display options when clicking three dots. The 3 dots no longer exist.
     * @param v View to show the popup menu
     */
    private void showPopupMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.inflate(R.menu.popup_menu);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.remove_facility) {
                    deleteFacility();
                    return true;
                }
                return false;
            }
        });

        popup.show();
    }
}
