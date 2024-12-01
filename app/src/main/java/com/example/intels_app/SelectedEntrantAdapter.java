/**
 * This class displays a list of selected entrants in a RecyclerView and binds
 * each entrant's profile information (such as name and email) to the corresponding
 * views in the layout.
 * @author Aayushi Shah, Katrina Alejo
 * @see com.example.intels_app.Profile Profile object
 */

package com.example.intels_app;

import android.app.AlertDialog;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * This class displays a list of selected entrants in a RecyclerView and binds
 * each entrant's profile information (such as name and email) to the corresponding
 * views in the layout.
 * @author Aayushi Shah
 * @see com.example.intels_app.Profile Profile object
 */

public class SelectedEntrantAdapter extends RecyclerView.Adapter<SelectedEntrantAdapter.EntrantViewHolder> implements Filterable {
    private Context context;
    private List<Profile> entrants;
    private List<Profile> entrantsFull; // Original full list for filtering
    private Filter entrantFilter;

    public SelectedEntrantAdapter(Context context, List<Profile> entrants) {
        this.context = context;
        this.entrants = entrants;
        this.entrantsFull = new ArrayList<>(entrants); // Copy of the full list for filtering
    }

    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout, match with profile_list_view_entrant if needed
        View view = LayoutInflater.from(context).inflate(R.layout.profile_list_view, parent, false);
        return new EntrantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntrantViewHolder holder, int position) {
        Profile profile = entrants.get(position);

        // Bind name
        holder.nameTextView.setText(profile.getName());

        // Load profile picture using Glide
        if (profile.getImageUrl() != null && !profile.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(profile.getImageUrl())
                    .placeholder(R.drawable.person_image) // Placeholder image while loading
                    .error(R.drawable.person_image) // Fallback image on error
                    .into(holder.profileImageView);
        } else {
            // If no image URL, set the default profile picture
            holder.profileImageView.setImageResource(R.drawable.person_image);
        }

        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to remove this entrant from the lottery list?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        String profileId = entrants.get(position).getName(); // Replace with the correct field
                        entrants.remove(position); // Remove from the UI
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, entrants.size()); // Update RecyclerView
                        ((LotteryList) context).deleteEntrantFromLotteryList(profileId); // Call the delete method
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return entrants.size();
    }

    @Override
    public Filter getFilter() {
        if (entrantFilter == null) {
            entrantFilter = new EntrantFilter();
        }
        return entrantFilter;
    }

    /**
     * ViewHolder for RecyclerView items.
     */
    public static class EntrantViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ImageView profileImageView;
        ImageButton deleteButton;

        public EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.profile_name);
            profileImageView = itemView.findViewById(R.id.profile_image);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

    /**
     * Custom Filter for RecyclerView items.
     */
    private class EntrantFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Profile> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(entrantsFull); // No filter, show all
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Profile profile : entrantsFull) {
                    if (profile.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(profile);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            entrants.clear();
            entrants.addAll((List) results.values);
            notifyDataSetChanged();
        }
    }

    /**
     * Method to update the data dynamically.
     */
    public void updateData(List<Profile> newProfiles) {
        this.entrants = newProfiles;
        this.entrantsFull = new ArrayList<>(newProfiles);
        notifyDataSetChanged();
    }

}