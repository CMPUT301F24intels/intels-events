package com.example.intels_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SelectedEntrantAdapter extends RecyclerView.Adapter<SelectedEntrantAdapter.EntrantViewHolder> {
    private Context context;
    private List<Profile> entrants;

    public SelectedEntrantAdapter(Context context, List<Profile> entrants) {
        this.context = context;
        this.entrants = entrants;
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

    public static class EntrantViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, emailTextView, contactTextView;

        public EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
        }
    }
}
