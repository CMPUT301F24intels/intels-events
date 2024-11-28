package com.example.intels_app;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * SignUp activity allows users to register for an event by entering their details and uploading a profile picture.
 * User information and the profile picture are stored in Firebase Firestore and Firebase Storage.
 * The activity includes functionality for image capture and selection, data validation, and navigation.
 *
 * @author Dhanshri Patel, Janan Panchal, Aayushi Shah
 * @see FirebaseFirestore Firebase
 * @see FirebaseStorage Storage
 */

public class SignUp extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private boolean isCameraOption = false;
    private FirebaseFirestore db;
    private CollectionReference profilesRef;
    private StorageReference storageReference;
    private CollectionReference waitlistRef;

    private ImageButton back_button;
    private EditText name, email, phone_number;
    private Button add_picture, register_button;
    private ImageView profile_pic;
    private SwitchCompat notificationSwitch;
    private boolean notificationPreference = false;

    private String deviceId;
    private String eventName;
    private String Imagehash;
    private Uri imageUri;
    private String imageHash;
    private byte[] imageData;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        // Initialize Firestore and Storage
        db = FirebaseFirestore.getInstance();
        profilesRef = db.collection("profiles");
        storageReference = FirebaseStorage.getInstance().getReference().child("profile_pics");

        // Retrieve intent extras
        deviceId = getIntent().getStringExtra("deviceId");
        eventName = getIntent().getStringExtra("eventName");
        Log.d("SignUpActivity", "Received Device ID: " + deviceId);
        Log.d("SignUpActivity", "Received Event Name: " + eventName);

        // Initialize UI components
        profile_pic = findViewById(R.id.camera_image);
        name = findViewById(R.id.enter_name);
        email = findViewById(R.id.enter_email);
        phone_number = findViewById(R.id.enter_phone_number);
        add_picture = findViewById(R.id.add_picture);
        register_button = findViewById(R.id.register_button);
        back_button = findViewById(R.id.back_button);
        notificationSwitch = findViewById(R.id.notification_switch);

        // Set up listeners
        add_picture.setOnClickListener(view -> showImagePickerDialog());

        back_button.setOnClickListener(view -> {
            Intent intent = new Intent(SignUp.this, SelectRoleActivity.class);
            startActivity(intent);
        });

        notificationPreference = notificationSwitch.isChecked();
        Log.d("SignUpActivity", "Initial Notification Preference: " + notificationPreference);

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            notificationPreference = isChecked;
            Log.d("SignUpActivity", "Notification Preference: " + notificationPreference);
        });

        register_button.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        String userName = name.getText().toString();
        String userEmail = email.getText().toString();
        String userPhoneNumber = phone_number.getText().toString();

        // Validate user input
        if (userName.isEmpty() || userEmail.isEmpty()) {
            Toast.makeText(SignUp.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageData == null || imageHash == null) {
            Toast.makeText(SignUp.this, "Please select or generate a profile picture", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            Toast.makeText(SignUp.this, "Enter a valid email address.", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference profilePicRef = storageReference.child(imageHash);
        profilePicRef.putBytes(imageData)
                .addOnSuccessListener(taskSnapshot -> profilePicRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String profilePicUrl = uri.toString();

                            Profile newProfile = new Profile(deviceId, userName, userEmail, userPhoneNumber, profilePicUrl, notificationPreference);
                            newProfile.setNotifPref(notificationPreference);

                            profilesRef.document(deviceId)
                                    .set(newProfile)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Profile successfully created!");
                                        addToWaitlistedEntrants(newProfile, eventName);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("FirestoreError", "Error adding profile", e);
                                        Toast.makeText(SignUp.this, "Failed to save profile. Please try again.", Toast.LENGTH_SHORT).show();
                                    });
                        }))
                .addOnFailureListener(e -> {
                    Log.e("StorageError", "Error uploading profile picture", e);
                    Toast.makeText(SignUp.this, "Error uploading profile picture. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }

    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery", "Generate with Initials"};
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
                case 2: // Generate with Initials
                    name = findViewById(R.id.enter_name);
                    Bitmap generatedImage = generateProfilePicture(name.getText().toString());
                    profile_pic.setImageBitmap(generatedImage);
                    imageData = bitmapToByteArray(generatedImage); // Convert generated image to byte array if needed
                    imageHash = hashImage(imageData);
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
        startActivity(intent);
    }

    private void openGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
    }

    private Bitmap generateProfilePicture(String name) {
        String initials = name.length() >= 2 ? name.substring(0, 2).toUpperCase() : name.substring(0, 1).toUpperCase();

        int size = 200;
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        Random random = new Random();
        int randomcolor = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        paint.setColor(randomcolor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(size / 2, size / 2, size / 2, paint);

        paint.setColor(Color.BLACK);
        paint.setTextSize(64);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);

        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float x = size / 2;
        float y = size / 2 - (fontMetrics.ascent + fontMetrics.descent) / 2;
        canvas.drawText(initials, x, y, paint);

        return bitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null && data.getExtras() != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");  // Get thumbnail from camera
                if (bitmap != null) {
                    profile_pic.setImageBitmap(bitmap); // Display the image in ImageView
                    imageData = bitmapToByteArray(bitmap);// Convert to byte array if needed
                    imageHash = hashImage(imageData);
                }
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                Uri selectedImageUri = data.getData();  // Get URI of selected image
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    profile_pic.setImageBitmap(bitmap); // Display the image in ImageView
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

    private void addToWaitlistedEntrants(Profile profile, String eventName) {
        CollectionReference waitlistRef = db.collection("waitlisted_entrants");

        // Create the event data
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventName", eventName);

        // Create the waitlist entry
        Map<String, Object> waitlistEntry = new HashMap<>();
        waitlistEntry.put("deviceId", profile.getDeviceId());
        waitlistEntry.put("profile", profile); // Add the full profile object, including notifPref
        waitlistEntry.put("events", Collections.singletonList(eventData)); // Add the event as a list

        // Save the waitlist entry to Firestore
        waitlistRef.document(profile.getName())
                .set(waitlistEntry)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Entrant successfully added to waitlisted_entrants!");
                    Toast.makeText(this, "Successfully joined the waitlist!", Toast.LENGTH_SHORT).show();

                    // Navigate back or perform other actions
                    returnCreatedProfile(profile, eventName);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error adding entrant to waitlisted_entrants", e);
                    Toast.makeText(this, "Failed to join waitlist. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }

    private void returnCreatedProfile(Profile profile, String eventName) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("deviceId", profile.getDeviceId());
        resultIntent.putExtra("name", profile.getName());
        resultIntent.putExtra("email", profile.getEmail());
        resultIntent.putExtra("phoneNumber", profile.getPhone_number());
        resultIntent.putExtra("profilePicUrl", profile.getImageUrl());
        resultIntent.putExtra("eventName", eventName);
        setResult(RESULT_OK, resultIntent);
        finish(); // Close SignUp activity
    }

}
