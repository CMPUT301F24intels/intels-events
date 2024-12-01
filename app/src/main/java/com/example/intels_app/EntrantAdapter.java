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
/**
 * This class is an adapter for displaying a list of entrants in a list view.
 * It binds each entrant's name and profile image to the corresponding views in the list item layout.
 * The class uses Glide to load profile images, and it shows a default image if the entrant does not have an image URL.
 * @author Aayushi Shah
 * @see com.example.intels_app.Entrant Entrant object representing an individual entrant
 * @see com.example.intels_app.Event Event related to the entrants
 */

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
