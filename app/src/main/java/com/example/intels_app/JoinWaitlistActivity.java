package com.example.intels_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class JoinWaitlistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_waitlist);

        Button joinWaitlistButton = findViewById(R.id.join_waitlist_button);

        joinWaitlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JoinWaitlistActivity.this, SelectRoleActivity.class);
                startActivity(intent);
            }
        });

        // Set up back button
        ImageButton backButton = findViewById(R.id.back_button_1);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Closes the current activity and returns to the previous one
            }
        });


        // Retrieve event details from the intent
        String eventName = getIntent().getStringExtra("Event Name");
        String facility = getIntent().getStringExtra("Facility");
        String location = getIntent().getStringExtra("Location");
        String dateTime = getIntent().getStringExtra("DateTime");
        String description = getIntent().getStringExtra("Description");
        int maxAttendees = getIntent().getIntExtra("Max Attendees", 0);

        // Find and set text for each TextView
        TextView eventNameTextView = findViewById(R.id.eventNameEdit);
        TextView facilityTextView = findViewById(R.id.facilityEdit);
        TextView locationTextView = findViewById(R.id.locationEdit);
        TextView dateTimeTextView = findViewById(R.id.dateTimeEdit);
        TextView descriptionTextView = findViewById(R.id.descriptionEdit);
        TextView maxAttendeesTextView = findViewById(R.id.max_attendees);

        if (eventNameTextView != null) {
            eventNameTextView.setText(eventName);
        }
        if (facilityTextView != null) {
            facilityTextView.setText(facility);
        }
        if (locationTextView != null) {
            locationTextView.setText(location);
        }
        if (dateTimeTextView != null) {
            dateTimeTextView.setText(dateTime);
        }
        if (descriptionTextView != null) {
            descriptionTextView.setText(description);
        }
        if (maxAttendeesTextView != null) {
            maxAttendeesTextView.setText(String.valueOf(maxAttendees));
        }



    }
}