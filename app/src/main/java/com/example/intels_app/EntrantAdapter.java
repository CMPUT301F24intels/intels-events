package com.example.intels_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class EntrantAdapter extends ArrayAdapter<Entrant> implements Filterable {
    private Context context;
    private List<Entrant> entrantList;
    private List<Entrant> originalEntrantList;

    public EntrantAdapter(Context context, List<Entrant> entrantList) {
        super(context, 0, entrantList);
        this.context = context;
        this.entrantList = entrantList;
        this.originalEntrantList = new ArrayList<>(entrantList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.profile_list_view_entrant, parent, false);
        }

        Entrant entrant = entrantList.get(position);

        TextView profileNameTextView = convertView.findViewById(R.id.profile_name);
        ImageView profileImageView = convertView.findViewById(R.id.profile_image);

        profileNameTextView.setText(entrant.getName());

        if (entrant.getImageUrl() != null && !entrant.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(entrant.getImageUrl())
                    .placeholder(R.drawable.person_image)
                    .error(R.drawable.person_image)
                    .into(profileImageView);
        } else {
            profileImageView.setImageResource(R.drawable.person_image);
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Entrant> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(originalEntrantList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Entrant entrant : originalEntrantList) {
                        if (entrant.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(entrant);
                        }
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                entrantList.clear();
                entrantList.addAll((List<Entrant>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getCount() {
        return entrantList.size();
    }

    @Override
    public Entrant getItem(int position) {
        return entrantList.get(position);
    }
}