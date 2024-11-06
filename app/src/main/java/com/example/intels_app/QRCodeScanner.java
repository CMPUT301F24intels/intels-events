package com.example.intels_app;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRCodeScanner {

    private final Activity activity;
    private FirebaseFirestore db;

    public QRCodeScanner(Activity activity) {
        this.activity = activity;
        this.db = FirebaseFirestore.getInstance();
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
                // The scanned content is now just the event name
                String eventName = result.getContents();
                fetchEventDetailsFromFirestore(eventName);
            }
        }
    }

    private void fetchEventDetailsFromFirestore(String eventName) {
        db.collection("events").document(eventName).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Create an intent to start JoinWaitlistActivity
                        Intent intent = new Intent(activity, JoinWaitlistActivity.class);

                        // Add all event details to the intent
                        intent.putExtra("eventName", eventName);
                        intent.putExtra("facilityName", documentSnapshot.getString("facilityName"));
                        intent.putExtra("location", documentSnapshot.getString("location"));
                        intent.putExtra("dateTime", documentSnapshot.getString("dateTime"));
                        intent.putExtra("description", documentSnapshot.getString("description"));
                        intent.putExtra("maxAttendees", documentSnapshot.getLong("maxAttendees").intValue());
                        intent.putExtra("geolocationRequirement", documentSnapshot.getBoolean("geolocationRequirement"));
                        intent.putExtra("posterUrl", documentSnapshot.getString("posterUrl"));

                        // Start JoinWaitlistActivity with the intent
                        activity.startActivity(intent);
                    } else {
                        Toast.makeText(activity, "Event not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(activity, "Error fetching event details", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }
}