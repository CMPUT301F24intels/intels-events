package com.example.intels_app;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class AddEvent extends AppCompatActivity {
    StorageReference storageReference;
    Uri image;
    ImageView imageView;
    Button addPosterButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);

        FirebaseApp.initializeApp(this);
        storageReference = FirebaseStorage.getInstance().getReference();

        imageView = findViewById(R.id.camera_image);

        // Go back to Manage Events if back button clicked
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(AddEvent.this, ManageEventsActivity.class);
            startActivity(intent);
        });

        // Select image from gallery if Edit Image Button clicked
        Button addPosterButton = findViewById(R.id.edit_poster_button);
        addPosterButton.setOnClickListener(view -> {

            openGallery(); // Get image from gallery and show it on the UI

            ImageView imageView = findViewById(R.id.camera_image);

            // Get the data from an ImageView as bytes
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
        });

        // Create a new event with entered details if Add Event button clicked
        Button addEvent = findViewById(R.id.add_event_button);
        addEvent.setOnClickListener(view -> {

            // Get user's event details input values
            EditText maxAttendees = findViewById(R.id.max_attendees_number);
            EditText eventName = findViewById(R.id.eventNameEditText);
            EditText facility = findViewById(R.id.facilityEditText);
            EditText location = findViewById(R.id.locationEditText);
            EditText dateTime = findViewById(R.id.dateTimeEditText);
            EditText description = findViewById(R.id.descriptionEditText);
            SwitchCompat geolocationRequirement = findViewById(R.id.geolocationRequirementTextView);
            SwitchCompat notifPreference = findViewById(R.id.notifPreferenceTextView);

            // Create a new event with the entered details
            Event newEvent = new Event(
                    eventName.getText().toString(),
                    facility.getText().toString(),
                    location.getText().toString(),
                    dateTime.getText().toString(),
                    description.getText().toString(),
                    Integer.parseInt(maxAttendees.getText().toString()),
                    geolocationRequirement.isChecked(),
                    notifPreference.isChecked()
            );

            // Add the new event to the database
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference eventsRef = db.collection("events");
            eventsRef.add(newEvent)
                    .addOnSuccessListener(documentReference -> {
                        Intent intent = new Intent(AddEvent.this, ManageEventsActivity.class);
                        startActivity(intent);
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });

            // Add the poster image to Firebase Storage
            //uploadImage(image);

            // Return to Manage Events activity
            Intent intent = new Intent(AddEvent.this, ManageEventsActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Opens the gallery to select an image.
     */
    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        openGallery.launch(intent);
    }

    /**
     * Registers an activity result launcher for the gallery.
     */
    ActivityResultLauncher<Intent> openGallery = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        addPosterButton.setEnabled(true);
                        image = result.getData().getData();
                        Glide.with(getApplicationContext()).load(image).into(imageView); // Put uploaded image into imageView
                    }
                } else {
                    Toast.makeText(AddEvent.this, "Please select an image", Toast.LENGTH_LONG).show();
                }
            }
        });

    private void uploadImage(Uri image) {
        StorageReference reference = storageReference.child("/images" + UUID.randomUUID().toString());
        reference.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddEvent.this, "Poster added successfully!", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddEvent.this, "There was an error in uploading the poster", Toast.LENGTH_LONG).show();
            }
        });
    }
}
