/**
 * This adapter is a custom adapter extending ArrayAdapter and inflates a
 * custom layout for each entrant and populates the entrant listview with
 * the entrant name.
 * @author Aayushi Shah
 * @see android.widget.ArrayAdapter ArrayAdapter
 */

package com.example.intels_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.List;

public class EntrantAdapter extends ArrayAdapter<Entrant> {
    private Context context;
    private List<Entrant> entrantList;

    public EntrantAdapter(Context context, List<Entrant> entrantList) {
        super(context, 0, entrantList);
        this.context = context;
        this.entrantList = entrantList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.profile_list_view_entrant, parent, false);
        }

        // Get the current entrant
        Entrant entrant = entrantList.get(position);

        // Get views
        TextView profileNameTextView = convertView.findViewById(R.id.profile_name);
        ImageView profileImageView = convertView.findViewById(R.id.profile_image);

        // Set name
        profileNameTextView.setText(entrant.getName());

        // Load image using Glide
        if (entrant.getImageUrl() != null && !entrant.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(entrant.getImageUrl())
                    .placeholder(R.drawable.person_image) // Default image while loading
                    .error(R.drawable.person_image) // Default image if an error occurs
                    .into(profileImageView);
        } else {
            // Set a default image if `imageUrl` is null or empty
            profileImageView.setImageResource(R.drawable.person_image);
        }

        return convertView;
    }
}
