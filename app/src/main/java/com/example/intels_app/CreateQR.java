/**
 * Creates a QR code for the event and displays it
 * @author Janan Panchal
 * @see com.example.intels_app.ManageEventsActivity Back button leads to page displaying organizer Events
 * @see com.example.intels_app.EventDetailsOrganizer Event details page for event just created
 */
package com.example.intels_app;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.intels_app.EventDetailsOrganizer;
import com.example.intels_app.ManageEventsActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CreateQR extends AppCompatActivity {
    private StorageReference storageReference;
    private Uri image;
    private Bitmap bitmap;
    private byte[] imageData;
    private String imageHash;

    /**
     * Creates a QR code for the event
     * @param savedInstanceState Bundle to save the state of the activity
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_display);

        // Retrieve event name from intent for database querying
        String eventName = getIntent().getStringExtra("Event Name");

        // Where to display QR
        ImageView qrCodeImageView = findViewById(R.id.qrCodeImageView);

        try {
            // Use ZXing to generate QR code with only the event name
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.encodeBitmap(eventName, BarcodeFormat.QR_CODE, 200, 200);
            qrCodeImageView.setImageBitmap(bitmap); // Create a bitmap for the QR code and display it

            // Convert Bitmap to byte array for storage
            imageData = bitmapToByteArray(bitmap);

            // Hash the byte array for storage
            imageHash = hashImage(imageData);

        } catch (WriterException e) {
            e.printStackTrace();
        }

        // Upload the poster to storage, named with by the imageHash. Get the download Url created by storage
        // and save it in posterUrl to be added to the new Event object
        storageReference = FirebaseStorage.getInstance().getReference().child("QRCodes").child(imageHash);
        storageReference.putBytes(imageData)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String QRUrl = uri.toString();

                            // Update the QR code URL in FireStore under the events collection
                            FirebaseFirestore.getInstance().collection("events").document(eventName).update("qrCodeUrl", QRUrl);
        }));

        // Button to open the event details page that can show the details about the event
        Button eventDetailsButton = findViewById(R.id.eventDetailsButton);
        eventDetailsButton.setOnClickListener(view -> {
            Intent intent = new Intent(CreateQR.this, EventDetailsOrganizer.class);
            intent.putExtra("Event Name", eventName);
            startActivity(intent);
        });

        // Button to open the manage events page
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(CreateQR.this, ManageEventsActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Converts the Bitmap to a byte array
     * @param bitmap The bitmap of the image
     * @return The byte array of the image
     */
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // Keep original quality
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