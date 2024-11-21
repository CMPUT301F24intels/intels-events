package com.example.intels_app;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

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
    private Profile profile;
    private ImageView profile_pic;
    private TextView name, email, phone_number;
    private ImageButton back_button;
    private Button delete_pfp_button;
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

        FirebaseFirestore.getInstance().collection("profiles").document(profileId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        profile = documentSnapshot.toObject(Profile.class);
                    }
                });

        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileDetailsAdmin.this, AdminProfiles.class);
                startActivity(intent);
            }
        });

        delete_pfp_button = findViewById(R.id.remove_pfp_button);
        delete_pfp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Remove image from storage
                FirebaseStorage.getInstance().getReferenceFromUrl(profile.getImageUrl()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                // Remove pfp URL from profile details
                                FirebaseFirestore.getInstance().collection("profiles").document(profileId)
                                        .update("imageUrl", null)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d(TAG, "Profile picture URL removed successfully");
                                                profile_pic.setImageResource(R.drawable.person_image);
                                            }
                                        });

                            }
                        });
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
