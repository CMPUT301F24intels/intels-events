package com.example.intels_app;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class EventDetailsOrganizer extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details);

        String eventName = getIntent().getStringExtra("Event Name");

        // Get views from layout
        TextView eventNameTextView = findViewById(R.id.eventNameEditText);
        TextView facilityTextView = findViewById(R.id.facilityEditText);
        TextView locationTextView = findViewById(R.id.locationEditText);
        TextView dateTimeTextView = findViewById(R.id.dateTimeEditText);
        TextView descriptionTextView = findViewById(R.id.descriptionEditText);
        TextView maxAttendeesTextView = findViewById(R.id.max_attendees_textview);
        TextView geolocationRequirementTextView = findViewById(R.id.geolocationRequirementTextView);
        TextView notificationPreferenceTextView = findViewById(R.id.notificationPreferenceTextView);
        ImageView posterImageView = findViewById(R.id.posterImageView);
        //ImageView qrCodeImageView = findViewById(R.id.qrCodeImageView);

        // Get event info from Firestore
        DocumentReference documentRef = FirebaseFirestore.getInstance().collection("events").document(eventName);
        documentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Event event = documentSnapshot.toObject(Event.class);
                String eventName = event.getEventName();
                String facility = event.getFacilityName();
                String location = event.getLocation();
                String dateTime = event.getDateTime();
                String description = event.getDescription();
                int maxAttendees = event.getMaxAttendees();
                boolean geolocationRequirement = event.isGeolocationRequirement();
                boolean notificationPreference = event.isNotifPreference();
                String posterUrl = event.getPosterUrl();
                //String qrCodeUrl = event.getQrCodeUrl();

                eventNameTextView.setText("Event Name: " + eventName);
                facilityTextView.setText("Facility: " + facility);
                locationTextView.setText("Location: " + location);
                dateTimeTextView.setText("Date and Time: " + dateTime);
                descriptionTextView.setText("Description: " + description);
                maxAttendeesTextView.setText("Max Attendees: " + maxAttendees);
                geolocationRequirementTextView.setText("Geolocation Requirement: " + geolocationRequirement);
                notificationPreferenceTextView.setText("Notification Preference: " + notificationPreference);

                if (posterUrl != null) {
                    Glide.with(getApplicationContext()).load(posterUrl).into(posterImageView); // Put image into imageView
                } else {
                    Log.w(TAG, "No poster URL found in the document");
                }


            }
        }).addOnFailureListener(e -> Log.w(TAG, "Error getting document", e));

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventDetailsOrganizer.this, ManageEventsActivity.class);
                startActivity(intent);
            }
        });
    }
}
