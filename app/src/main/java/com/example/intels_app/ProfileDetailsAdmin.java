package com.example.intels_app;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Displays detailed information about a specific user profile in the admin view.
 * Retrieves profile data from Firestore using a profile ID and populates the UI
 * with the user's name, email, phone number, and profile picture. Includes a back
 * button to navigate to the admin profiles list.
 *
 * @author Dhanshri Patel
 * @see com.example.intels_app.AdminProfiles
 */

public class ProfileDetailsAdmin extends AppCompatActivity {

    private ImageView profile_pic;
    private EditText name, email, phone_number;
    private ImageButton back_button;
    private FirebaseFirestore db;
    private String profileId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_details_admin);

        profileId = getIntent().getStringExtra("profileId");

        if (profileId == null) {
            Log.e(TAG, "Profile ID is missing");
            finish();
            return;
        }

        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileDetailsAdmin.this, AdminProfiles.class);
                startActivity(intent);
            }
        });

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone_number = findViewById(R.id.phone_number);
        profile_pic = findViewById(R.id.profile_pic);

        db = FirebaseFirestore.getInstance();
        loadProfileDetails();

    }
    private void loadProfileDetails(){
        DocumentReference documentRef = db.collection("profiles").document(profileId);
        documentRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Profile profile = documentSnapshot.toObject(Profile.class);
                if (profile != null) {
                    // Populate the UI with event details
                    name.setText("Name: " + profile.getName());
                    email.setText("Email: " + profile.getEmail());
                    phone_number.setText("Phone Number: " + profile.getPhone_number());

                    // Load event poster image using Glide
                    if (profile.getImageUrl() != null && !profile.getImageUrl().isEmpty()) {
                        Glide.with(getApplicationContext())
                                .load(profile.getImageUrl())
                                .placeholder(R.drawable.pfp_placeholder_image)
                                .error(R.drawable.person_image)
                                .into(profile_pic);
                    } else {
                        Log.w(TAG, "No poster URL found in the document");
                        profile_pic.setImageResource(R.drawable.person_image);
                    }
                }
            } else {
                Log.e(TAG, "No such document exists");
            }
        }).addOnFailureListener(e -> Log.w(TAG, "Error getting document", e));
    }
}
