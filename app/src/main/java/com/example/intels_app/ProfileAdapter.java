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

import java.util.ArrayList;
import java.util.List;
/**
 * ProfileAdapter is a custom adapter for displaying and filtering a list of Profile objects
 * in a ListView. It supports search functionality through filtering, updating the displayed
 * list based on user input.
 *
 * This adapter inflates a custom layout for each list item and binds profile data, such as
 * the name, to the corresponding view elements.
 */
public class ProfileAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private List<Profile> originalProfiles;
    private List<Profile> filteredProfiles;
    private Filter ProfileFilter;

    public ProfileAdapter(Context context, List<Profile> profileList) {
        this.context = context;
        this.originalProfiles = new ArrayList<>(profileList);
        this.filteredProfiles = new ArrayList<>(profileList);
    }

    @Override
    public int getCount() {
        return filteredProfiles.size();
    }

    @Override
    public Profile getItem(int position) {
        return filteredProfiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Filter getFilter() {
        if (ProfileFilter == null) {
            ProfileFilter = new ProfileFilter();
        }
        return ProfileFilter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.profile_list_view_entrant, parent, false);
        }

        Profile profile = filteredProfiles.get(position);

        TextView nameTextView = convertView.findViewById(R.id.profile_name);
        //ImageView profileImageView = convertView.findViewById(R.id.profile_image);

        nameTextView.setText(profile.getName());
        //profileImageView.setImageResource(profile.getImageResId());
        //profileImageView.setImageResource(profile.getImageResId() != 0 ? profile.getImageResId() : R.drawable.default_image);

        return convertView;
    }

    private class ProfileFilter extends Filter {
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

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredProfiles = (List<Profile>) results.values;
            notifyDataSetChanged();
        }
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