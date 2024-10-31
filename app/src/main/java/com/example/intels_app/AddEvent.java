package com.example.intels_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ImageReader;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.gms.tasks.OnFailureListener;

import java.io.ByteArrayOutputStream;

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
            Event event = new Event(
                    eventName.getText().toString(),
                    facility.getText().toString(),
                    location.getText().toString(),
                    dateTime.getText().toString(),
                    description.getText().toString(),
                    Integer.parseInt(maxAttendees.getText().toString()),
                    geolocationRequirement.isChecked(),
                    notifPreference.isChecked()
            );
            //addEvent();
        });

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(AddEvent.this, ManageEventsActivity.class);
            startActivity(intent);
        });

        Button addPosterButton = findViewById(R.id.edit_poster_button);
        addPosterButton.setOnClickListener(view -> {

            ImageView imageView = findViewById(R.id.camera_image);

            // Get the data from an ImageView as bytes
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

        });
    }
}
