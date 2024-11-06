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

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

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