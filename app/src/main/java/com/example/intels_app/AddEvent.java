package com.example.intels_app;

import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddEvent extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);
    }

    EditText maxAttendees = findViewById(R.id.max_attendees_number);
    EditText eventName = findViewById(R.id.eventNameEditText);
    EditText facility = findViewById(R.id.facilityEditText);
    EditText location = findViewById(R.id.locationEditText);
    EditText dateTime = findViewById(R.id.dateTimeEditText);
    EditText description = findViewById(R.id.descriptionEditText);
}
