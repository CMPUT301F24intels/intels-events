/**
 * When the user uses the app for the first time (new device ID), prompt them to create a facility profile
 * @author Janan Panchal
 * @see com.example.intels_app.MainActivity Creating a profile leads to main page
 * @see com.example.intels_app.Facility Facility object
 */
package com.example.intels_app;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CreateFacility extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private boolean isCameraOption = false;
    private ImageView imageView;
    private String imageHash;
    private byte[] imageData;
    private boolean imageUploaded = false;
    private String deviceId;
    private ImageButton back_button;

    /**
     * Create the facility profile using the user-entered details
     * @param savedInstanceState Bundle to save the state of the activity
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_facility);

        deviceId = getIntent().getStringExtra("deviceId");

        back_button = findViewById(R.id.back_button_facility);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateFacility.this, MainActivity.class);
                startActivity(intent);
            }
        });

        /*
        // Get the current user's device Id to add as a parameter to their facility profile
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        deviceId = task.getResult();
                        Log.d("DeviceID", "Device ID (Firebase Token): " + deviceId);

                    } else {
                        Log.e("DeviceID", "Failed to get Firebase Instance ID", task.getException());
                    }
                });*/
        imageView = findViewById(R.id.camera_image);

        // Add an image to the facility
        Button addFacilityImage = findViewById(R.id.edit_poster_button);
        addFacilityImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickerDialog();
                imageUploaded = true;
            }
        });

        // Click the create button to create the facility and add it to FireStore
        Button create = findViewById(R.id.edit_facility_details_button);
        create.setOnClickListener(view -> {

            // Initialize the fields
            EditText facilityName = findViewById(R.id.facilityNameEditText);
            EditText location = findViewById(R.id.locationEditText);
            EditText email = findViewById(R.id.emailEditText);
            EditText telephone = findViewById(R.id.telephoneEditText);

            // Get the facility details the user entered
            String facilityNameStr = facilityName.getText().toString();
            String locationStr = location.getText().toString();
            String emailStr = email.getText().toString();
            String telephoneNum = telephone.getText().toString();

            boolean isValid = true;

            if (facilityNameStr.isEmpty()) {
                facilityName.setError("Facility Name is required");
                isValid = false;
            }
            if (locationStr.isEmpty()) {
                location.setError("Location is required");
                isValid = false;
            }
            if (emailStr.isEmpty()) {
                email.setError("Email is required");
                isValid = false;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
                Toast.makeText(CreateFacility.this, "Enter a valid email address.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (telephoneNum.isEmpty()) {
                telephone.setError("Telephone is required");
                isValid = false;
            }

            // If validation fails, stop further execution
            if (!isValid) {
                Toast.makeText(CreateFacility.this, "Please fill out all mandatory fields correctly", Toast.LENGTH_SHORT).show();
                return; // Do not proceed further
            }

            // If an image was uploaded, add it to Firebase Storage and then create the facility
            if (imageUploaded && imageData != null) {
                // Upload the image and then create the facility
                uploadImageAndSaveFacility(facilityNameStr, locationStr, emailStr, telephoneNum);
            } else {
                // No image uploaded, save the facility without image URL
                saveFacilityToFirestore(facilityNameStr, locationStr, emailStr, telephoneNum, "", deviceId);
            }
        });
    }

    /**
     * If an image was uploaded, add it to Firebase Storage and then create the facility in FireStore
     * @param facilityName Name of the facility
     * @param location Location of the facility
     * @param email Email of the facility
     * @param telephone Telephone number of the facility
     */
    private void uploadImageAndSaveFacility(String facilityName, String location, String email, String telephone) {
        if (imageHash == null) {
            Toast.makeText(this, "Image hash is null, cannot proceed.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload the image to Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("facilities").child(imageHash);
        storageReference.putBytes(imageData)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String facilityImageUrl = uri.toString();

                            // Add the Facility object to FireStore
                            saveFacilityToFirestore(facilityName, location, email, telephone, facilityImageUrl, deviceId);
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Failed to get download URL", e);
                            Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to upload image data", e);
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Add the Facility object to FireStore
     * @param facilityName Name of the facility
     * @param location Location of the facility
     * @param email Email of the facility
     * @param telephone Telephone number of the facility
     * @param imageUrl URL of the facility's image
     * @param deviceID Device ID of the user
     */
    private void saveFacilityToFirestore(String facilityName, String location, String email, String telephone, String imageUrl, String deviceID) {

        // Create new facility object
        Facility facility = new Facility(facilityName, location, email, telephone, imageUrl, deviceID);

        // Add to FireStore
        FirebaseFirestore.getInstance().collection("facilities").document(deviceID)
                .set(facility)
                .addOnSuccessListener(documentReference -> {
                    Intent intent = new Intent(CreateFacility.this, ManageEventsActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Failed to save facility to Firestore", e);
                    Toast.makeText(CreateFacility.this, "Failed to save facility", Toast.LENGTH_SHORT).show();
                });
    }

    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery",};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Facility Poster");
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
                    imageView.setImageBitmap(bitmap); // Display the image in ImageView
                    imageData = bitmapToByteArray(bitmap);
                    imageHash = hashImage(imageData);// Convert to byte array if needed
                }
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                Uri selectedImage = data.getData();
                try {
                    // Decode and scale the selected image to fit within the ImageView
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    imageView.setImageBitmap(bitmap);
                    imageData = bitmapToByteArray(bitmap); // Convert to byte array if needed
                    imageHash = hashImage(imageData);
                    Log.d(TAG, "Gallery Image Set - imageHash: " + imageHash);
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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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