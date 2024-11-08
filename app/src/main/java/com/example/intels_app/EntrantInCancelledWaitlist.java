package com.example.intels_app;

import android.app.AlertDialog;
import android.content.Intent;
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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntrantInCancelledWaitlist extends AppCompatActivity {
    private Button waitlist_button, cancelled_button;
    private ListView listView;
    private List<Profile> profileList;
    private String eventName;
    private CheckBox sendNotificationCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waitlist_with_cancelled_entrants);

        EditText searchBar = findViewById(R.id.search_bar);
        listView = findViewById(R.id.profile_list);

        profileList = new ArrayList<>();
        ProfileAdapter adapter = new ProfileAdapter(this, profileList);
        listView.setAdapter(adapter);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(EntrantInCancelledWaitlist.this, EventGridOrganizerActivity.class);
            startActivity(intent);
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        waitlist_button = findViewById(R.id.btn_waitlist);
        cancelled_button = findViewById(R.id.btn_cancelled);

        cancelled_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
        waitlist_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));

        waitlist_button.setOnClickListener(v -> {
            cancelled_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
            waitlist_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));

            Intent intent = new Intent(EntrantInCancelledWaitlist.this, EntrantInWaitlist.class);
            startActivity(intent);
        });

        cancelled_button.setOnClickListener(v -> {
            cancelled_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
            waitlist_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));

            Intent intent = new Intent(EntrantInCancelledWaitlist.this, EntrantInCancelledWaitlist.class);
            startActivity(intent);
        });

        sendNotificationCheckbox = findViewById(R.id.checkbox_notify);
        sendNotificationCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showCustomNotificationDialog();
            }
        });

        // Fetch cancelled entrants from Firestore
        fetchCancelledEntrants(adapter);
    }


    private void fetchCancelledEntrants(ProfileAdapter adapter) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("waitlisted_entrants")
                .whereEqualTo("status", "cancelled")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        profileList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            String name = (String) document.get("name");
                            String imageUrl = (String) document.get("imageUrl");
                            Profile profile = new Profile(name, imageUrl);
                            profileList.add(profile);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to fetch cancelled entrants.", Toast.LENGTH_SHORT).show();
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

        // Validate eventId
        if (eventName == null || eventName.isEmpty()) {
            Log.e("Firestore", "eventId is null or empty");
            Toast.makeText(this, "Event ID is missing. Cannot send notification.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create notification data to save in Firestore
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("message", message); // Custom message
        notificationData.put("timestamp", FieldValue.serverTimestamp()); // Server timestamp
        notificationData.put("eventName", eventName); // Tag to associate with the event

        // Add the notification to the top-level notifications collection
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