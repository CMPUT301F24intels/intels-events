package com.example.intels_app;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class AddEvent extends AppCompatActivity {
    private StorageReference storageReference;
    private ImageView cameraImage;
    private byte[] imageData;
    private String imageHash;
    private ImageButton backButton;
    private Button addPosterButton;
    private Button addEvent;
    private EditText limitEntrantsButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);

        cameraImage = findViewById(R.id.camera_image);

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(AddEvent.this, ManageEventsActivity.class);
            startActivity(intent);
        });

        addPosterButton = findViewById(R.id.edit_poster_button);
        addPosterButton.setOnClickListener(view -> openGallery());

        limitEntrantsButton = findViewById(R.id.limit_entrants);
        addEvent = findViewById(R.id.add_event_button);

        addEvent.setOnClickListener(view -> {
            if (validateInputs()) {
                EditText maxAttendees = findViewById(R.id.max_attendees_number);
                EditText eventName = findViewById(R.id.eventNameEditText);
                EditText location = findViewById(R.id.locationEditText);
                EditText dateTime = findViewById(R.id.dateTimeEditText);
                EditText description = findViewById(R.id.descriptionEditText);
                SwitchCompat geolocationRequirement = findViewById(R.id.geolocationRequirementTextView);
                String limitEntrantsText = limitEntrantsButton.getText().toString();

                FirebaseInstallations.getInstance().getId().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String deviceId = task.getResult();
                        storageReference = FirebaseStorage.getInstance().getReference().child("posters").child(imageHash);
                        storageReference.putBytes(imageData).addOnSuccessListener(taskSnapshot ->
                                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                    String posterUrl = uri.toString();

                                    FirebaseFirestore.getInstance().collection("facilities").document(deviceId).get()
                                            .addOnSuccessListener(documentSnapshot -> {
                                                String facilityName = documentSnapshot.getString("facilityName");

                                                Map<String, Object> eventData = new HashMap<>();
                                                eventData.put("eventName", eventName.getText().toString());
                                                eventData.put("facilityName", facilityName);
                                                eventData.put("location", location.getText().toString());
                                                eventData.put("dateTime", dateTime.getText().toString());
                                                eventData.put("description", description.getText().toString());
                                                eventData.put("maxAttendees", Integer.parseInt(maxAttendees.getText().toString()));
                                                eventData.put("geolocationRequired", geolocationRequirement.isChecked());
                                                eventData.put("posterUrl", posterUrl);
                                                eventData.put("deviceId", deviceId);

                                                if (!limitEntrantsText.isEmpty()) {
                                                    eventData.put("limitEntrants", Integer.parseInt(limitEntrantsText));
                                                }

                                                FirebaseFirestore.getInstance().collection("events").document(eventName.getText().toString())
                                                        .set(eventData)
                                                        .addOnSuccessListener(aVoid -> {
                                                            Intent intent = new Intent(AddEvent.this, CreateQR.class);
                                                            intent.putExtra("Event Name", eventName.getText().toString());
                                                            startActivity(intent);
                                                        })
                                                        .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
                                            });
                                })
                        );
                    } else {
                        Log.e("FirebaseInstallations", "Unable to get device ID", task.getException());
                    }
                });
            }
        });
    }

    private boolean validateInputs() {
        EditText maxAttendees = findViewById(R.id.max_attendees_number);
        EditText eventName = findViewById(R.id.eventNameEditText);
        EditText location = findViewById(R.id.locationEditText);
        EditText dateTime = findViewById(R.id.dateTimeEditText);
        EditText description = findViewById(R.id.descriptionEditText);

        if (eventName.getText().toString().isEmpty()) {
            eventName.setError("Event name is required");
            eventName.requestFocus();
            return false;
        }

        if (maxAttendees.getText().toString().isEmpty()) {
            maxAttendees.setError("Max attendees is required");
            maxAttendees.requestFocus();
            return false;
        }

        if (location.getText().toString().isEmpty()) {
            location.setError("Location is required");
            location.requestFocus();
            return false;
        }

        if (dateTime.getText().toString().isEmpty()) {
            dateTime.setError("Date and time are required");
            dateTime.requestFocus();
            return false;
        }

        if (description.getText().toString().isEmpty()) {
            description.setError("Description is required");
            description.requestFocus();
            return false;
        }

        if (imageData == null || imageHash == null) {
            Toast.makeText(this, "Please upload a poster image", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void openGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 2 && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                cameraImage.setImageBitmap(bitmap);
                imageData = bitmapToByteArray(bitmap);
                imageHash = hashImage(imageData);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
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
