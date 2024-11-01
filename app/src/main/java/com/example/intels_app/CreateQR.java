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
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class CreateQR extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_display);

        ImageView qrCodeImageView = findViewById(R.id.qrCodeImageView);

        // Data to encode in the QR code
        String dataToEncode = "launch_add_event_activity";

        try {
            // Use ZXing to generate QR code
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            BitMatrix bitMatrix = barcodeEncoder.encode(dataToEncode, BarcodeFormat.QR_CODE, 200, 200);
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        Button eventDetailsButton = findViewById(R.id.eventDetailsButton);
        eventDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("event_id", "event_id_value");

                Intent intent = new Intent(CreateQR.this, EventDetails.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(CreateQR.this, ManageEventsActivity.class);
            startActivity(intent);
        });

    }
}
