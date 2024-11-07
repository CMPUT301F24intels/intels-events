package com.example.intels_app;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntrantInWaitlist extends AppCompatActivity {
    private Button waitlist_button, cancelled_button, final_list_button;
    private ListView listView;
    private List<Profile> profileList;
    private CheckBox sendNotificationCheckbox;
    private String eventId;
    private ProfileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waitlist_with_entrants);

        SharedPreferences sharedPreferences = getSharedPreferences("EventPrefs", MODE_PRIVATE);
        eventId = getIntent().getStringExtra("eventId");

        // Store eventId in SharedPreferences if it's passed in Intent
        if (eventId != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("eventId", eventId);
            editor.apply();
        } else {
            // Retrieve eventId from SharedPreferences if not in Intent
            eventId = sharedPreferences.getString("eventId", null);
            if (eventId == null) {
                Toast.makeText(this, "Event ID is missing. Cannot proceed.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }

        Log.d("EntrantInWaitlist", "Retrieved eventId: " + eventId);

        EditText searchBar = findViewById(R.id.search_bar);
        listView = findViewById(R.id.profile_list);
        sendNotificationCheckbox = findViewById(R.id.checkbox_notify);

        profileList = new ArrayList<>();
        profileList.add(new Profile("Aayushi", "dshjfd@gmail.com", 7050404));
        profileList.add(new Profile("Janan", "dshjfd@gmail.com", 7050404));
        adapter = new ProfileAdapter(this, profileList);
        listView.setAdapter(adapter);

        fetchEntrants();

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(EntrantInWaitlist.this, EventGridOrganizerActivity.class);
            startActivity(intent);
            finish();
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s); // Filter the adapter based on search input
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        waitlist_button = findViewById(R.id.btn_waitlist);
        cancelled_button = findViewById(R.id.btn_cancelled);
        final_list_button = findViewById(R.id.final_list_button);

        cancelled_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
        waitlist_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));

        waitlist_button.setOnClickListener(v -> {
            cancelled_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
            waitlist_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));

            Intent intent = new Intent(EntrantInWaitlist.this, EntrantInWaitlist.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
            finish();
        });

        cancelled_button.setOnClickListener(v -> {
            cancelled_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
            waitlist_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));

            Intent intent = new Intent(EntrantInWaitlist.this, EntrantInCancelledWaitlist.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
            finish();
        });

        final_list_button.setOnClickListener(view -> {
            Intent intent = new Intent(EntrantInWaitlist.this, FinalList.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
            finish();
        });

        sendNotificationCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showCustomNotificationDialog();
            }
        });
    }

    private void fetchEntrants() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference waitlistRef = db.collection("waitlisted_entrants");

        List<String> documentNames = new ArrayList<>();
        EntrantAdapter adapter = new EntrantAdapter(this, documentNames);
        listView.setAdapter(adapter);

        waitlistRef.whereEqualTo("eventId", eventId)  // Filter by eventId
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.w("Firestore", "Listen failed.", e);
                        Toast.makeText(this, "Error listening to changes.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    documentNames.clear(); // Helps remove duplicates

                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        Log.d("EntrantInWaitlist", "Received updated documents from waitlisted_entrants.");

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String documentId = documentSnapshot.getId();  // Or get a field like "name"
                            Log.d("EntrantInWaitlist", "Document ID: " + documentId);
                            documentNames.add(documentId);  // Store document name (ID) in list
                        }

                        adapter.notifyDataSetChanged();

                    } else {
                        Log.w("EntrantInWaitlist", "No document names found for this event.");
                        Toast.makeText(this, "No entrants found for this event.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showCustomNotificationDialog() {
        EditText input = new EditText(this);
        input.setHint("Enter custom notification message");

        new AlertDialog.Builder(this)
                .setTitle("Custom Notification")
                .setMessage("Enter the message to send to all waitlisted entrants:")
                .setView(input)
                .setPositiveButton("Send", (dialog, which) -> {
                    String message = input.getText().toString().trim();
                    if (!message.isEmpty()) {
                        sendNotificationToEntrants(message);
                        sendNotificationCheckbox.setChecked(false); // Uncheck the checkbox after sending
                    } else {
                        Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    sendNotificationCheckbox.setChecked(false);
                    dialog.cancel();
                })
                .show();
    }

    private void sendNotificationToEntrants(String message) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (eventId == null || eventId.isEmpty()) {
            Log.e("Firestore", "eventId is null or empty");
            Toast.makeText(this, "Event ID is missing. Cannot send notification.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("message", message);
        notificationData.put("timestamp", FieldValue.serverTimestamp());
        notificationData.put("eventId", eventId);

        db.collection("notifications")
                .add(notificationData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Notification saved successfully!", Toast.LENGTH_LONG).show();
                    Log.d("Firestore", "Notification saved with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error saving notification", e);
                    Toast.makeText(this, "Failed to save notification", Toast.LENGTH_SHORT).show();
                });
    }
}
