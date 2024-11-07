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
    ImageButton back_button;
    Button profile_button, events_button;
    ListView profile_list;
    private List<Profile> profileList;
    Profile profile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);

        profile_list = findViewById(R.id.profile_list);
        profileList = new ArrayList<>();

        ProfileAdapterAdmin adapter = new ProfileAdapterAdmin(this, profileList);
        profile_list.setAdapter(adapter);

        // Retrieve profile data from Firestore and assign it to profile arraylist
        FirebaseFirestore.getInstance().collection("profiles").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Log.d("Firestore", "Profiles found: " + queryDocumentSnapshots.size());
                        for (DocumentSnapshot document : queryDocumentSnapshots) {

                            // Convert document to Profile object and add to profileList
                            //Profile profile = document.toObject(Profile.class);
                            //Log.d("Firestore", "Name: " + profile.getName() + "ID: " + profile.getImageResId());
                            //Log.d("Firestore", "Name: " + profile.getName() + "ID: " + profile.getImageResId());

                            Profile setProfile = new Profile(document.getString("name"), document.getString("email"));

                            Log.d("Firestore", "name: " + document.getString("name"));

                            profileList.add(setProfile);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.d("Firestore", "No documents found in this collection.");
                    }
                }).addOnFailureListener(e -> Log.w("Firestore", "Error fetching documents", e));

        Log.d("hi", "hi");

        for (int i = 0; i < profileList.size(); i++) {
            Log.d("List", "Name: " + profileList.get(i).getName() + "ID: " + profileList.get(i).getImageResId());
        }

        profile_button = findViewById(R.id.profile_button);
        events_button = findViewById(R.id.events_button);

        profile_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
        events_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));

        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                events_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
                profile_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));

                Intent intent = new Intent(AdminProfiles.this, AdminProfiles.class);
                startActivity(intent);
            }
        });

        events_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                events_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
                profile_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));

                Intent intent = new Intent(AdminProfiles.this, AdminEvents.class);
                startActivity(intent);
            }
        });

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
