package com.example.intels_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import java.io.ByteArrayOutputStream;
import java.util.Collection;

public class FirestoreData {

    public static FirebaseFirestore getDB() {
        return FirebaseFirestore.getInstance();
    }

    public static CollectionReference getEventReference(FirebaseFirestore db) {
        return db.collection("events");
    }

    public static CollectionReference getProfileReference(FirebaseFirestore db) {
        return db.collection("profiles");

    }

    public static CollectionReference getEntrantEventsReference(String profile) {
        return FirebaseFirestore.getInstance().collection("profiles").document(profile).collection("events");
    }

    public static void deleteEventReference(Context context, FirebaseFirestore db, String documentId, CollectionReference collectionReference) {
        if (NetworkConnectivity.isNetworkAvailable(context)) {
            FirestoreData.getEventReference(db).document(documentId).delete();
        } else {
        Toast.makeText(context, "No network connection. Unable to delete event.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void addEvent(Context context, FirebaseFirestore db, CollectionReference collectionReference, Event event) {
        if (NetworkConnectivity.isNetworkAvailable(context)) {
            FirebaseFirestore.getInstance().collection("events").add(event);
        } else {
            Toast.makeText(context, "No network connection. Unable to delete event.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    @FunctionalInterface
    public interface EventCallback<T> {
        void onSuccess(); // Single abstract method

        // Optionally, add a default method for failure handling if needed
        default void onFailure(Exception e) {
            // Default error handling can go here
        }
    }

    public void addEventWithImages(Event event, Bitmap posterBitmap, Bitmap qrCodeBitmap, EventCallback callback) {
        uploadImage(posterBitmap, "poster_" + event.getEventName() + ".jpg", posterUrl -> {
            event.setPosterUrl(posterUrl);
            uploadImage(qrCodeBitmap, "qrCode_" + event.getEventName() + ".jpg", qrCodeUrl -> {
                event.setQrCodeUrl(qrCodeUrl);
                addEventToFirestore(event, callback);
            }, callback::onFailure);
        }, callback::onFailure);
    }

    private void uploadImage(Bitmap bitmap, String fileName, OnImageUploadListener listener, EventCallback failureCallback) {
        StorageReference storageRef = storage.getReference().child("images/" + fileName);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        storageRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                .addOnSuccessListener(uri -> listener.onSuccess(uri.toString()))
                .addOnFailureListener(e -> failureCallback.onFailure(e.getMessage())))
                .addOnFailureListener(e -> failureCallback.onFailure(e.getMessage()));
    }

    private void addEventToFirestore(Event event, EventCallback callback) {
        firestore.collection("events")
                .add(event)
                .addOnSuccessListener(documentReference -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    private interface OnImageUploadListener {
        void onSuccess(String imageUrl);
    } */
}