package com.example.intels_app;

import android.annotation.SuppressLint;
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
        entrantDataList.add(new Profile("Dhanshri", R.drawable.cat));
        entrantDataList.add(new Profile("Aayushii", R.drawable.bean));

        ProfileAdapter adapter = new ProfileAdapter(this, entrantDataList);
        entrantList.setAdapter(adapter);

        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (FinalList.this, EntrantInWaitlist.class);
                startActivity(intent);
            }
        });

        EditText searchBar = findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s); // Filter the adapter based on search input
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
        // Logic to send the notification to all entrants goes here
        // For demonstration, we're using a Toast message as a placeholder
        Toast.makeText(this, "Notification sent: " + message, Toast.LENGTH_LONG).show();

        // Add your actual notification sending code here
        // For example, integrating with Firebase Cloud Messaging if applicable
    }
}
