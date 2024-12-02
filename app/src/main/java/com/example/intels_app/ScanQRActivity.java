package com.example.intels_app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
/**
 * This class allows for users to scan a QR code and navigate
 * based on the scan result. It uses QRCodeScanner class to initiate
 * the scan and handle the results.
 * @author Het Patel
 * @see com.example.intels_app.JoinWaitlistActivity Joining waitlist for event
 * @see com.example.intels_app.QRCodeScanner Scans QR code
 * @see com.example.intels_app.MainActivity Main screen of app
 */
public class ScanQRActivity extends AppCompatActivity {

    private QRCodeScanner qrCodeScanner;

    /**
     * Called when the activity is first created. Sets up the layout and initializes the QR code scanner.
     * @param savedInstanceState A Bundle containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_qr_page);

        qrCodeScanner = new QRCodeScanner(this);

        // Sets up the qr code button to start the QR code scanner
        ImageButton scanButton = findViewById(R.id.qrcodescanbutton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrCodeScanner.startScan();
            }

            /* Uncomment when implementing
            // TODO: Qr Code scanning should obtain data (in particular: String "Event Name" + eventName).
            // TODO: This was the data encoded in the CreateQR class.
            // TODO: Store this data obtained from scan in the variable data
            // The following will extract the event name from the data and pass it to the EventDetailsOrganizer
            //      class to show the event details screen
            if (data.startsWith("Event Name")) {
                String eventName = data.substring("Event Name".length()); // Extract event name from data
                Intent intent = new Intent(ScanQRActivity.this, EventDetailsOrganizer.class);
                intent.putExtra("Event Name", eventName);
                startActivity(intent);
            } else {
                Toast.makeText(ScanQRActivity.this, "Invalid QR Code", Toast.LENGTH_LONG).show();
            }*/

        });

        // Sets up the back button to return to MainActivity
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScanQRActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Handles the result of the QR code scan.
     * @param requestCode The request code originally supplied to startActivityForResult().
     * @param resultCode  The result code returned by the child activity.
     * @param data        The intent returned by the child activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        qrCodeScanner.handleActivityResult(requestCode, resultCode, data);
    }
}