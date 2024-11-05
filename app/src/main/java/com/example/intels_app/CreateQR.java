package com.example.intels_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class CreateQR extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_display);

        // Retrieve event details from intent
        String eventName = getIntent().getStringExtra("Event Name");
        String facility = getIntent().getStringExtra("Facility");
        String location = getIntent().getStringExtra("Location");
        String dateTime = getIntent().getStringExtra("DateTime");
        String description = getIntent().getStringExtra("Description");
        int maxAttendees = getIntent().getIntExtra("Max Attendees", 0);

        // Combine all event details into a single JSON string
        String dataToEncode = String.format(
                "{\"name\":\"%s\", \"facility\":\"%s\", \"location\":\"%s\", \"dateTime\":\"%s\", \"description\":\"%s\", \"maxAttendees\":%d}",
                eventName, facility, location, dateTime, description, maxAttendees
        );

        ImageView qrCodeImageView = findViewById(R.id.qrCodeImageView);

        try {
            // Use ZXing to generate QR code
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(dataToEncode, BarcodeFormat.QR_CODE, 200, 200);
            qrCodeImageView.setImageBitmap(bitmap);

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
}