package com.example.intels_app;

import static android.content.ContentValues.TAG;

import static com.example.intels_app.CreateQR.bitmapToByteArray;
import static com.example.intels_app.CreateQR.hashImage;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EditEventActivity extends AppCompatActivity {
    String eventName;
    EditText eventNameText;
    EditText maxAttendees;
    EditText location;
    EditText dateTime;
    EditText description;
    SwitchCompat geolocationRequirement;
    SwitchCompat notifPreference;
    ImageView qrImageView;
    ImageView posterImageView;
    String deviceID;
    private StorageReference storageReference;
    private Bitmap QRbitmap;
    private byte[] QRimageData;
    private String QRimageHash;
    private Bitmap posterBitmap;
    private byte[] posterImageData;
    private String posterImageHash;
    private ImageView cameraImage;
    private Uri posterImage;
    private String newQRUrl;

    // Flags to track changes
    boolean isQRChanged = false;
    boolean isPosterChanged = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_event_details);

        // Get the event name from the intent
        eventName = getIntent().getStringExtra("Event Name");

        // Back button logic
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(EditEventActivity.this, EventDetailsOrganizer.class);
            intent.putExtra("Event Name", eventName);
            startActivity(intent);
        });

        // Replace Poster button logic
        Button replacePosterButton = findViewById(R.id.replacePosterbutton);
        replacePosterButton.setOnClickListener(view -> {
            // Open the gallery to pick an image
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            openGallery.launch(intent);
        });

        // Replace QR button logic
        Button replaceQRButton = findViewById(R.id.replaceQRbutton);
        replaceQRButton.setOnClickListener(view -> {
            FirebaseFirestore.getInstance().collection("events").document(eventName).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Event event = documentSnapshot.toObject(Event.class);

                        if (event != null && event.getQrCodeUrl() != null && !event.getQrCodeUrl().isEmpty()) {
                            // Get the old QR code URL
                            String oldQRUrl = event.getQrCodeUrl();

                            // Create a reference to the old QR code in Firebase Storage
                            StorageReference oldQRRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldQRUrl);

                            // Delete the old QR code from Firebase Storage
                            oldQRRef.delete().addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Old QR code successfully deleted from Firebase Storage.");
                                // Set flag to indicate QR code change
                                isQRChanged = true;
                            }).addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to delete old QR code from Firebase Storage.", e);
                            });
                        } else {
                            // No old QR code, just flag it as changed
                            isQRChanged = true;
                        }
                    });
        });

        // Set up the UI elements
        eventNameText = findViewById(R.id.eventNameEditText);
        maxAttendees = findViewById(R.id.maxAttendeesEditText);
        location = findViewById(R.id.locationEditText);
        dateTime = findViewById(R.id.dateTimeEditText);
        description = findViewById(R.id.descriptionEditText);
        geolocationRequirement = findViewById(R.id.geolocationRequirementTextView);
        notifPreference = findViewById(R.id.notifPreferenceTextView);
        qrImageView = findViewById(R.id.qrImageView);
        posterImageView = findViewById(R.id.posterImageView);

        loadProfileData();

        // Save Changes button logic
        Button saveChangesButton = findViewById(R.id.save_changes_button);
        saveChangesButton.setOnClickListener(view -> {
            // Get updated values from the form fields
            String updatedEventName = eventNameText.getText().toString();
            String updatedMaxAttendees = maxAttendees.getText().toString();
            String updatedLocation = location.getText().toString();
            String updatedDateTime = dateTime.getText().toString();
            String updatedDescription = description.getText().toString();
            boolean updatedGeolocationRequirement = geolocationRequirement.isChecked();
            boolean updatedNotifPreference = notifPreference.isChecked();

            // Validate the inputs (you can add more checks as needed)
            if (updatedEventName.isEmpty() || updatedMaxAttendees.isEmpty() || updatedLocation.isEmpty()) {
                Toast.makeText(EditEventActivity.this, "Please fill in all the required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update the event in Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("events").document(eventName) // Use the original event name to get the document
                    .update(
                            "eventName", updatedEventName,
                            "maxAttendees", Integer.parseInt(updatedMaxAttendees), // Assuming maxAttendees is an integer
                            "location", updatedLocation,
                            "dateTime", updatedDateTime,
                            "description", updatedDescription,
                            "geolocationRequirement", updatedGeolocationRequirement,
                            "notifPreference", updatedNotifPreference
                    )
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Event details successfully updated!");
                        Toast.makeText(EditEventActivity.this, "Event details updated successfully!", Toast.LENGTH_SHORT).show();

                        // Upload the new poster and QR code if they've been changed
                        if (isQRChanged) {
                            generateAndUploadNewQRCode(); // This is your QR code upload method
                        }

                        if (isPosterChanged) {
                            replacePoster(); // This is your poster upload method
                        }

                        // Optionally, navigate back to the event details page or another activity
                        Intent intent = new Intent(EditEventActivity.this, EventDetailsOrganizer.class);
                        intent.putExtra("Event Name", updatedEventName); // Pass the updated event name back
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error updating event details", e);
                        Toast.makeText(EditEventActivity.this, "Failed to update event details", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    public void generateAndUploadNewQRCode() {
        try {
            // Use ZXing to generate QR code with only the event name
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            QRbitmap = barcodeEncoder.encodeBitmap(eventName, BarcodeFormat.QR_CODE, 200, 200);
            qrImageView.setImageBitmap(QRbitmap); // Display the new QR code in the ImageView

            // Convert Bitmap to byte array for storage
            QRimageData = bitmapToByteArray(QRbitmap);

            // Hash the byte array for storage
            QRimageHash = hashImage(QRimageData);

            // Upload the new QR code to Firebase Storage
            storageReference = FirebaseStorage.getInstance().getReference().child("QRCodes").child(QRimageHash);
            storageReference.putBytes(QRimageData)
                    .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                newQRUrl = uri.toString();

                                // Update the QR code URL in Firestore under the events collection
                                FirebaseFirestore.getInstance().collection("events").document(eventName).update("qrCodeUrl", newQRUrl)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d(TAG, "QR code URL successfully updated in Firestore.");

                                            // Load the new QR code image into the ImageView using Glide
                                            Glide.with(getApplicationContext())
                                                    .load(newQRUrl)
                                                    .placeholder(R.drawable.pfp_placeholder_image)
                                                    .error(R.drawable.person_image)
                                                    .into(qrImageView);
                                        })
                                        .addOnFailureListener(e -> Log.e(TAG, "Failed to update QR code URL in Firestore.", e));
                            })
                            .addOnFailureListener(e -> Log.e(TAG, "Failed to get new QR code URL from Firebase Storage.", e))
                    )
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to upload new QR code to Firebase Storage.", e));

        } catch (WriterException e) {
            Log.e(TAG, "Error generating QR code.", e);
        }
    }

    // Code for handling image selection and poster upload
    ActivityResultLauncher<Intent> openGallery = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        // Get the URI of the selected image
                        posterImage = result.getData().getData();

                        // Set the selected image into the ImageView
                        Glide.with(getApplicationContext()).load(posterImage).into(posterImageView);

                        // Hide the camera image once an image has been uploaded
                        cameraImage = findViewById(R.id.camera_image);
                        cameraImage.setVisibility(View.INVISIBLE);

                        try {
                            // Get Bitmap from Uri
                            posterBitmap = getBitmapFromUri(posterImage, getContentResolver());

                            // Convert Bitmap to byte array
                            posterImageData = bitmapToByteArray(posterBitmap);

                            // Hash the byte array
                            posterImageHash = hashImage(posterImageData);

                            // Set flag to indicate poster change
                            isPosterChanged = true;

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(EditEventActivity.this, "Error processing image", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(EditEventActivity.this, "Please select an image", Toast.LENGTH_LONG).show();
                }
            }
    );

    public void replacePoster() {
        storageReference = FirebaseStorage.getInstance().getReference().child("Posters").child(posterImageHash);
        storageReference.putBytes(posterImageData)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String newPosterUrl = uri.toString();

                            // Update the poster URL in Firestore under the events collection
                            FirebaseFirestore.getInstance().collection("events").document(eventName).update("posterUrl", newPosterUrl)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Poster URL successfully updated in Firestore.");

                                        // Load the new poster image into the ImageView using Glide
                                        Glide.with(getApplicationContext())
                                                .load(newPosterUrl)
                                                .placeholder(R.drawable.pfp_placeholder_image)
                                                .error(R.drawable.person_image)
                                                .into(posterImageView);
                                    })
                                    .addOnFailureListener(e -> Log.e(TAG, "Failed to update poster URL in Firestore.", e));
                        })
                        .addOnFailureListener(e -> Log.e(TAG, "Failed to get new poster URL from Firebase Storage.", e))
                )
                .addOnFailureListener(e -> Log.e(TAG, "Failed to upload new poster to Firebase Storage.", e));
    }

    public void loadProfileData() {
        FirebaseFirestore.getInstance().collection("events").document(eventName).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Event event = documentSnapshot.toObject(Event.class);

                    Log.d(TAG, "Event Name: " + event.getEventName());
                    Log.d(TAG, "Location: " + event.getLocation());
                    Log.d(TAG, "Date Time: " + event.getDateTime());
                    Log.d(TAG, "Description: " + event.getDescription());
                    Log.d(TAG, "Max Attendees: " + String.valueOf(event.getMaxAttendees()));
                    Log.d(TAG, "Geolocation Requirement: " + event.isGeolocationRequirement());
                    Log.d(TAG, "Notification Preference: " + event.isNotifPreference());

                    eventNameText.setText(event.getEventName());
                    maxAttendees.setText(String.valueOf(event.getMaxAttendees()));
                    location.setText(event.getLocation());
                    dateTime.setText(event.getDateTime());
                    description.setText(event.getDescription());
                    geolocationRequirement.setChecked(event.isGeolocationRequirement());
                    notifPreference.setChecked(event.isNotifPreference());

                    // Load event poster image using Glide
                    if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
                        Glide.with(getApplicationContext())
                                .load(event.getPosterUrl())
                                .placeholder(R.drawable.pfp_placeholder_image)
                                .error(R.drawable.person_image)
                                .into(posterImageView);
                    } else {
                        Log.w(TAG, "No poster URL found in the document");
                        posterImageView.setImageResource(R.drawable.person_image);
                    }

                    // Load qr code image using Glide
                    if (event.getQrCodeUrl() != null && !event.getQrCodeUrl().isEmpty()) {
                        Glide.with(getApplicationContext())
                                .load(event.getQrCodeUrl())
                                .placeholder(R.drawable.pfp_placeholder_image)
                                .error(R.drawable.person_image)
                                .into(qrImageView);
                    } else {
                        Log.w(TAG, "No poster URL found in the document");
                        qrImageView.setImageResource(R.drawable.pfp_placeholder_image);
                    }

                });
    }

    /**
     * Converts the image Uri to a Bitmap
     * @param uri The uri of the image selected from gallery
     * @param contentResolver Provide access to the content model
     * @return The Bitmap of the image
     * @throws IOException If there is an error processing the image
     */
    public Bitmap getBitmapFromUri(Uri uri, ContentResolver contentResolver) throws IOException {
        InputStream inputStream = contentResolver.openInputStream(uri);
        return BitmapFactory.decodeStream(inputStream);
    }

    /*
    public void replacePoster() {
        // Retrieve the current event data to get the old poster URL
        FirebaseFirestore.getInstance().collection("events").document(eventName).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Event event = documentSnapshot.toObject(Event.class);

                    if (event != null && event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
                        // Get the old poster URL
                        String oldPosterUrl = event.getPosterUrl();

                        // Create a reference to the old poster in Firebase Storage
                        StorageReference oldPosterRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldPosterUrl);

                        // Delete the old poster from Firebase Storage
                        oldPosterRef.delete()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Old poster successfully deleted from Firebase Storage.");

                                    // Now upload the new poster
                                    uploadNewPoster();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to delete old poster from Firebase Storage.", e);

                                    // Still proceed to upload the new poster even if the old deletion fails
                                    uploadNewPoster();
                                });
                    } else {
                        // If there's no old poster, just proceed with uploading the new one
                        uploadNewPoster();
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to retrieve event data from Firestore.", e));
    }*/

    public void uploadNewPoster() {
        if (posterImageData != null && posterImageHash != null) {
            storageReference = FirebaseStorage.getInstance().getReference().child("Posters").child(posterImageHash);
            storageReference.putBytes(posterImageData)
                    .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String newPosterUrl = uri.toString();
                                // Update Firestore with the new poster URL
                                FirebaseFirestore.getInstance().collection("events").document(eventName).update("posterUrl", newPosterUrl)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d(TAG, "Poster URL successfully updated in Firestore.");
                                            Glide.with(getApplicationContext())
                                                    .load(newPosterUrl)
                                                    .placeholder(R.drawable.pfp_placeholder_image)
                                                    .error(R.drawable.person_image)
                                                    .into(posterImageView);
                                        })
                                        .addOnFailureListener(e -> Log.e(TAG, "Failed to update poster URL in Firestore.", e));
                            })
                            .addOnFailureListener(e -> Log.e(TAG, "Failed to get new poster URL from Firebase Storage.", e)))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to upload new poster to Firebase Storage.", e));
        } else {
            Log.e(TAG, "Poster image data or hash is null, cannot upload.");
        }
    }
}


