package com.example.intels_app;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FinalList extends AppCompatActivity {
    private ListView entrantList;
    private List<Profile> entrantDataList;
    private ImageButton back_button;
    private CheckBox sendNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_list);

        entrantList = findViewById(R.id.entrant_list);
        entrantDataList = new ArrayList<>();
        ProfileAdapter adapter = new ProfileAdapter(this, entrantDataList);
        entrantList.setAdapter(adapter);

        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(view -> {
            Intent intent = new Intent(FinalList.this, EntrantInWaitlist.class);
            startActivity(intent);
        });

        EditText searchBar = findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        sendNotifications = findViewById(R.id.send_notifications);
        sendNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showCustomNotificationDialog();
            }
        });

        // Fetch accepted entrants from Firestore
        fetchAcceptedEntrants(adapter);
    }

    private void fetchAcceptedEntrants(ProfileAdapter adapter) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("final_list")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        entrantDataList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            String name = (String) document.get("name");
                            String imageUrl = (String) document.get("imageUrl");
                            Profile profile = new Profile(name, imageUrl);
                            entrantDataList.add(profile);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to fetch accepted entrants.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showCustomNotificationDialog() {
        EditText input = new EditText(this);
        input.setHint("Enter custom notification message");

        new AlertDialog.Builder(this)
                .setTitle("Custom Notification")
                .setMessage("Enter the message to send to all selected entrants:")
                .setView(input)
                .setPositiveButton("Send", (dialog, which) -> {
                    String message = input.getText().toString().trim();
                    if (!message.isEmpty()) {
                        sendNotificationToEntrants(message);
                        sendNotifications.setChecked(false);
                    } else {
                        Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    sendNotifications.setChecked(false);
                    dialog.cancel();
                })
                .show();
    }

    private void sendNotificationToEntrants(String message) {
        Toast.makeText(this, "Notification sent: " + message, Toast.LENGTH_LONG).show();
    }
}
