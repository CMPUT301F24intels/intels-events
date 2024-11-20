/**
 * This class displays a list of selected entrants from Firestore for a
 * specific event using a RecyclerView based on the event ID, and displays
 * each entrant's profile information.
 * @author Aayushi Shah, Katrina Alejo
 * @see com.example.intels_app.SelectedEntrantAdapter Adapter for entrants selected in lottery
 * @see com.google.firebase.firestore.FirebaseFirestore Firebase
 * @see com.example.intels_app.ManageEventsActivity Manage events home page
 */

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

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SelectedEntrantActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SelectedEntrantAdapter adapter;
    private List<Profile> selectedEntrants;
    private FirebaseFirestore db;
    private String eventName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_entrants);

        // Get the event ID from the intent
        eventName = getIntent().getStringExtra("eventName");

        if (eventName == null) {
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
                .whereEqualTo("eventName", eventName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    selectedEntrants.clear();
                    List<Task<DocumentSnapshot>> tasks = new ArrayList<>(); // Track DocumentSnapshot tasks

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String profileId = doc.getString("profileId");

                        // Retrieve profile data based on profileId
                        Task<DocumentSnapshot> task = db.collection("profiles").document(profileId).get()
                                .addOnSuccessListener(profileDoc -> {
                                    if (profileDoc.exists()) {
                                        Profile profile = profileDoc.toObject(Profile.class);
                                        if (profile != null) {
                                            selectedEntrants.add(profile);
                                        }
                                    } else {
                                        Log.w("SelectedEntrants", "Profile not found for ID: " + profileId);
                                    }
                                })
                                .addOnFailureListener(e -> Log.w("SelectedEntrants", "Error loading profile for ID: " + profileId, e));

                        tasks.add(task);
                    }

                    // Wait for all tasks to complete, then update the adapter
                    Tasks.whenAllComplete(tasks).addOnCompleteListener(task -> {
                        adapter.notifyDataSetChanged();
                        if (!task.isSuccessful()) {
                            Log.e("SelectedEntrants", "One or more profile load operations failed.");
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.w("SelectedEntrants", "Error loading selected entrants", e);
                    Toast.makeText(this, "Failed to load selected entrants.", Toast.LENGTH_SHORT).show();
                });
    }
}

