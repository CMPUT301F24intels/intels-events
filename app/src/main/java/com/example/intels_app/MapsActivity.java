package com.example.intels_app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int FINE_PERMISSION_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Check permissions and initialize the map
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
        } else {
            loadMap();
        }
    }

    private void loadMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Enable My Location layer if permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            db.collection("waitlist")
                            .document("kat")
                    .collection("entrants")
                    .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {

                                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                                    String eventName = document.getId(); // The document ID is the event name
                                    Double latitude = document.getDouble("latitude");
                                    Double longitude = document.getDouble("longitude");


                                    LatLng position = new LatLng(latitude,longitude);


                                    // Add marker with event name and location
                                    mMap.addMarker(new MarkerOptions()
                                            .position(position)
                                            .title(eventName)
                                            .snippet("Entrant")); // Display location in the marker's snippet

                                    boundsBuilder.include(position);


                                }
                                LatLngBounds bounds = boundsBuilder.build();
                                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
//                                Toast.makeText(MapsActivity.this, "Location:"+latitude+","+longitude, Toast.LENGTH_SHORT).show();

                            }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Error fetching locations", Toast.LENGTH_SHORT).show();
                        Log.e("MapsActivity", "Firestore error", e);
                    });





        } else{
            Toast.makeText(this, "Permission denied, unable to show map. Please change permissions in settings", Toast.LENGTH_SHORT).show();
        }

        // Fetch and display locations from Firestore
        fetchWaitlistEntries();
    }

    private void fetchWaitlistEntries() {
        db.collection("waitlist")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                    boolean hasLocations = false;

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String eventName = document.getId(); // The document ID is the event name
                        String location = document.getString("location"); // Fetch the location field

                        GeoPoint geoPoint = document.getGeoPoint("coordinates"); // Use GeoPoint for precise coordinates
                        if (geoPoint != null) {
                            LatLng position = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());

                            // Add marker with event name and location
                            mMap.addMarker(new MarkerOptions()
                                    .position(position)
                                    .title(eventName)
                                    .snippet(location)); // Display location in the marker's snippet

                            boundsBuilder.include(position);
                            hasLocations = true;
                        }
                    }

                    // Adjust camera to show all markers
                    if (hasLocations) {
                        LatLngBounds bounds = boundsBuilder.build();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100)); // Adjust padding
                    } else {
                        Toast.makeText(this, "No locations available", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching locations", Toast.LENGTH_SHORT).show();
                    Log.e("MapsActivity", "Firestore error", e);
                });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadMap();
            } else {
                Toast.makeText(this, "Permission denied, unable to show map", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
