package com.example.intels_app;

import android.content.Context;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Random;

public class PopupMenuHelper {

    public static void showPopupMenu(Context context, View view) {
        PopupMenu popup = new PopupMenu(context, view);
        popup.inflate(R.menu.popup_menu);

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.remove_facility) {
                deleteFacility(context);
                return true;
            }
            return false;
        });

        popup.show();
    }

    private static void deleteFacility(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference facilitiesRef = db.collection("facilities");

        facilitiesRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> facilities = queryDocumentSnapshots.getDocuments();
                        int randomIndex = new Random().nextInt(facilities.size());
                        DocumentSnapshot randomFacility = facilities.get(randomIndex);

                        String facilityName = randomFacility.getString("facilityName");

                        randomFacility.getReference().delete()
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(context, "Removed facility: " + facilityName, Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(context, "Error removing facility: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(context, "No facilities to remove", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Error fetching facilities: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
