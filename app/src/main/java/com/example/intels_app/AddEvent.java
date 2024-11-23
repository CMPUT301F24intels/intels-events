/**
 * An organizer can enter details to create a new event including a poster.
 * The event gets created by the system and FireStore is updated with the information.
 * @author Janan Panchal, Dhanshri Patel
 * @see com.example.intels_app.ManageEventsActivity Back button leads to main page
 * @see com.example.intels_app.CreateQR Adding an even generates a QR code
 * @see com.example.intels_app.Event Event object
 *
 */
package com.example.intels_app;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private boolean isCameraOption = false;
    private StorageReference storageReference;
    private ImageView cameraImage;
    private byte[] imageData;
    private String imageHash;
    private ImageButton backButton;
    private Button addPosterButton;
    private Button addEvent;

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
        cameraImage = findViewById(R.id.camera_image);

        // Go back to Manage Events if back button clicked
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(AddEvent.this, ManageEventsActivity.class);
            startActivity(intent);
        });

        // Select image from gallery if Add Poster Button is clicked
        addPosterButton = findViewById(R.id.edit_poster_button);
        addPosterButton.setOnClickListener(view -> showImagePickerDialog());

        // Create a new event with entered details if Add Event button clicked
        addEvent = findViewById(R.id.add_event_button);
        addEvent.setOnClickListener(view -> {
            // Get user's event details text fields and switches
            EditText maxAttendees = findViewById(R.id.max_attendees_number);
            EditText eventName = findViewById(R.id.eventNameEditText);
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
                                                    intent.putExtra("Event Name", eventName.getText().toString());
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

    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Profile Picture");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Take Photo
                    isCameraOption = true;
                    if (checkAndRequestPermissions()) {
                        openCamera();
                    }
                    break;
                case 1: // Choose from Gallery
                    isCameraOption = false;
                    if (checkAndRequestPermissions()) {
                        openGallery();
                    }
                    break;
            }
        });
        builder.show();
    }

    private boolean checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isCameraOption) {
                    openCamera();
                } else {
                    openGallery();
                }
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivity(intent);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void openGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null && data.getExtras() != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");  // Get thumbnail from camera
                if (bitmap != null) {
                    cameraImage.setImageBitmap(bitmap); // Display the image in ImageView
                    imageData = bitmapToByteArray(bitmap);// Convert to byte array if needed
                    imageHash = hashImage(imageData);
                }
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                Uri selectedImageUri = data.getData();  // Get URI of selected image
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    cameraImage.setImageBitmap(bitmap); // Display the image in ImageView
                    imageData = bitmapToByteArray(bitmap); // Convert to byte array if needed
                    imageHash = hashImage(imageData);
                    Log.d(TAG, "Gallery Image Set - imageHash: " + imageHash);  // Log for verification
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            }
        }
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