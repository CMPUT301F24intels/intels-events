/**
 * This class allows users to edit their profile information, including name,
 * email, phone number, and profile picture. The activity provides options
 * for capturing or selecting a profile picture, input validation based on set
 * parameters, and saving profile changes.
 * @author Dhanshri Patel
 * @see com.example.intels_app.MainActivity Main screen of app
 */

package com.example.intels_app;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class EditProfileActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private FirebaseFirestore db;

    private ImageButton back_button;
    private Button edit_pfp_button, save_changes_button;
    private ImageView profile_pic;
    private boolean isCameraOption = false;
    private EditText name, email, phone_number;
    private String finalImageUrl;
    private String oldImageUrl;
    private Profile oldProfile;
    private Profile profile;
    private String deviceId;
    private String imageHash;
    private byte[] imageData;
    private boolean imageUploaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_page);
        db = FirebaseFirestore.getInstance();

        FirebaseInstallations.getInstance().getId()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        deviceId = task.getResult();
                        Log.d("Device ID", "Device ID in EditProfile: " + deviceId);
                        loadProfileDetails();
                        save_changes_button.setOnClickListener(view -> saveProfileChanges());
                    } else {
                        Log.e("Device ID Error", "Unable to get Device ID in EditProfile", task.getException());
                        Toast.makeText(this, "Error retrieving Device ID", Toast.LENGTH_SHORT).show();
                    }
                });



        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(view -> {
            Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
            startActivity(intent);
        });

        name = findViewById(R.id.enter_name);
        email = findViewById(R.id.enter_email);
        phone_number = findViewById(R.id.enter_phone_number);
        profile_pic = findViewById(R.id.camera_image);
        edit_pfp_button = findViewById(R.id.edit_button);
        save_changes_button = findViewById(R.id.save_changes_button);

        edit_pfp_button.setOnClickListener(view -> {
                showImagePickerDialog();
                imageUploaded = true;
        });
    }

    private void loadProfileDetails(){
        db.collection("profiles")
                .whereEqualTo("deviceId", deviceId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                Profile profile = documentSnapshot.toObject(Profile.class);
                if (profile != null) {
                    // Populate the UI with event details
                    name.setText(profile.getName());
                    email.setText(profile.getEmail());
                    phone_number.setText(String.valueOf(profile.getPhone_number()));

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
        }).addOnFailureListener(e ->
                        Log.w(TAG, "Error getting document", e));
    }

    private void saveProfileChanges() {
        if (imageUploaded) {
            db.collection("profiles")
                    .whereEqualTo("deviceId", deviceId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            oldProfile = documentSnapshot.toObject(Profile.class);
                            if (oldProfile != null) {
                                oldImageUrl = oldProfile.getImageUrl();
                            }
                        }
                        FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "Old profile picture deleted successfully");

                                        FirebaseStorage.getInstance().getReference().child("profile_pics")
                                                .child(imageHash)
                                                .putBytes(imageData)
                                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        Log.d(TAG, "New profile picture uploaded successfully");

                                                        FirebaseStorage.getInstance().getReference().child("profile_pics").child(imageHash).getDownloadUrl()
                                                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                    @Override
                                                                    public void onSuccess(Uri uri) {
                                                                        finalImageUrl = uri.toString();
                                                                        Log.d(TAG, "New Profile URL: " + finalImageUrl);

                                                                        profile = new Profile(
                                                                                deviceId,
                                                                                name.getText().toString(),
                                                                                email.getText().toString(),
                                                                                Integer.parseInt(phone_number.getText().toString()),
                                                                                finalImageUrl
                                                                        );

                                                                        db.collection("profiles")
                                                                                .document(name.getText().toString())
                                                                                .set(profile)
                                                                                .addOnSuccessListener(documentReference -> {
                                                                                    Toast.makeText(EditProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                                                                                    finish();
                                                                                })
                                                                                .addOnFailureListener(e -> {
                                                                                    Log.w(TAG, "Image upload failed", e);
                                                                                    Toast.makeText(EditProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
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
            db.collection("profiles")
                    .whereEqualTo("deviceId", deviceId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            oldProfile = documentSnapshot.toObject(Profile.class);
                            if (oldProfile != null){
                                finalImageUrl = oldProfile.getImageUrl();
                            }
                        }
                        profile = new Profile(
                                deviceId,
                                name.getText().toString(),
                                email.getText().toString(),
                                Integer.parseInt(phone_number.getText().toString()),
                                finalImageUrl
                        );
                        FirebaseFirestore.getInstance().collection("profiles").document(name.getText().toString())
                                .set(profile)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(EditProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Log.w(TAG, "Image upload failed", e);
                                    Toast.makeText(EditProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                                });
                    });
        }
    }

    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery", "Remove Profile Picture"};
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
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                if (bitmap != null) {
                    profile_pic.setImageBitmap(bitmap); // Display the image in ImageView
                    imageData = bitmapToByteArray(bitmap);
                    imageHash = hashImage(imageData);// Convert to byte array if needed
                }
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                Uri selectedImage = data.getData();
                try {
                    // Decode and scale the selected image to fit within the ImageView
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    profile_pic.setImageBitmap(bitmap);
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

