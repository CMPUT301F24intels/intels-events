package com.example.intels_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class AddEvent extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);

        EditText maxAttendees = findViewById(R.id.max_attendees_number);
        EditText eventName = findViewById(R.id.eventNameEditText);
        EditText facility = findViewById(R.id.facilityEditText);
        EditText location = findViewById(R.id.locationEditText);
        EditText dateTime = findViewById(R.id.dateTimeEditText);
        EditText description = findViewById(R.id.descriptionEditText);
        SwitchCompat geolocationRequirement = findViewById(R.id.geolocationRequirementTextView);
        SwitchCompat notifPreference = findViewById(R.id.notifPreferenceTextView);

        Button addEvent = findViewById(R.id.add_event_button);
        addEvent.setOnClickListener(view -> {
            //addEvent();
        });

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(AddEvent.this, ManageEventsActivity.class);
            startActivity(intent);
        });


        /*
        Event event = new Event(
                eventName.getText().toString(),
                facility.getText().toString(),
                location.getText().toString(),
                dateTime.getText().toString(),
                description.getText().toString(),
                Integer.parseInt(maxAttendees.getText().toString()),
                geolocationRequirement.isChecked(),
                notifPreference.isChecked()
        );*/

    }
}
