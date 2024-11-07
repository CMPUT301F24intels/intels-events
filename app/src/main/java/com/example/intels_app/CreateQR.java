package com.example.intels_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.intels_app.EventDetailsOrganizer;
import com.example.intels_app.ManageEventsActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class CreateQR extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_display);

        // Retrieve event name from intent
        String eventName = getIntent().getStringExtra("Event Name");

        ImageView qrCodeImageView = findViewById(R.id.qrCodeImageView);

        try {
            // Use ZXing to generate QR code with only the event name
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(eventName, BarcodeFormat.QR_CODE, 200, 200);
            qrCodeImageView.setImageBitmap(bitmap);

            // Convert the QR code bitmap to byte array and hash it
            byte[] qrImageData = bitmapToByteArray(bitmap);
            String qrHash = hashImage(qrImageData);

            // Store QR hash in Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("events").document(eventName)
                    .update("qrCodeHash", qrHash)
                    .addOnSuccessListener(aVoid -> Log.d("QRHash", "QR hash successfully added to Firestore"))
                    .addOnFailureListener(e -> Log.w("QRHash", "Error updating document", e));

        } catch (WriterException e) {
            e.printStackTrace();
        }

        Button eventDetailsButton = findViewById(R.id.eventDetailsButton);
        eventDetailsButton.setOnClickListener(view -> {
            Intent intent = new Intent(CreateQR.this, EventDetailsOrganizer.class);
            intent.putExtra("Event Name", eventName);
            startActivity(intent);
        });

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(CreateQR.this, ManageEventsActivity.class);
            startActivity(intent);
        });
    }
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
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

