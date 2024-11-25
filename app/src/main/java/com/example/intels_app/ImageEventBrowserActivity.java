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

public class ImageEventBrowserActivity extends AppCompatActivity {

    private GridView gridView;
    private ArrayList<EventDataClass> imageUrls;
    private FirebaseStorage firebaseStorage;
    private EventIBadapter imageAdapter;

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

    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Poster")
                .setMessage("Are you sure you want to delete this poster?")
                .setPositiveButton("Delete", (dialog, which) -> deleteEventPoster(position))
                .setNegativeButton("Cancel", null)
                .show();
    }

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