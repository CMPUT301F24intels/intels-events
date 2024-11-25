package com.example.intels_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ImageBrowserActivity extends AppCompatActivity {

    private GridView gridView;
    private ArrayList<DataClass> imageUrls;
    private FirebaseStorage firebaseStorage;
    private ProfileIBadapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_browser);

        gridView = findViewById(R.id.grid_view);
        imageUrls = new ArrayList<>();
        firebaseStorage = FirebaseStorage.getInstance();

        imageAdapter = new ProfileIBadapter(imageUrls, this);
        gridView.setAdapter(imageAdapter);

        loadImagesFromFirebase();

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        gridView.setOnItemClickListener((parent, view, position, id) ->
                Toast.makeText(ImageBrowserActivity.this, "Clicked Image: " + position, Toast.LENGTH_SHORT).show());

        gridView.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteConfirmationDialog(position);
            return true;
        });
    }

    private void loadImagesFromFirebase() {
        StorageReference storageReference = firebaseStorage.getReference().child("profile_pics/");

        storageReference.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference fileRef : listResult.getItems()) {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    DataClass data = new DataClass(uri.toString());
                    imageUrls.add(data);
                    imageAdapter.notifyDataSetChanged();
                }).addOnFailureListener(e ->
                        Toast.makeText(ImageBrowserActivity.this, "Failed to load image: " + fileRef.getName(), Toast.LENGTH_SHORT).show());
            }
        }).addOnFailureListener(e ->
                Toast.makeText(ImageBrowserActivity.this, "Failed to load images.", Toast.LENGTH_SHORT).show());
    }

    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Image")
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton("Delete", (dialog, which) -> deleteImage(position))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteImage(int position) {
        DataClass imageToDelete = imageUrls.get(position);
        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageToDelete.getImageUrl());

        imageRef.delete().addOnSuccessListener(aVoid -> {
            imageUrls.remove(position);
            imageAdapter.notifyDataSetChanged();
            Toast.makeText(ImageBrowserActivity.this, "Image deleted successfully", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e ->
                Toast.makeText(ImageBrowserActivity.this, "Failed to delete image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}