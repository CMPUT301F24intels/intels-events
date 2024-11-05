package com.example.intels_app;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class QRCodeScanner {

    private final Activity activity;

    public QRCodeScanner(Activity activity) {
        this.activity = activity;
    }

    public void startScan() {
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a QR Code");
        integrator.setCameraId(0);  // Use a specific camera of the device
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
                try {
                    // Parse JSON data from QR code
                    JSONObject eventDetails = new JSONObject(result.getContents());

                    // Extract event details
                    String eventName = eventDetails.getString("name");
                    String facility = eventDetails.getString("facility");
                    String location = eventDetails.getString("location");
                    String dateTime = eventDetails.getString("dateTime");
                    String description = eventDetails.getString("description");
                    int maxAttendees = eventDetails.getInt("maxAttendees");

                    // Create an intent to start JoinWaitlistActivity
                    Intent intent = new Intent(activity, JoinWaitlistActivity.class);
                    intent.putExtra("Event Name", eventName);
                    intent.putExtra("Facility", facility);
                    intent.putExtra("Location", location);
                    intent.putExtra("DateTime", dateTime);
                    intent.putExtra("Description", description);
                    intent.putExtra("Max Attendees", maxAttendees);

                    // Start JoinWaitlistActivity with the intent
                    activity.startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, "Failed to parse QR code", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
