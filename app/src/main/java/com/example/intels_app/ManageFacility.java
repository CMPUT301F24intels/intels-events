package com.example.intels_app;

import static android.content.ContentValues.TAG;

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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * Allows the organizer to manage their facility profile by editing the details.
 * @author Janan Panchal, Dhanshri Patel
 * @see com.example.intels_app.Facility Facility object
 * @see com.example.intels_app.ManageEventsActivity Back button leads to manage events page
 */

public class ManageFacility extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private boolean isCameraOption = false;
    private FirebaseFirestore db;
    private Facility oldFacility;
    private String oldPosterUrl;
    private String finalPosterUrl;
    private String imageHash;
    byte[] imageData;
    boolean imageUploaded = false;
    private String deviceId;
    private EditText facilityName, location, email, telephone;
    private Button makeChanges;
    private ImageView poster;

    /**
     * Called when the activity is first created. Initializes views and listeners, sets up Firestore,
     * and loads facility details from the database.
     * @param savedInstanceState The saved state of the application.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_facility);
        db = FirebaseFirestore.getInstance();


        makeChanges = findViewById(R.id.edit_facility_details_button);

        FirebaseInstallations.getInstance().getId()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        deviceId = task.getResult();
                        Log.d(TAG, "Retrieved Device ID: " + deviceId);
                        loadFacilityDetails();
                        makeChanges.setOnClickListener(view -> savePosterChanges());
                    } else {
                        Toast.makeText(this, "Error retrieving Device ID", Toast.LENGTH_SHORT).show();
                    }
                });

        facilityName = findViewById(R.id.facilityNameEditText);
        location = findViewById(R.id.locationEditText);
        email = findViewById(R.id.emailEditText);
        telephone = findViewById(R.id.telephoneEditText);
        poster = findViewById(R.id.camera_image);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageFacility.this, ManageEventsActivity.class);
                startActivity(intent);
            }
        });

        Button addFacilityImage = findViewById(R.id.edit_poster_button);
        addFacilityImage.setOnClickListener(view -> {
            showImagePickerDialog();
            imageUploaded = true;
        });
    }

    /**
     * Saves the changes made to the facility, including uploading a new poster if necessary.
     * This method updates the facility information in Firestore.
     */
    private void savePosterChanges() {

        // Validate fields
        if (!areFieldsValid()) {
            return; // Exit the method if validation fails
        }

        if (imageUploaded) {
            db.collection("facilities")
                    .whereEqualTo("deviceId", deviceId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            oldFacility = documentSnapshot.toObject(Facility.class);
                            if (oldFacility != null) {
                                oldPosterUrl = oldFacility.getFacilityImageUrl();
                            }
                        }
                        FirebaseStorage.getInstance().getReferenceFromUrl(oldPosterUrl).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "Old poster deleted successfully");

                                        FirebaseStorage.getInstance().getReference().child("facilities")
                                                .child(imageHash)
                                                .putBytes(imageData)
                                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        Log.d(TAG, "New poster uploaded successfully");

                                                        FirebaseStorage.getInstance().getReference().child("facilities").child(imageHash).getDownloadUrl()
                                                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                    @Override
                                                                    public void onSuccess(Uri uri) {
                                                                        finalPosterUrl = uri.toString();
                                                                        Log.d(TAG, "New Poster URL: " + finalPosterUrl);

                                                                        Facility facility = new Facility(
                                                                                facilityName.getText().toString(),
                                                                                location.getText().toString(),
                                                                                email.getText().toString(),
                                                                                telephone.getText().toString(),
                                                                                finalPosterUrl,
                                                                                deviceId
                                                                        );

                                                                        db.collection("facilities")
                                                                                .document(deviceId)
                                                                                .set(facility)
                                                                                .addOnSuccessListener(documentReference -> {
                                                                                    Toast.makeText(ManageFacility.this, "Facility updated", Toast.LENGTH_SHORT).show();
                                                                                    finish();
                                                                                })
                                                                                .addOnFailureListener(e -> {
                                                                                    Log.w(TAG, "Image upload failed", e);
                                                                                    Toast.makeText(ManageFacility.this, "Failed to upload poster", Toast.LENGTH_SHORT).show();
                                                                                });

                                                                    }
                                                                });

                                                    }
                                                });
                                    }
                                });
                    });
        }
        else {
            db.collection("facilities")
                    .whereEqualTo("deviceId", deviceId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            oldFacility = documentSnapshot.toObject(Facility.class);
                            if (oldFacility != null){
                                finalPosterUrl = oldFacility.getFacilityImageUrl();
                            }
                        }
                        Facility facility = new Facility(
                                facilityName.getText().toString(),
                                location.getText().toString(),
                                email.getText().toString(),
                                telephone.getText().toString(),
                                finalPosterUrl,
                                deviceId
                        );
                        FirebaseFirestore.getInstance().collection("facilities").document(deviceId)
                                .set(facility)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText( ManageFacility.this, "Facility updated", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Log.w(TAG, "Poster upload failed", e);
                                    Toast.makeText(ManageFacility.this, "Failed to upload poster", Toast.LENGTH_SHORT).show();
                                });
                    });
        }
    }

    /**
     * Validates the input fields for the facility details.
     * @return True if all fields are valid, otherwise false.
     */
    private boolean areFieldsValid() {
        if (facilityName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Facility name is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (location.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Location is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (email.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()) {
            Toast.makeText(ManageFacility.this, "Enter a valid email address.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (telephone.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Telephone is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Loads the current facility details from Firestore and populates the UI fields with these details.
     */
    private void loadFacilityDetails(){
        FirebaseFirestore.getInstance()
                .collection("facilities")
                .whereEqualTo("deviceId", deviceId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        Facility facility = documentSnapshot.toObject(Facility.class);
                        if (facility != null) {
                            // Populate the UI with event details
                            facilityName.setText(facility.getFacilityName());
                            location.setText(facility.getLocation());
                            email.setText(facility.getEmail());
                            telephone.setText(facility.getTelephone());

                            // Load event poster image using Glide
                            if (facility.getFacilityImageUrl() != null && !facility.getFacilityImageUrl().isEmpty()) {
                                Glide.with(getApplicationContext())
                                        .load(facility.getFacilityImageUrl())
                                        .placeholder(R.drawable.pfp_placeholder_image)
                                        .error(R.drawable.camera_image)
                                        .into(poster);
                            } else {
                                Log.w(TAG, "No poster URL found in the document");
                                poster.setImageResource(R.drawable.camera_image);
                            }
                        }
                    }
                    else {
                        Log.e(TAG, "No such document exists");
                    }
                });
    }

    /**
     * Displays a dialog allowing the user to choose between taking a photo with the camera or
     * selecting an image from the gallery.
     */
    private void showImagePickerDialog() {
        String[] options = {"Use Camera", "Choose from Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Poster");
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

    /**
     * Checks and requests permissions for using the camera and accessing external storage.
     * @return True if permissions are already granted, otherwise false.
     */
    private boolean checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    /**
     * Handles the result of permission requests for accessing camera and storage.
     * @param requestCode The request code passed when requesting permission.
     * @param permissions The requested permissions.
     * @param grantResults The grant results for the requested permissions.
     */
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

    /**
     * Opens the camera to allow the user to take a photo.
     */
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * Opens the gallery to allow the user to pick an image.
     */
    private void openGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
    }

    /**
     * Handles the result from an activity, such as selecting an image or taking a photo.
     * @param requestCode The request code for identifying which activity returned the result.
     * @param resultCode The result code indicating the success or failure of the operation.
     * @param data The intent data containing the result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                if (bitmap != null) {
                    poster.setImageBitmap(bitmap); // Display the image in ImageView
                    imageData = bitmapToByteArray(bitmap);
                    imageHash = hashImage(imageData);// Convert to byte array if needed
                }
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                Uri selectedImage = data.getData();
                try {
                    // Decode and scale the selected image to fit within the ImageView
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    poster.setImageBitmap(bitmap);
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
     * Converts a Bitmap image to a byte array.
     * @param bitmap The Bitmap image to be converted.
     * @return A byte array representing the bitmap.
     */
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * Generates a SHA-256 hash of the given image data.
     * @param imageData The image data to hash.
     * @return The SHA-256 hash of the image data as a hexadecimal string.
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