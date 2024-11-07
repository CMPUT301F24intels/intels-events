/**
 * Creates a QR code for the event and displays it
 * @author Janan Panchal
 * @see com.example.intels_app.ManageEventsActivity Back button leads to page displaying organizer Events
 * @see com.example.intels_app.EventDetailsOrganizer Event details page for event just created
 */
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

import com.example.intels_app.EventDetailsOrganizer;
import com.example.intels_app.ManageEventsActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class CreateQR extends AppCompatActivity {

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
            Bitmap bitmap = barcodeEncoder.encodeBitmap(eventName, BarcodeFormat.QR_CODE, 200, 200);
            qrCodeImageView.setImageBitmap(bitmap); // Create a bitmap for the QR code and display it

        } catch (WriterException e) {
            e.printStackTrace();
        }

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
}