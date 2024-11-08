package com.example.intels_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SelectedEntrantActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SelectedEntrantAdapter adapter;
    private List<Profile> selectedEntrants;
    private FirebaseFirestore db;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_entrants);

        // Get the event ID from the intent
        eventId = getIntent().getStringExtra("eventId");

        if (eventId == null) {
            Toast.makeText(this, "Event ID is missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        selectedEntrants = new ArrayList<>();
        adapter = new SelectedEntrantAdapter(this, selectedEntrants);
        recyclerView.setAdapter(adapter);

        // Load selected entrants from Firestore
        loadSelectedEntrants();

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectedEntrantActivity.this, ManageEventsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadSelectedEntrants() {
        db.collection("selected_entrants")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    selectedEntrants.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String profileId = doc.getString("profileId");

                        // Retrieve profile data based on profileId
                        db.collection("profiles").document(profileId).get().addOnSuccessListener(profileDoc -> {
                            if (profileDoc.exists()) {
                                Profile profile = profileDoc.toObject(Profile.class);
                                if (profile != null) {
                                    selectedEntrants.add(profile);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("SelectedEntrants", "Error loading selected entrants", e);
                    Toast.makeText(this, "Failed to load selected entrants.", Toast.LENGTH_SHORT).show();
                });
    }
}
