package com.example.intels_app;

import static android.content.ContentValues.TAG;

import android.content.ContentResolver;
import android.content.Intent;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CreateFacility extends AppCompatActivity {
    Uri image;
    ImageView imageView;
    String imageHash;
    byte[] imageData;
    boolean imageUploaded = false;
    String deviceId;
    Facility facility;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_facility);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        deviceId = task.getResult();
                        Log.d("DeviceID", "Device ID (Firebase Token): " + deviceId);

                        // Use of deviceId to track the organizer's device in Firestore
                    } else {
                        Log.e("DeviceID", "Failed to get Firebase Instance ID", task.getException());
                    }
                });

        imageView = findViewById(R.id.pfpPlaceholder);

        Button addFacilityImage = findViewById(R.id.edit_poster_button);
        addFacilityImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            openGallery.launch(intent);
            imageUploaded = true;
        });

        Button makeChanges = findViewById(R.id.edit_facility_details_button);
        makeChanges.setOnClickListener(view -> {

            EditText facilityName = findViewById(R.id.facilityNameEditText);
            EditText location = findViewById(R.id.locationEditText);
            EditText email = findViewById(R.id.emailEditText);
            EditText telephone = findViewById(R.id.telephoneEditText);

            String facilityNameStr = facilityName.getText().toString();
            String locationStr = location.getText().toString();
            String emailStr = email.getText().toString();
            Integer telephoneNum = Integer.parseInt(telephone.getText().toString());

            if (imageUploaded && imageData != null) {
                // Upload the image and then create the facility
                uploadImageAndSaveFacility(facilityNameStr, locationStr, emailStr, telephoneNum);
            } else {
                // No image uploaded, save the facility without image URL
                saveFacilityToFirestore(facilityNameStr, locationStr, emailStr, telephoneNum, "", deviceId);
            }
        });
    }

    private void uploadImageAndSaveFacility(String facilityName, String location, String email, int telephone) {
        if (imageHash == null) {
            Toast.makeText(this, "Image hash is null, cannot proceed.", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("facilities").child(imageHash);
        storageReference.putBytes(imageData)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String facilityImageUrl = uri.toString();
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

    private void saveFacilityToFirestore(String facilityName, String location, String email, int telephone, String imageUrl, String deviceID) {
        Facility facility = new Facility(facilityName, location, email, telephone, imageUrl, deviceID);

        FirebaseFirestore.getInstance().collection("facilities").document(facilityName)
                .set(facility)
                .addOnSuccessListener(documentReference -> {
                    Intent intent = new Intent(CreateFacility.this, MainPageActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Failed to save facility to Firestore", e);
                    Toast.makeText(CreateFacility.this, "Failed to save facility", Toast.LENGTH_SHORT).show();
                });
    }

    ActivityResultLauncher<Intent> openGallery = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        image = result.getData().getData();
                        Glide.with(getApplicationContext()).load(image).into(imageView); // Put uploaded image into imageView
                        ImageView cameraImage = findViewById(R.id.camera_image);
                        cameraImage.setVisibility(View.INVISIBLE);

                        try {
                            // Step 1: Get Bitmap from Uri
                            Bitmap bitmap = getBitmapFromUri(image, getContentResolver());

                            // Step 2: Convert Bitmap to byte array
                            imageData = bitmapToByteArray(bitmap);

                            // Step 3: Hash the byte array
                            imageHash = hashImage(imageData);

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(CreateFacility.this, "Error processing image", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(CreateFacility.this, "Please select an image", Toast.LENGTH_LONG).show();
                }
            }
    );

    public Bitmap getBitmapFromUri(Uri uri, ContentResolver contentResolver) throws IOException {
        InputStream inputStream = contentResolver.openInputStream(uri);
        return BitmapFactory.decodeStream(inputStream);
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
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