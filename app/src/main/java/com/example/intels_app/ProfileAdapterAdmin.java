/**
 * Adapter for the profile list view to take the data list and inflate it into the view.
 * @author Janan Panchal, Dhanshri Patel
 * @see com.example.intels_app.Profile Profile object
 * @see com.example.intels_app.AdminProfiles Get the data list from the admin profiles page
 */
package com.example.intels_app;

import static android.content.ContentValues.TAG;

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

import java.util.ArrayList;
import java.util.List;

public class ProfileAdapterAdmin extends BaseAdapter {
    private Context context;
    private List<Profile> profiles;

    public ProfileAdapterAdmin(Context context, List<Profile> profiles) {
        this.context = context;
        this.profiles = profiles;
    }

    public int getCount() {
        return profiles.size();
    }

    public Object getItem(int i) {
        return profiles.get(i);
    }

    public long getItemId(int i) {
        return i;
    }

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
                // Remove item from Firestore, data list, and notify adapter

                FirebaseFirestore.getInstance().collection("profiles").document(profile.getName())
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
                Toast.makeText(context, "Event deleted", Toast.LENGTH_SHORT).show();
            }
        });

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProfileDetailsAdmin.class);
            intent.putExtra("profileId", profiles.get(position).getName());
            context.startActivity(intent);
        });

        return convertView;
    }

}