/*
                FirebaseFirestore.getInstance().collection("events").document(eventName).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                                        deviceID = document.getString("deviceId");
                                    } else {
                                        Log.d(TAG, "No such document");
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });

                Event updatedEvent = new Event(
                        eventNameText.getText().toString(),
                        location.getText().toString(),
                        dateTime.getText().toString(),
                        description.getText().toString(),
                        Integer.parseInt(maxAttendees.getText().toString()),
                        geolocationRequirement.isChecked(),
                        notifPreference.isChecked(),
                        posterImage.toString(),
                        newQRUrl,
                        deviceID
                );

                Map<String, Object> eventUpdates = new HashMap<>();
                eventUpdates.put("eventName", updatedEvent.getEventName());
                eventUpdates.put("location", updatedEvent.getLocation());
                eventUpdates.put("dateTime", updatedEvent.getDateTime());
                eventUpdates.put("description", updatedEvent.getDescription());
                eventUpdates.put("maxAttendees", updatedEvent.getMaxAttendees());
                eventUpdates.put("geolocationRequirement", updatedEvent.isGeolocationRequirement());
                eventUpdates.put("notifPreference", updatedEvent.isNotifPreference());
                eventUpdates.put("posterUrl", updatedEvent.getPosterUrl());
                eventUpdates.put("qrCodeUrl", updatedEvent.getQrCodeUrl());
                eventUpdates.put("deviceId", updatedEvent.getDeviceId());

                FirebaseFirestore.getInstance().collection("events").document(eventName).update(eventUpdates)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Event details successfully updated in Firestore.");
                        })
                        .addOnFailureListener(e -> Log.e(TAG, "Failed to update event details in Firestore.", e));

                Intent intent = new Intent(EditEventActivity.this, EventDetailsOrganizer.class);
                intent.putExtra("Event Name", eventName);
                startActivity(intent);
* */