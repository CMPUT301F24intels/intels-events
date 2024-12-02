package com.example.intels_app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.intels_app.EventDataClass;
import com.example.intels_app.EventIBadapter;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
/**
 * This activity allows users to browse facility images stored in Firebase Storage. It displays the
 * images in a grid view and supports features such as viewing and deleting facility images. When an
 * image is deleted, it is removed from Firebase Storage.
 *
 * @author Kanishka Aswani
 */

public class ImageFacilityBrowserActivity extends AppCompatActivity {

    private GridView gridView;
    private ArrayList<EventDataClass> imageUrls;
    private FirebaseStorage firebaseStorage;
    private EventIBadapter imageAdapter;

    /**
     * Called when the activity is first created.
     * Initializes UI components and sets up Firebase for loading facility images.
     * @param savedInstanceState Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_event_browser);

        gridView = findViewById(R.id.grid_view);
        imageUrls = new ArrayList<>();
        firebaseStorage = FirebaseStorage.getInstance();

        imageAdapter = new EventIBadapter(imageUrls, this);
        gridView.setAdapter(imageAdapter);

        loadEventPostersFromFirebase();

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        gridView.setOnItemClickListener((parent, view, position, id) ->
        Toast.makeText(ImageFacilityBrowserActivity.this, "Clicked Poster: " + position, Toast.LENGTH_SHORT).show());

        gridView.setOnItemLongClickListener((parent, view, position, id) -> {
        showDeleteConfirmationDialog(position);
        return true;
    });
    }

    /**
     * Loads facility images from Firebase Storage and adds them to the GridView.
     */
    private void loadEventPostersFromFirebase() {
        StorageReference storageReference = firebaseStorage.getReference().child("facilities/");

        storageReference.listAll().addOnSuccessListener(listResult -> {
        for (StorageReference fileRef : listResult.getItems()) {
        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
        EventDataClass eventData = new EventDataClass(uri.toString());
        imageUrls.add(eventData);
        imageAdapter.notifyDataSetChanged();
    }).addOnFailureListener(e ->
        Toast.makeText(ImageFacilityBrowserActivity.this, "Failed to load poster: " + fileRef.getName(), Toast.LENGTH_SHORT).show());
    }
    }).addOnFailureListener(e ->
        Toast.makeText(ImageFacilityBrowserActivity.this, "Failed to load posters.", Toast.LENGTH_SHORT).show());
    }

    /**
     * Shows a confirmation dialog to confirm if the user wants to delete the selected facility image.
     * @param position The position of the facility image in the GridView that the user wants to delete.
     */
    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Poster")
            .setMessage("Are you sure you want to delete this poster?")
            .setPositiveButton("Delete", (dialog, which) -> deleteFacilitiesImages(position))
        .setNegativeButton("Cancel", null)
        .show();
    }

    /**
     * Deletes the facility image from Firebase Storage and removes it from the GridView.
     * @param position The position of the facility image in the GridView that the user wants to delete.
     */
    private void deleteFacilitiesImages(int position) {
        EventDataClass posterToDelete = imageUrls.get(position);
        StorageReference posterRef = FirebaseStorage.getInstance().getReferenceFromUrl(posterToDelete.getImageUrl());

        posterRef.delete().addOnSuccessListener(aVoid -> {
        imageUrls.remove(position);
        imageAdapter.notifyDataSetChanged();
        Toast.makeText(ImageFacilityBrowserActivity.this, "Poster deleted successfully", Toast.LENGTH_SHORT).show();
    }).addOnFailureListener(e ->
        Toast.makeText(ImageFacilityBrowserActivity.this, "Failed to delete poster: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}