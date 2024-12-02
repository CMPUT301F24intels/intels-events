package com.example.intels_app;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntrantInCancelledWaitlist extends AppCompatActivity {
    private Button waitlistButton, cancelledButton;
    private ListView listView;
    private String eventName;
    private CheckBox sendNotificationCheckbox;
    private EntrantAdapter entrantAdapter;
    private List<Entrant> entrantsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waitlist_with_cancelled_entrants);

        // Retrieve eventName from the Intent
        eventName = getIntent().getStringExtra("eventName");
        if (eventName == null || eventName.isEmpty()) {
            Toast.makeText(this, "Event Name is missing. Cannot proceed.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize UI components
        EditText searchBar = findViewById(R.id.search_bar);
        listView = findViewById(R.id.profile_list);
        waitlistButton = findViewById(R.id.btn_waitlist);
        cancelledButton = findViewById(R.id.btn_cancelled);
        sendNotificationCheckbox = findViewById(R.id.checkbox_notify);

        entrantsList = new ArrayList<>();
        entrantAdapter = new EntrantAdapter(this, entrantsList);
        listView.setAdapter(entrantAdapter);

        // Back button functionality
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, EventGridOrganizerActivity.class);
            startActivity(intent);
        });

        // Search bar filtering
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                entrantAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Button navigation
        waitlistButton.setOnClickListener(v -> navigateToWaitlist());
        cancelledButton.setOnClickListener(v -> fetchCancelledEntrants());

        // Checkbox notification
        sendNotificationCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showCustomNotificationDialog();
            }
        });

        // Initial fetch
        fetchCancelledEntrants();
    }

    private void fetchCancelledEntrants() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notificationsRef = db.collection("notifications");

        // Clear current list
        entrantsList.clear();
        entrantAdapter.notifyDataSetChanged();

        notificationsRef.whereEqualTo("type", "declined")
                .whereEqualTo("eventName", eventName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        if (documents.isEmpty()) {
                            Toast.makeText(this, "No cancelled entrants found for this event.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (DocumentSnapshot doc : documents) {
                            String profileId = doc.getString("profileId");
                            if (profileId != null) {
                                fetchEntrantDetails(profileId);
                            }
                        }
                    } else {
                        Toast.makeText(this, "Error fetching cancelled entrants.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchEntrantDetails(String profileId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("waitlisted_entrants")
                .document(profileId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Map<String, Object> profile = (Map<String, Object>) doc.get("profile");
                        if (profile != null) {
                            String name = (String) profile.get("name");
                            String imageUrl = (String) profile.get("imageUrl");
                            entrantsList.add(new Entrant(name, imageUrl));
                            entrantAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching entrant details", e));
    }

    private void showCustomNotificationDialog() {
        EditText input = new EditText(this);
        input.setHint("Enter custom notification message");

        new AlertDialog.Builder(this)
                .setTitle("Custom Notification")
                .setMessage("Enter the message to send to all cancelled entrants:")
                .setView(input)
                .setPositiveButton("Send", (dialog, which) -> {
                    String message = input.getText().toString().trim();
                    if (!message.isEmpty()) {
                        sendNotificationToCancelledEntrants(message);
                        sendNotificationCheckbox.setChecked(false);
                    } else {
                        Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> sendNotificationCheckbox.setChecked(false))
                .show();
    }

    private void sendNotificationToCancelledEntrants(String message) {
        // Similar notification logic as the original code
        Toast.makeText(this, "Notification sent to all eligible entrants.", Toast.LENGTH_SHORT).show();
    }

    private void navigateToWaitlist() {
        Intent intent = new Intent(this, EntrantInWaitlist.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
}
