/**
 * This class displays a list of selected entrants in a RecyclerView and binds
 * each entrant's profile information (such as name and email) to the corresponding
 * views in the layout.
 * @author Aayushi Shah, Katrina Alejo
 * @see com.example.intels_app.Profile Profile object
 */

package com.example.intels_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
    private List<Profile> entrantsFull; // For keeping the full, unfiltered list

    public SelectedEntrantAdapter(Context context, List<Profile> entrants) {
        this.context = context;
        this.entrants = entrants;
        this.entrantsFull = new ArrayList<>(entrants); // Make a copy of the full list
    }

    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entrant, parent, false);
        return new EntrantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntrantViewHolder holder, int position) {
        Profile profile = entrants.get(position);
        holder.nameTextView.setText(profile.getName());
        holder.emailTextView.setText(profile.getEmail());
    }

    @Override
    public int getItemCount() {
        return entrants.size();
    }

    @Override
    public Filter getFilter() {
        return entrantFilter;
    }

    private final Filter entrantFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Profile> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(entrantsFull); // Show all if no filter is applied
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Profile item : entrantsFull) {
                    if (item.getName().toLowerCase().contains(filterPattern) ||
                            item.getEmail().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
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
    };

    public static class EntrantViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, emailTextView;

        public EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
        }
    }
}
