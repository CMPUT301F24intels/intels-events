package com.example.intels_app;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * CancelledProfileAdapter is a custom adapter for managing and displaying a list of profiles
 * that belong to cancelled entrants. It includes a delete button to remove entrants from the list
 * and from Firestore.
 * @author Katrina Alejo
 */

public class CancelledProfileAdapter extends ProfileAdapter {

    /**
     * Constructor
     * @param context Context of the current state of the application
     * @param profileList List of Profile objects to be used by the adapter
     */
    public CancelledProfileAdapter(Context context, List<Profile> profileList) {
        super(context, profileList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.profile_list_view, parent, false);
        }

        Profile profile = filteredProfiles.get(position);

        // Set up views
        TextView nameTextView = convertView.findViewById(R.id.profile_name);
        ImageButton deleteButton = convertView.findViewById(R.id.delete_button);

        // Display the name and other details
        nameTextView.setText(profile.getName() != null ? profile.getName() : "Unknown");

        // Set up delete button functionality
        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this profile?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        String deviceId = profile.getDeviceId(); // Retrieve deviceId

                        if (deviceId == null || deviceId.isEmpty()) {
                            Toast.makeText(context, "Document ID is invalid or empty.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Remove locally
                        filteredProfiles.remove(position);
                        notifyDataSetChanged();

                        // Delete from Firestore
                        FirebaseFirestore.getInstance()
                                .collection("waitlisted_entrants")
                                .document(deviceId) // Use deviceId to reference the Firestore document
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Cancelled entrant deleted: " + profile.getName(), Toast.LENGTH_SHORT).show();
                                    Log.d("CancelledEntrants", "Successfully deleted profile with ID: " + deviceId);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to delete entrant from Firebase", Toast.LENGTH_SHORT).show();
                                    Log.e("CancelledEntrants", "Error deleting profile with ID: " + deviceId, e);
                                });
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // Dismiss the dialog if the user cancels
                        dialog.dismiss();
                    })
                    .show();
        });

        return convertView;
    }
}

