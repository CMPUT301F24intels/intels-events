package com.example.intels_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.bumptech.glide.Glide;

public class JoinWaitlistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_waitlist);

        /*// Retrieve event details from the intent
        String eventName = getIntent().getStringExtra("eventName");
        String facilityName = getIntent().getStringExtra("facilityName");
        String location = getIntent().getStringExtra("location");
        String dateTime = getIntent().getStringExtra("dateTime");
        String description = getIntent().getStringExtra("description");
        int maxAttendees = getIntent().getIntExtra("maxAttendees", 0);
        boolean geolocationRequirement = getIntent().getBooleanExtra("geolocationRequirement", false);
        String posterUrl = getIntent().getStringExtra("posterUrl");*/

        // Set the retrieved data to the UI elements
        TextView eventNameTextView = findViewById(R.id.eventNameEdit);
        TextView facilityTextView = findViewById(R.id.facilityEdit);
        TextView locationTextView = findViewById(R.id.locationEdit);
        TextView dateTimeTextView = findViewById(R.id.dateTimeEdit);
        TextView descriptionTextView = findViewById(R.id.descriptionEdit);
        TextView maxAttendeesTextView = findViewById(R.id.max_attendees);
        SwitchCompat geolocationSwitch = findViewById(R.id.geolocationRequirementText);

        // Hardcoding Data simply to test, change back after
        String eventName = "2:00AM";
        String facilityName = "Main Hall";
        String location = "123 Event St, City";
        String dateTime = "2024-11-10 10:00 AM";
        String description = "This is a sample event description.";
        int maxAttendees = 100;
        boolean geolocationRequirement = false;
        String posterUrl = "wwwwwwww";

        eventNameTextView.setText(String.format("Name: %s", eventName));
        facilityTextView.setText(String.format("Facility: %s", facilityName));
        locationTextView.setText(String.format("Location: %s", location));
        dateTimeTextView.setText(String.format("Date and Time: %s", dateTime));
        descriptionTextView.setText(String.format("Description: %s", description));
        maxAttendeesTextView.setText(String.format("Max Attendees: %d", maxAttendees));
        geolocationSwitch.setText(String.format("Geolocation Requirement: %s", geolocationRequirement ? "Yes" : "No"));
        geolocationSwitch.setChecked(geolocationRequirement);
        geolocationSwitch.setClickable(false);  // Make the switch non-interactive

        // Load the poster image
        ImageView posterImageView = findViewById(R.id.qrCodeImage_2);
        if (posterUrl != null && !posterUrl.isEmpty()) {
            Glide.with(this)
                    .load(posterUrl)
                    .into(posterImageView);
        }


        Button joinWaitlistButton = findViewById(R.id.join_waitlist_button);
        joinWaitlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JoinWaitlistActivity.this, SelectRoleActivity.class);
                startActivity(intent);
            }
        });

        ImageButton backButton = findViewById(R.id.back_button_1);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JoinWaitlistActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        if (eventNameTextView != null) {
            eventNameTextView.setText(eventName);
        }
        if (facilityTextView != null) {
            facilityTextView.setText(facilityName);
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

        joinWaitlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JoinWaitlistActivity.this, SelectRoleActivity.class);
                intent.putExtra("Event Name", eventName);
                intent.putExtra("Facility", facilityName);
                intent.putExtra("Location", location);
                intent.putExtra("DateTime", dateTime);
                intent.putExtra("Description", description);
                intent.putExtra("Max Attendees", maxAttendees);
                startActivity(intent);
            }
        });
    }
}