package com.example.intels_app;

import static android.content.ContentValues.TAG;

import static com.example.intels_app.CreateQR.bitmapToByteArray;
import static com.example.intels_app.CreateQR.hashImage;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EditEventActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int PERMISSION_REQUEST_CODE = 100;
    String eventName;
    Event oldEvent;
    String finalPosterUrl;
    String finalQrUrl;
    private boolean isCameraOption = false;
    private boolean imageUploaded;
    Button replacePosterButton;
    Button replaceQRButton;
    Button saveChangesButton;
    ImageButton backButton;

    EditText eventNameText;
    EditText maxAttendees;
    EditText location;
    EditText dateTime;
    EditText description;
    SwitchCompat geolocationRequirement;
    ImageView qrImageView;
    ImageView posterImageView;

    String newQRImageHash;
    Uri newPosterImage;
    byte[] newPosterImageData;
    String newPosterImageHash;
    byte[] newQRImageData;

    // Flags to track changes
    boolean isQRChanged = false;
    boolean isPosterChanged = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_event_details);

        // Get the event name from the intent
        eventName = getIntent().getStringExtra("Event Name");

        Log.d(TAG, "Event Name Start: " + eventName);

        FirebaseFirestore.getInstance().collection("events").document(eventName).get()
                .addOnSuccessListener(documentSnapshot -> {
                    oldEvent = documentSnapshot.toObject(Event.class);

                    Log.d(TAG, "Event Name Old Event: " + oldEvent.getEventName());

                });

        // Set up the UI elements
        eventNameText = findViewById(R.id.eventNameEditText);
        maxAttendees = findViewById(R.id.maxAttendeesEditText);
        location = findViewById(R.id.locationEditText);
        dateTime = findViewById(R.id.dateTimeEditText);
        description = findViewById(R.id.descriptionEditText);
        geolocationRequirement = findViewById(R.id.geolocationRequirementTextView);
        qrImageView = findViewById(R.id.qrImageView);
        posterImageView = findViewById(R.id.posterImageView);

        // load current details
        loadProfileData();

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditEventActivity.this, EventDetailsOrganizer.class);
                intent.putExtra("Event Name", eventName);
                startActivity(intent);
            }
        });

        replacePosterButton = findViewById(R.id.replacePosterbutton);
        replacePosterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickerDialog();
                imageUploaded = true;
            }
        });

        replaceQRButton = findViewById(R.id.replaceQRbutton);
        replaceQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Generate new QR code and glide into image view
                try {
                    generateQRCode();
                    isQRChanged = true;
                } catch (WriterException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        saveChangesButton = findViewById(R.id.save_changes_button);
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Validate that all required fields are filled
                if (!areFieldsValid()) {
                    Toast.makeText(EditEventActivity.this, "Please fill out all fields before saving changes.", Toast.LENGTH_LONG).show();
                    return; // Do not proceed with saving changes
                }

                if (isPosterChanged) {
                    // Delete old poster and upload the new one
                    FirebaseStorage.getInstance().getReferenceFromUrl(oldEvent.getPosterUrl()).delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "Old poster deleted successfully");

                                    // Upload new poster to storage
                                    FirebaseStorage.getInstance().getReference().child("posters").child(newPosterImageHash).putFile(newPosterImage)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Log.d(TAG, "New poster uploaded successfully");

                                                    // Get URL of new poster
                                                    FirebaseStorage.getInstance().getReference("posters").child(newPosterImageHash).getDownloadUrl()
                                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri) {
                                                                    finalPosterUrl = uri.toString();
                                                                    Log.d(TAG, "New poster URL: " + finalPosterUrl);

                                                                    // Check if we are ready to update Firestore
                                                                    checkAndUpdateFirestore();
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                } else {
                    finalPosterUrl = oldEvent.getPosterUrl();

                    // Check if we are ready to update Firestore
                    checkAndUpdateFirestore();
                }

                if (isQRChanged) {
                    // Delete old QR and upload the new one
                    FirebaseStorage.getInstance().getReferenceFromUrl(oldEvent.getQrCodeUrl()).delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "Old QR deleted successfully");

                                    // Upload new QR to storage
                                    FirebaseStorage.getInstance().getReference().child("QRCodes").child(newQRImageHash).putBytes(newQRImageData)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Log.d(TAG, "New QR uploaded successfully");

                                                    // Get URL of new QR
                                                    FirebaseStorage.getInstance().getReference().child("QRCodes").child(newQRImageHash).getDownloadUrl()
                                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri) {

                                                                    finalQrUrl = uri.toString();
                                                                    Log.d(TAG, "New QR URL: " + finalQrUrl);

                                                                    // Proceed to update Firestore after QR URL is ready
                                                                    checkAndUpdateFirestore();
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                } else {
                    finalQrUrl = oldEvent.getQrCodeUrl();

                    // Check if we are ready to update Firestore
                    checkAndUpdateFirestore();
                }
            }
        });
    }

    private boolean areFieldsValid() {
        if (eventNameText.getText().toString().trim().isEmpty()) return false;
        if (maxAttendees.getText().toString().trim().isEmpty()) return false;
        if (location.getText().toString().trim().isEmpty()) return false;
        if (dateTime.getText().toString().trim().isEmpty()) return false;
        if (description.getText().toString().trim().isEmpty()) return false;
        return true;
    }

    public void loadProfileData() {
        FirebaseFirestore.getInstance().collection("events").document(eventName).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Event event = documentSnapshot.toObject(Event.class);

                    // Set original event details into textview
                    eventNameText.setText(event.getEventName());
                    maxAttendees.setText(String.valueOf(event.getMaxAttendees()));
                    location.setText(event.getLocation());
                    dateTime.setText(event.getDateTime());
                    description.setText(event.getDescription());
                    geolocationRequirement.setChecked(event.isGeolocationRequirement());

                    Log.d(TAG, "Event name" + event.getEventName());
                    Log.d(TAG, "Max Attendees" + String.valueOf(event.getMaxAttendees()));
                    Log.d(TAG, "Location" + event.getLocation());
                    Log.d(TAG, "Date Time" + event.getDateTime());
                    Log.d(TAG, "Description" + event.getDescription());
                    Log.d(TAG, "Geolocation Requirement" + String.valueOf(event.isGeolocationRequirement()));

                    // Load event poster image using Glide
                    if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
                        com.bumptech.glide.Glide.with(getApplicationContext())
                                .load(event.getPosterUrl())
                                .placeholder(R.drawable.pfp_placeholder_image)
                                .error(R.drawable.pfp_placeholder_image)
                                .into(posterImageView);
                    } else {
                        Log.w(TAG, "No poster URL found in the document");
                        posterImageView.setImageResource(R.drawable.pfp_placeholder_image);
                    }

                    Log.d(TAG, "Poster URL" + event.getPosterUrl());

                    // Load qr code image using Glide
                    if (event.getQrCodeUrl() != null && !event.getQrCodeUrl().isEmpty()) {
                        com.bumptech.glide.Glide.with(getApplicationContext())
                                .load(event.getQrCodeUrl())
                                .placeholder(R.drawable.pfp_placeholder_image)
                                .error(R.drawable.pfp_placeholder_image)
                                .into(qrImageView);
                    } else {
                        Log.w(TAG, "No poster URL found in the document");
                        qrImageView.setImageResource(R.drawable.pfp_placeholder_image);
                    }

                    Log.d(TAG, "QR URL" + event.getQrCodeUrl());

                });
    }

    public void generateQRCode() throws WriterException {
        // Use ZXing to generate QR code with only the event name
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap newQRbitmap = barcodeEncoder.encodeBitmap(eventName, BarcodeFormat.QR_CODE, 200, 200);
        qrImageView.setImageBitmap(newQRbitmap); // Display the new QR code in the ImageView

        // Convert Bitmap to byte array for storage
        newQRImageData = bitmapToByteArray(newQRbitmap);

        // Hash the byte array for storage
        newQRImageHash = hashImage(newQRImageData);

        // Show in the app
        Glide.with(getApplicationContext()).load(newQRbitmap).into(qrImageView);
    }


    public void checkAndUpdateFirestore() {

        // Ensure both URLs are ready
        if ((isPosterChanged && finalPosterUrl == null) || (isQRChanged && finalQrUrl == null)) {
            // Wait for both URLs to be retrieved
            return;
        }

        // Get updated values from the form fields
        String updatedEventName = eventNameText.getText().toString();
        String updatedMaxAttendees = maxAttendees.getText().toString();
        String updatedLocation = location.getText().toString();
        String updatedDateTime = dateTime.getText().toString();
        String updatedDescription = description.getText().toString();
        boolean updatedGeolocationRequirement = geolocationRequirement.isChecked();

        // Update event details in Firestore with new edit text details and URLs
        FirebaseFirestore.getInstance().collection("events").document(eventName)
                .update("eventName", updatedEventName,
                        "maxAttendees", Integer.parseInt(updatedMaxAttendees),
                        "location", updatedLocation,
                        "dateTime", updatedDateTime,
                        "description", updatedDescription,
                        "geolocationRequirement", updatedGeolocationRequirement,
                        "posterUrl", finalPosterUrl,
                        "qrCodeUrl", finalQrUrl
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Event details successfully updated!");
                    }
                });

        // Navigate back to the event details page or another activity
        Intent intent = new Intent(EditEventActivity.this, EventDetailsOrganizer.class);
        intent.putExtra("Event Name", updatedEventName); // Pass the updated event name back
        startActivity(intent);
    }

    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Event Poster");
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

    @Override
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
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    private void openGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                if (bitmap != null) {
                    posterImageView.setImageBitmap(bitmap); // Display the image in ImageView
                    newPosterImageData = bitmapToByteArray(bitmap);
                    newPosterImageHash = hashImage(newPosterImageData);// Convert to byte array if needed
                }
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                Uri selectedImage = data.getData();
                try {
                    // Decode and scale the selected image to fit within the ImageView
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    posterImageView.setImageBitmap(bitmap);
                    newPosterImageData = bitmapToByteArray(bitmap); // Convert to byte array if needed
                    newPosterImageHash = hashImage(newPosterImageData);
                    Log.d(TAG, "Gallery Image Set - imageHash: " + newPosterImageHash);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

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