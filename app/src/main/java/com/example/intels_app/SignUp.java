package com.example.intels_app;

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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SignUp extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private boolean isCameraOption = false;

    ImageButton back_button;
    EditText name, username, phone_number;
    Button add_picture, register_button;
    ImageView profile_pic;

    private StorageReference storage;
    private FirebaseFirestore db;
    private String Imagehash;
    private Uri imageUri;
    private byte[] imageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();

        add_picture = findViewById(R.id.add_picture);
        add_picture.setOnClickListener(view -> showImagePickerDialog());
        profile_pic = findViewById(R.id.camera_image);
        name = findViewById(R.id.enter_name);
        username = findViewById(R.id.enter_username);
        phone_number = findViewById(R.id.enter_phone_number);
        register_button = findViewById(R.id.register_button);

        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, SelectRoleActivity.class);
                startActivity(intent);
            }
        });
        register_button = findViewById(R.id.register_button);
    }

    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery", "Generate with Initials"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Profile Picture");
        builder.setItems(options, (dialog, which) -> {
            isCameraOption = (which == 0);
            if (which == 2) {
                Bitmap generatedImage = generateProfilePicture("Dhanshri");
                profile_pic.setImageBitmap(generatedImage);
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
                // Handle the camera image
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                profile_pic.setImageBitmap(photo);  // Set the photo to the ImageView
                imageData = bitmapToBytes(photo);   // Convert the image to bytes for upload
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                // Handle the gallery image
                imageUri = data.getData();
                profile_pic.setImageURI(imageUri);  // Set the selected image to the ImageView
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    imageData = bitmapToBytes(bitmap);  // Convert the image to bytes for upload
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    private Bitmap generateProfilePicture(String name) {
        String initials = name.length() >= 2 ? name.substring(0, 2).toUpperCase() : name.substring(0, 1).toUpperCase();

        int size = 200;
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(this, R.color.custom_blue));
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
