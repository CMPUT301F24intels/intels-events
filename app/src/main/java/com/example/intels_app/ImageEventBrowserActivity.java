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
 * This activity allows users to browse event posters stored in Firebase Storage. It displays the
 * posters in a grid view and supports features such as viewing and deleting event posters. When a
 * poster is deleted, it is removed from Firebase Storage.
 *
 * @author Kanishka Aswani
 */

public class ImageEventBrowserActivity extends AppCompatActivity {

    private GridView gridView;
    private ArrayList<EventDataClass> imageUrls;
    private FirebaseStorage firebaseStorage;
    private EventIBadapter imageAdapter;

    /**
     * Called when the activity is first created.
     * Initializes UI components and sets up Firebase for loading event posters.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied.
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
                Toast.makeText(ImageEventBrowserActivity.this, "Clicked Poster: " + position, Toast.LENGTH_SHORT).show());

        gridView.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteConfirmationDialog(position);
            return true;
        });
    }

    /**
     * Loads event posters from Firebase Storage and adds them to the GridView.
     */
    private void loadEventPostersFromFirebase() {
        StorageReference storageReference = firebaseStorage.getReference().child("posters/");

        storageReference.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference fileRef : listResult.getItems()) {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    EventDataClass eventData = new EventDataClass(uri.toString());
                    imageUrls.add(eventData);
                    imageAdapter.notifyDataSetChanged();
                }).addOnFailureListener(e ->
                        Toast.makeText(ImageEventBrowserActivity.this, "Failed to load poster: " + fileRef.getName(), Toast.LENGTH_SHORT).show());
            }
        }).addOnFailureListener(e ->
                Toast.makeText(ImageEventBrowserActivity.this, "Failed to load posters.", Toast.LENGTH_SHORT).show());
    }

    /**
     * Shows a confirmation dialog to confirm if the user wants to delete the selected event poster.
     * @param position The position of the poster in the GridView that the user wants to delete.
     */
    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Poster")
                .setMessage("Are you sure you want to delete this poster?")
                .setPositiveButton("Delete", (dialog, which) -> deleteEventPoster(position))
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Deletes the event poster from Firebase Storage and removes it from the GridView.
     * @param position The position of the poster in the GridView that the user wants to delete.
     */
    private void deleteEventPoster(int position) {
        EventDataClass posterToDelete = imageUrls.get(position);
        StorageReference posterRef = FirebaseStorage.getInstance().getReferenceFromUrl(posterToDelete.getImageUrl());

        posterRef.delete().addOnSuccessListener(aVoid -> {
            imageUrls.remove(position);
            imageAdapter.notifyDataSetChanged();
            Toast.makeText(ImageEventBrowserActivity.this, "Poster deleted successfully", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e ->
                Toast.makeText(ImageEventBrowserActivity.this, "Failed to delete poster: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}