package com.example.intels_app;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

public class QRCodeScanner {

    private Activity activity;

    public QRCodeScanner(Activity activity) {
        this.activity = activity;
    }

    public void startScan() {
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setCaptureActivity(CaptureActivity.class); // Use default CaptureActivity
        integrator.setPrompt("Scan a QR Code");
        integrator.setOrientationLocked(false); // Lock orientation to current mode
        integrator.setBeepEnabled(true); // Enable beep sound
        integrator.initiateScan();
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                String qrData = result.getContents();
                fetchEventDetails(qrData);
            }
        }
    }

    private void fetchEventDetails(String qrData) {
        // Implement fetching event details from Firebase or other sources
    }
}