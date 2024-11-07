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

import java.util.List;

public class EntrantAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> entrantNames;

    public EntrantAdapter(Context context, List<String> entrantNames) {
        super(context, 0, entrantNames);
        this.context = context;
        this.entrantNames = entrantNames;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Inflate the custom layout if not already created
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.profile_list_view_entrant, parent, false);
        }

        String entrantName = entrantNames.get(position);

        TextView profileNameTextView = convertView.findViewById(R.id.profile_name);

        profileNameTextView.setText(entrantName);

        return convertView;
    }

}
