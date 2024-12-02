package com.example.intels_app;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for the profile list view to take the data list and inflate it into the view.
 * @author Janan Panchal, Dhanshri Patel
 * @see com.example.intels_app.Profile Profile object
 * @see com.example.intels_app.AdminProfiles Get the data list from the admin profiles page
 */

public class ProfileAdapterAdmin extends BaseAdapter {
    private Context context;
    private List<Profile> profiles;

    /**
     * Constructor to create an instance of ProfileAdapterAdmin.
     * @param context  The context in which the adapter is used.
     * @param profiles The list of Profile objects to be displayed.
     */
    public ProfileAdapterAdmin(Context context, List<Profile> profiles) {
        this.context = context;
        this.profiles = profiles;
    }

    /**
     * Returns the number of profiles in the list.
     * @return The size of the profiles list.
     */
    public int getCount() {
        return profiles.size();
    }

    /**
     * Returns the Profile object at the specified position in the list.
     * @param i The position of the profile in the list.
     * @return The Profile object at the specified position.
     */
    public Object getItem(int i) {
        return profiles.get(i);
    }

    /**
     * Returns the item ID for the specified position.
     * @param i The position of the profile.
     * @return The position value as the item ID.
     */
    public long getItemId(int i) {
        return i;
    }

    /**
     * Returns the view for a specific item in the list.
     * @param position    The position of the item in the list.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent ViewGroup that this view will be attached to.
     * @return The View corresponding to the data at the specified position.
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("Adapter", "Hi");
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.profile_list_view, parent, false);
        }

        Profile profile = profiles.get(position);

        TextView nameTextView = convertView.findViewById(R.id.profile_name);
        ImageView profileImageView = convertView.findViewById(R.id.profile_image);

        nameTextView.setText(profile.getName());
        Glide.with(context.getApplicationContext()).load(profile.getImageUrl()).into(profileImageView);

        ImageButton deleteButton = convertView.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete this profile?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Remove item from Firestore, data list, and notify adapter
                            if (profile.getImageUrl() != null && !profile.getImageUrl().isEmpty()) {
                                FirebaseStorage.getInstance().getReferenceFromUrl(profile.getImageUrl()).delete()
                                        .addOnSuccessListener(unused -> Log.d(TAG, "Image successfully deleted."))
                                        .addOnFailureListener(e -> Log.w(TAG, "Failed to delete image.", e));
                            }

                            FirebaseFirestore.getInstance().collection("profiles").document(profile.getDeviceId())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error deleting document", e);
                                        }
                                    });

                            profiles.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Profile deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            // Dismiss the dialog if the user cancels
                            dialog.dismiss();
                        })
                        .show();
            }
        });

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProfileDetailsAdmin.class);
            intent.putExtra("deviceId", profiles.get(position).getDeviceId());
            context.startActivity(intent);
        });

        return convertView;
    }

}
