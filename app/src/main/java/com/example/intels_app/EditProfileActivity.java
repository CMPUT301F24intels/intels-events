/**
 * This class allows users to edit their profile information, including name,
 * email, phone number, and profile picture. The activity provides options
 * for capturing or selecting a profile picture, input validation based on set
 * parameters, and saving profile changes.
 * @author Dhanshri Patel
 * @see com.example.intels_app.MainPageActivity Main screen of app
 */

package com.example.intels_app;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.SharedPreferences;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EditProfileActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private FirebaseFirestore db;

    ImageButton back_button;
    Button edit_pfp_button, save_changes_button;
    ImageView profile_pic;
    private boolean isCameraOption = false;
    EditText name, email, phone_number;
    Profile profile;
    String deviceId;
    Uri image;
    String imageHash;
    byte[] imageData;
    boolean imageUploaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_page);
        db = FirebaseFirestore.getInstance();

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

        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(view -> {
            Intent intent = new Intent(EditProfileActivity.this, MainPageActivity.class);
            startActivity(intent);
        });

        name = findViewById(R.id.enter_name);
        email = findViewById(R.id.enter_email);
        phone_number = findViewById(R.id.enter_phone_number);
        profile_pic = findViewById(R.id.camera_image);
        edit_pfp_button = findViewById(R.id.edit_button);
        save_changes_button = findViewById(R.id.save_changes_button);

        loadProfileDetails();

        edit_pfp_button.setOnClickListener(view -> showImagePickerDialog());
//        save_changes_button.setOnClickListener(view -> saveProfileChanges());
    }

    private void loadProfileDetails(){
        DocumentReference documentRef = db.collection("profiles").document(deviceId);
        documentRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Profile profile = documentSnapshot.toObject(Profile.class);
                if (profile != null) {
                    // Populate the UI with event details
                    name.setText("Name: " + profile.getName());
                    email.setText("Email: " + profile.getEmail());
                    phone_number.setText("Phone Number: " + profile.getPhone_number());

                    // Load event poster image using Glide
                    if (profile.getImageUrl() != null && !profile.getImageUrl().isEmpty()) {
                        Glide.with(getApplicationContext())
                                .load(profile.getImageUrl())
                                .placeholder(R.drawable.pfp_placeholder_image)
                                .error(R.drawable.person_image)
                                .into(profile_pic);
                    } else {
                        Log.w(TAG, "No poster URL found in the document");
                        profile_pic.setImageResource(R.drawable.person_image);
                    }
                }
            } else {
                Log.e(TAG, "No such document exists");
            }
        }).addOnFailureListener(e -> Log.w(TAG, "Error getting document", e));
    }

    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery", "Remove Profile Picture"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Profile Picture");
        builder.setItems(options, (dialog, which) -> {
            isCameraOption = (which == 0);
            if (which == 2) {
                profile_pic.setImageBitmap(generateProfilePicture(name.getText().toString()));
            } else if (checkAndRequestPermissions()) {
                if (isCameraOption) {
                    openCamera();
                } else {
                    openGallery();
                }
            }
        });
        builder.show();
    }

    private boolean checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                profile_pic.setImageBitmap(photo);
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                Uri selectedImage = data.getData();
                try {
                    // Decode and scale the selected image to fit within the ImageView
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, profile_pic.getWidth(), profile_pic.getHeight(), true);
                    profile_pic.setImageBitmap(scaledBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private Bitmap generateProfilePicture(String email) {
        String initials = email.length() >= 2 ? email.substring(0, 2).toUpperCase() : email.substring(0, 1).toUpperCase();

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


}

