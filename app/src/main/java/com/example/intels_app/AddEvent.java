/**
 * @author Janan Panchal
 * @see com.example.intels_app.ManageEventsActivity
 * @see com.example.intels_app.CreateQR
 *
 */
package com.example.intels_app;

import static android.content.ContentValues.TAG;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.UUID;

public class AddEvent extends AppCompatActivity {
    StorageReference storageReference;
    ImageView posterImageView;
    ImageView cameraImage;
    Uri image;
    Bitmap bitmap;
    byte[] imageData;
    String imageHash;
    ImageButton backButton;
    Button addPosterButton;
    Button addEvent;

    /**
     * Opens the Add Event layout that allows organizers to enter details for their new event
     * Handles the buttons:
     *      Back Button - return to the Manage Events activity
     *      Add Poster Button = select a poster image from gallery
     *      Add Event Button - create a new event with the entered details
     * Pressing the Add Event Button gets the new event details entered and creates a new Event
     * object to FireStore
     *
     * @param savedInstanceState - Bundle to save the state of the activity
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);

        // Where the event poster will be uploaded
        posterImageView = findViewById(R.id.pfpPlaceholder);

        // Go back to Manage Events if back button clicked
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(AddEvent.this, ManageEventsActivity.class);
            startActivity(intent);
        });

        // Select image from gallery if Add Poster Button is clicked
        addPosterButton = findViewById(R.id.edit_poster_button);
        addPosterButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            openGallery.launch(intent);
        });

        // Create a new event with entered details if Add Event button clicked
        addEvent = findViewById(R.id.add_event_button);
        addEvent.setOnClickListener(view -> {
            // Get user's event details text fields and switches
            EditText maxAttendees = findViewById(R.id.max_attendees_number);
            EditText eventName = findViewById(R.id.eventNameEditText);
            EditText facility = findViewById(R.id.facilityEditText);
            EditText location = findViewById(R.id.locationEditText);
            EditText dateTime = findViewById(R.id.dateTimeEditText);
            EditText description = findViewById(R.id.descriptionEditText);
            SwitchCompat geolocationRequirement = findViewById(R.id.geolocationRequirementTextView);
            SwitchCompat notifPreference = findViewById(R.id.notifPreferenceTextView);

            // Get device ID so we know which organizer is adding an event
            // During creation, the event will be added for the organizer with this device ID
            FirebaseInstallations.getInstance().getId()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String deviceId = task.getResult();

                    // Upload the poster to storage, named with by the imageHash. Get the download Url created by storage
                    // and save it in posterUrl to be added to the new Event object
                    storageReference = FirebaseStorage.getInstance().getReference().child("posters").child(imageHash);
                    storageReference.putBytes(imageData)
                            .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        String posterUrl = uri.toString();

                                        // Create a new event with the entered details and device ID
                                        Event newEvent = new Event(
                                                eventName.getText().toString(),
                                                facility.getText().toString(),
                                                location.getText().toString(),
                                                dateTime.getText().toString(),
                                                description.getText().toString(),
                                                Integer.parseInt(maxAttendees.getText().toString()),
                                                geolocationRequirement.isChecked(),
                                                notifPreference.isChecked(),
                                                posterUrl,
                                                deviceId // Add the device ID here
                                        );

                                        // Save new event to FireStore under the events collection. Name it by the event name
                                        FirebaseFirestore.getInstance().collection("events").document(eventName.getText().toString())
                                        .set(newEvent)
                                                .addOnSuccessListener(documentReference -> {
                                                    Intent intent = new Intent(AddEvent.this, CreateQR.class);
                                                    // Pass all necessary details to CreateQR activity
                                                    intent.putExtra("Event Name", eventName.getText().toString());
                                                    intent.putExtra("Facility", facility.getText().toString());
                                                    intent.putExtra("Location", location.getText().toString());
                                                    intent.putExtra("DateTime", dateTime.getText().toString());
                                                    intent.putExtra("Description", description.getText().toString());
                                                    intent.putExtra("Max Attendees", Integer.parseInt(maxAttendees.getText().toString()));
                                                    startActivity(intent);
                                                })
                                                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
                                    })
                            ).addOnFailureListener(e -> {
                                Log.w(TAG, "Image upload failed", e);
                                Toast.makeText(AddEvent.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Log.e("FirebaseInstallations", "Unable to get device ID", task.getException());
                }
            });
        });
    }

    /**
     * When the organizer wants to upload an image, it opens gallery, converts the Bitmap of the selected image
     * to a byte array and hashes it
     */
    ActivityResultLauncher<Intent> openGallery = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        image = result.getData().getData();
                        Glide.with(getApplicationContext()).load(image).into(posterImageView); // Put uploaded image into imageView

                        // Hide the camera image once an image has been uploaded
                        cameraImage = findViewById(R.id.camera_image);
                        cameraImage.setVisibility(View.INVISIBLE);

                        try {
                            //Get Bitmap from Uri
                            bitmap = getBitmapFromUri(image, getContentResolver());

                            // Convert Bitmap to byte array
                            imageData = bitmapToByteArray(bitmap);

                            // Hash the byte array
                            imageHash = hashImage(imageData);

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(AddEvent.this, "Error processing image", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(AddEvent.this, "Please select an image", Toast.LENGTH_LONG).show();
                }
            }
    );

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

    /**
     * Converts the Bitmap to a byte array
     * @param bitmap The bitmap of the image
     * @return The byte array of the image
     */
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // Keep original quality
        return baos.toByteArray();
    }

    /**
     * Hashes the byte array of the image for use in naming the image in Firebase Storage
     * @param imageData The byte array of the image
     * @return The hash of the image
     */
    public static String hashImage(byte[] imageData) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(imageData);

            // Convert bytes to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}