package com.example.intels_app;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class FacilityDetailsAdmin extends AppCompatActivity {
    private Facility facility;
    TextView name;
    TextView location;
    TextView email;
    TextView telephone;
    ImageView facilityImage;
    Button removeImageButton;
    ImageButton backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_facility_details);

        name = findViewById(R.id.facilityNameEditText);
        location = findViewById(R.id.locationEditText);
        email = findViewById(R.id.emailEditText);
        telephone = findViewById(R.id.telephoneEditText);
        facilityImage = findViewById(R.id.pfpPlaceholder);

        // Get the facility name from the intent
        String facilityName = getIntent().getStringExtra("Facility Name");

        FirebaseFirestore.getInstance().collection("facilities").document(facilityName).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        facility = documentSnapshot.toObject(Facility.class);

                        // Set the facility details in the UI
                        name.setText(facility.getFacilityName());
                        location.setText(facility.getLocation());
                        email.setText(facility.getEmail());
                        telephone.setText(String.valueOf(facility.getTelephone()));

                        if (facility.getFacilityImageUrl() != null && !facility.getFacilityImageUrl().isEmpty()) {
                            Glide.with(getApplicationContext())
                                    .load(facility.getFacilityImageUrl())
                                    .placeholder(R.drawable.pfp_placeholder_image)
                                    .error(R.drawable.pfp_placeholder_image)
                                    .into(facilityImage);
                        } else {
                            facilityImage.setImageResource(R.drawable.pfp_placeholder_image);
                            removeImageButton.setVisibility(View.INVISIBLE);
                        }
                    }
                });

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FacilityDetailsAdmin.this, AdminFacilities.class);
                startActivity(intent);
            }
        });

        removeImageButton = findViewById(R.id.edit_poster_button);
        removeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(FacilityDetailsAdmin.this)
                        .setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete this image?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Remove image from Storage
                            FirebaseStorage.getInstance().getReferenceFromUrl(facility.getFacilityImageUrl()).delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            // Remove image URL from FireStore
                                            FirebaseFirestore.getInstance().collection("facilities").document(facilityName)
                                                    .update("facilityImageUrl", null)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Log.d(TAG, "Image URL removed successfully");
                                                            facilityImage.setImageResource(R.drawable.pfp_placeholder_image);
                                                            removeImageButton.setVisibility(View.INVISIBLE);
                                                        }
                                                    });

                                        }
                                    });
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            // Dismiss the dialog if the user cancels
                            dialog.dismiss();
                        })
                        .show();
            }
        });
    }
}