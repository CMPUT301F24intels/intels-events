package com.example.intels_app;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class EntrantInWaitlist extends AppCompatActivity {
    private Button waitlist_button, cancelled_button, final_list_button, back_button;
    private ListView listView;
    private List<Profile> profileList;
    private CheckBox sendNotificationCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waitlist_with_entrants);

        listView = findViewById(R.id.profile_list);

        profileList = new ArrayList<>();
        profileList.add(new Profile("Gopi Modi", R.drawable.gopimodi));
        profileList.add(new Profile("Mr.Bean", R.drawable.bean));

        ProfileAdapter adapter = new ProfileAdapter(this, profileList);
        listView.setAdapter(adapter);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // This will close the current activity and return to the previous one
            }
        });

        waitlist_button = findViewById(R.id.btn_waitlist);
        cancelled_button = findViewById(R.id.btn_cancelled);
        final_list_button = findViewById(R.id.final_list_button);

        cancelled_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
        waitlist_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));

        waitlist_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelled_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
                waitlist_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));

                Intent intent = new Intent(EntrantInWaitlist.this, EntrantInWaitlist.class);
                startActivity(intent);
            }
        });

        cancelled_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelled_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
                waitlist_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));

                Intent intent = new Intent(EntrantInWaitlist.this, EntrantInCancelledWaitlist.class);
                startActivity(intent);
            }
        });
        final_list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EntrantInWaitlist.this, FinalList.class);
                startActivity(intent);
            }
        });
        sendNotificationCheckbox = findViewById(R.id.checkbox_notify);

        // Set up the listener for the checkbox
        sendNotificationCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
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
                .setMessage("Enter the message to send to all entrants:")
                .setView(input)
                .setPositiveButton("Send", (dialog, which) -> {
                    String message = input.getText().toString().trim();
                    if (!message.isEmpty()) {
                        sendNotificationToEntrants(message);
                        sendNotificationCheckbox.setChecked(false);
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
        // Logic to send the notification to all entrants goes here
        // For demonstration, we're using a Toast message as a placeholder
        Toast.makeText(this, "Notification sent: " + message, Toast.LENGTH_LONG).show();

        // Add your actual notification sending code here
        // For example, integrating with Firebase Cloud Messaging if applicable
    }
}
