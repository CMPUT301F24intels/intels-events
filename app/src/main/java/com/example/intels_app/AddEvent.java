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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class AddEvent extends AppCompatActivity {
    StorageReference storageReference;
    Uri image;
    ImageView imageView;
    String imageHash;
    byte[] imageData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);

        imageView = findViewById(R.id.pfpPlaceholder);

        // Go back to Manage Events if back button clicked
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddEvent.this, ManageEventsActivity.class);
                startActivity(intent);
            }
        });

        // Select image from gallery if Edit Image Button clicked
        Button addPosterButton = findViewById(R.id.edit_poster_button);
        addPosterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                openGallery.launch(intent);

                ImageView imageView = findViewById(R.id.camera_image);
            }
        });

        // Create a new event with entered details if Add Event button clicked
        Button addEvent = findViewById(R.id.add_event_button);
        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user's event details input values
                EditText maxAttendees = findViewById(R.id.max_attendees_number);
                EditText eventName = findViewById(R.id.eventNameEditText);
                EditText facility = findViewById(R.id.facilityEditText);
                EditText location = findViewById(R.id.locationEditText);
                EditText dateTime = findViewById(R.id.dateTimeEditText);
                EditText description = findViewById(R.id.descriptionEditText);
                SwitchCompat geolocationRequirement = findViewById(R.id.geolocationRequirementTextView);
                SwitchCompat notifPreference = findViewById(R.id.notifPreferenceTextView);

                // Put poster image into storage. Put uri into newEvent parameters
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("posters").child(imageHash);
                storageReference.putBytes(imageData)
                        .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    String posterUrl = uri.toString();

                                    // Create a new event with the entered details
                                    Event newEvent = new Event(
                                            eventName.getText().toString(),
                                            facility.getText().toString(),
                                            location.getText().toString(),
                                            dateTime.getText().toString(),
                                            description.getText().toString(),
                                            Integer.parseInt(maxAttendees.getText().toString()),
                                            geolocationRequirement.isChecked(),
                                            notifPreference.isChecked(),
                                            posterUrl
                                    );

                                    // Create a document with ID of eventName under the events collection
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    DocumentReference docRef = db.collection("events").document(eventName.getText().toString());
                                    docRef.set(newEvent)
                                            .addOnSuccessListener(documentReference -> {
                                                Intent intent = new Intent(AddEvent.this, CreateQR.class);
                                                startActivity(intent);
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error adding document", e);
                                                }
                                            });

                                })
                        ).addOnFailureListener(e -> {
                            Log.w(TAG, "Image upload failed", e);
                            Toast.makeText(AddEvent.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        });

                // Return to Manage Events activity
                Intent intent = new Intent(AddEvent.this, ManageEventsActivity.class);
                intent.putExtra("Event Name", eventName.getText().toString());
                startActivity(intent);
            }
        });
    }

    /**
     * Handles the image selected.
     */
    ActivityResultLauncher<Intent> openGallery = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        //addPosterButton.setEnabled(true);
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
                            Toast.makeText(AddEvent.this, "Error processing image", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(AddEvent.this, "Please select an image", Toast.LENGTH_LONG).show();
                }
            }
        });

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
