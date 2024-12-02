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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
/**
 * This activity allows users to browse images stored in Firebase Storage. It displays the images in a
 * grid view and supports features such as viewing and deleting images. When an image is deleted,
 * it is removed from both Firebase Storage and the associated Firestore document.
 *
 * @author Kaniskha Aswani
 */

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
            // Remove the image from the local list and update the adapter
            imageUrls.remove(position);
            imageAdapter.notifyDataSetChanged();

            // Update Firestore document
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("profiles")
                    .whereEqualTo("imageUrl", imageToDelete.getImageUrl())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().update("imageUrl", null)
                                    .addOnSuccessListener(aVoid1 -> {
                                        Toast.makeText(ImageBrowserActivity.this, "Image deleted and Firestore updated successfully", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(ImageBrowserActivity.this, "Failed to update Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ImageBrowserActivity.this, "Failed to find document in Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }).addOnFailureListener(e -> {
            Toast.makeText(ImageBrowserActivity.this, "Failed to delete image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}