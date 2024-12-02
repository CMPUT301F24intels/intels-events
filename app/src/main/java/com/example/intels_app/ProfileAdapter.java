package com.example.intels_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * ProfileAdapter is a custom adapter for displaying and filtering a list of Profile objects
 * in a ListView. It supports search functionality through filtering, updating the displayed
 * list based on user input. This adapter inflates a custom layout for each list item and binds profile data, such as
 * the name, to the corresponding view elements.
 * @author Aayushi Shah
 * @see Profile profile object
 */
public class ProfileAdapter extends BaseAdapter implements Filterable {
    public Context context;
    public List<Profile> originalProfiles;
    public List<Profile> filteredProfiles;
    private Filter ProfileFilter;

    /**
     * Constructor to create an instance of ProfileAdapter.
     * @param context The context in which the adapter is used.
     * @param profileList The list of Profile objects to display.
     */
    public ProfileAdapter(Context context, List<Profile> profileList) {
        this.context = context;
        this.originalProfiles = new ArrayList<>(profileList);
        this.filteredProfiles = new ArrayList<>(profileList);
    }

    /**
     * Returns the number of profiles in the filtered list.
     * @return The size of the filteredProfiles list.
     */
    @Override
    public int getCount() {
        return filteredProfiles.size();
    }

    /**
     * Returns the Profile object at the specified position in the filtered list.
     * @param position The position of the profile in the filtered list.
     * @return The Profile object at the specified position.
     */
    @Override
    public Profile getItem(int position) {
        return filteredProfiles.get(position);
    }

    /**
     * Returns the item ID for the specified position.
     * @param position The position of the profile.
     * @return The position value as the item ID.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Returns the filter used to filter profiles based on their name.
     * @return The ProfileFilter object used to perform filtering.
     */
    public Filter getFilter() {
        if (ProfileFilter == null) {
            ProfileFilter = new ProfileFilter();
        }
        return ProfileFilter;
    }

    /**
     * Returns the view for a specific item in the list.
     * @param position The position of the item in the list.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent ViewGroup that this view will be attached to.
     * @return The View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.profile_list_view_entrant, parent, false);
        }

        Profile profile = filteredProfiles.get(position);

        TextView nameTextView = convertView.findViewById(R.id.profile_name);
        ImageView profileImageView = convertView.findViewById(R.id.profile_image);

        nameTextView.setText(profile.getName());

        if (profile.getImageUrl() != null && !profile.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(profile.getImageUrl())
                    .placeholder(R.drawable.person_image) // Default placeholder image
                    .error(R.drawable.person_image) // Fallback image in case of an error
                    .into(profileImageView);
        } else {
            profileImageView.setImageResource(R.drawable.person_image); // Default image if no URL
        }

        return convertView;
    }

    /**
     * ProfileFilter is an inner class that provides filtering capabilities for the ProfileAdapter.
     * It filters profiles based on their name.
     */
    private class ProfileFilter extends Filter {
        /**
         * Performs the filtering based on the search constraint.
         * @param constraint The constraint to filter by.
         * @return A FilterResults object containing the filtered profiles.
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = originalProfiles;
                results.count = originalProfiles.size();
            } else {
                String filterString = constraint.toString().toLowerCase();
                List<Profile> filteredProfiles = new ArrayList<>();

                for (Profile profile : originalProfiles) {
                    if (profile.getName().toLowerCase().contains(filterString)) {
                        filteredProfiles.add(profile);
                    }
                }

                results.values = filteredProfiles;
                results.count = filteredProfiles.size();
            }
            return results;
        }

        /**
         * Publishes the filtering results to update the filteredProfiles list and notify changes.
         * @param constraint The constraint used for filtering.
         * @param results The FilterResults containing the filtered data.
         */
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredProfiles = (List<Profile>) results.values;
            notifyDataSetChanged();
        }
    }

    /**
     * Updates the filteredProfiles list with new data and refreshes the adapter.
     * @param newProfiles The new list of Profile objects to update the adapter with.
     */
    public void updateData(List<Profile> newProfiles) {
            this.filteredProfiles = newProfiles;
            notifyDataSetChanged();
        }


}

/* JANAN USE THIS FOR ADMIN PROFILE LIST WITH DELETE FUNCTIONALITY
        ImageButton deleteButton = convertView.findViewById(R.id.delete_button);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profiles.remove(position);
                notifyDataSetChanged();
                Toast.makeText(context, "Profile deleted", Toast.LENGTH_SHORT).show();
            }
        });*/