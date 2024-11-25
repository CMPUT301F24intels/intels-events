/**
 * This class allows users to view detailed information about an event
 * and provides an option to join the waitlist. This activity displays
 * event details such as name, location, facility, date and time, description,
 * maximum attendees, and geolocation requirements.
 *
 * @author Het Patel
 * @see com.example.intels_app.SelectRoleActivity Select role as entrant/guest
 */

package com.example.intels_app;

import android.app.AlertDialog;
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

//        //This hardcoded data is only to TEST, REMOVE AFTER TO REAL CODE
        String eventName = "Dancing Party";
        String facilityName = "Tech Auditorium";
        String location = "Whyte Ave, Edmonton";
        String dateTime = "2024-12-01 10:00 AM";
        String description = "A conference bringing together the brightest minds in tech.";
        int maxAttendees = 5;
        boolean geolocationRequirement = true;
        String posterUrl = "https://testingexample.com/poster.jpg";

        // Retrieve event details from the intent REAL CODE
        /*String eventName = getIntent().getStringExtra("eventName");
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
                if (geolocationRequirement) {
                    new AlertDialog.Builder(JoinWaitlistActivity.this)
                            .setTitle("Confirm Join")
                            .setMessage("This event tracks your geolocation. Are you sure you want to join this event?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                Intent intent = new Intent(JoinWaitlistActivity.this, SelectRoleActivity.class);
                                intent.putExtra("Event Name", eventName);
                                intent.putExtra("Facility", facilityName);
                                intent.putExtra("Location", location);
                                intent.putExtra("DateTime", dateTime);
                                intent.putExtra("Description", description);
                                intent.putExtra("Max Attendees", maxAttendees);
                                startActivity(intent);
                            })
                            .setNegativeButton("No", (dialog, which) -> {
                                // Dismiss the dialog if the user cancels
                                dialog.dismiss();
                            })
                            .show();
                } else {
                    Intent intent = new Intent(JoinWaitlistActivity.this, SelectRoleActivity.class);
                    intent.putExtra("Event Name", eventName);
                    intent.putExtra("Facility", facilityName);
                    intent.putExtra("Location", location);
                    intent.putExtra("DateTime", dateTime);
                    intent.putExtra("Description", description);
                    intent.putExtra("Max Attendees", maxAttendees);
                    startActivity(intent);
                }
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
    }
}