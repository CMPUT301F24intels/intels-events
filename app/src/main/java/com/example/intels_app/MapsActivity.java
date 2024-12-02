package com.example.intels_app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
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

import java.util.List;
import java.util.Map;
/**
 * This activity displays a map with markers representing the locations of participants who have joined the waitlist for an event.
 * It uses the Google Maps API to display the map and Firebase Firestore to retrieve the waitlist data, which includes location
 * information for each participant. Upon granting location permissions, the user's current location is also shown on the map.
 *
 * The activity retrieves event data from Firestore, displaying markers for participants based on their geolocation data.
 * If no locations are available for the event, the map notifies the user.
 *
 * @author Kanishka Aswani
 */

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int FINE_PERMISSION_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseFirestore db;

    private String eventName;

    /**
     * Called when the activity is first created.
     * Initializes views, Firestore, and checks permissions for location access.
     * @param savedInstanceState The saved state of the application.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Bundle extras  = getIntent().getExtras();

        if(extras!=null){
            eventName = extras.getString("eventName");
        }
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

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

    }

    /**
     * Loads the map fragment asynchronously. Called after permission for accessing location is granted.
     */
    private void loadMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Called when the Google Map is ready to be used. Sets up map settings and fetches waitlisted entrant locations
     * to display as markers on the map.
     * @param googleMap The GoogleMap instance.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Enable zoom controls and gestures
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        // Set minimum and maximum zoom levels
        mMap.setMinZoomPreference(2.0f);
        mMap.setMaxZoomPreference(21.0f);

        // Enable My Location layer if permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            // Query the `waitlisted_entrants` collection
            db.collection("waitlisted_entrants")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                        boolean hasLocations = false;

                        // Iterate through documents in the collection
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String entrantName = document.getId(); // Document ID is the entrant's name
                            List<Map<String, Object>> events = (List<Map<String, Object>>) document.get("events");

                            // Ensure events is not null or empty
                            if (events != null && !events.isEmpty()) {
                                for (Map<String, Object> event : events) {
                                    String currentEventName = (String) event.get("eventName");
                                    Double latitude = (Double) event.get("latitude");
                                    Double longitude = (Double) event.get("longitude");

                                    // Ensure all necessary fields are available
                                    if (currentEventName != null && latitude != null && longitude != null) {
                                        LatLng position = new LatLng(latitude, longitude);

                                        // Add a marker for the entrant's location
                                        mMap.addMarker(new MarkerOptions()
                                                .position(position)
                                                .title(currentEventName)
                                                .snippet("Signed up by: " + entrantName));

                                        // Include the marker in the bounds
                                        boundsBuilder.include(position);
                                        hasLocations = true;

                                        // Debugging log
                                        Log.d("MapsActivity", "Added marker for event: " + currentEventName +
                                                ", Entrant: " + entrantName +
                                                ", Location: " + latitude + ", " + longitude);
                                    } else {
                                        Log.w("MapsActivity", "Missing latitude/longitude/eventName for event in entrant: " + entrantName);
                                    }
                                }
                            } else {
                                Log.w("MapsActivity", "No events found for entrant: " + entrantName);
                            }
                        }

                        // Adjust camera to show all markers or show a message if no locations are available
                        if (hasLocations) {
                            LatLngBounds bounds = boundsBuilder.build();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100)); // Adjust padding
                        } else {
                            Toast.makeText(this, "No locations available for this event.", Toast.LENGTH_SHORT).show();
                            Log.d("MapsActivity", "No locations found for any entrants.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error fetching locations", Toast.LENGTH_SHORT).show();
                        Log.e("MapsActivity", "Firestore error", e);
                    });

        } else {
            Toast.makeText(this, "Permission denied, unable to show map. Please change permissions in settings", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Fetches waitlist entries and marks them on the map if coordinates are available.
     * This is used to display additional waitlisted events based on different collection.
     */
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


    /**
     * Handles the result of permission requests for accessing location.
     * @param requestCode The request code used to request the permission.
     * @param permissions The requested permissions.
     * @param grantResults The results of the permission requests.
     */
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
