/**
 * CancelledProfileAdapter is a custom adapter for managing and displaying a list of profiles
 * that belong to cancelled entrants. It includes a delete button to remove entrants from the list
 * and from Firestore.
 *
 * @author Aayushi Shah
 */

package com.example.intels_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CancelledProfileAdapter extends ProfileAdapter {

    public CancelledProfileAdapter(Context context, List<Profile> profileList) {
        super(context, profileList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.profile_list_view, parent, false);
        }

        Profile profile = filteredProfiles.get(position);
        TextView nameTextView = convertView.findViewById(R.id.profile_name);
        ImageButton deleteButton = convertView.findViewById(R.id.delete_button);

        nameTextView.setText(profile.getName());

        deleteButton.setOnClickListener(v -> {
            filteredProfiles.remove(position);
            notifyDataSetChanged();
            Toast.makeText(context, "Cancelled entrant deleted: " + profile.getName(), Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }
}

