package com.example.intels_app;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

public class QRCodeScanner {

    private final Activity activity;

    public QRCodeScanner(Activity activity) {
        this.activity = activity;
    }

    public void startScan() {
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a QR Code");
        integrator.setCameraId(0);  // Right now using a specific camera of the device, Change later
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(activity, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                // Need to Handle the scanned qr content here
            }
        }
    }
}