package com.example.intels_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;

import java.util.List;

public class ProfileAdapter extends BaseAdapter {
    private Context context;
    private List<Profile> profiles;

    public ProfileAdapter(Context context, List<Profile> profiles) {
        this.context = context;
        this.profiles = profiles;
    }

    @Override
    public int getCount() {
        return profiles.size();
    }

    @Override
    public Object getItem(int position) {
        return profiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.profile_list_view, parent, false);
        }

        Profile profile = profiles.get(position);

        TextView nameTextView = convertView.findViewById(R.id.profile_name);
        ImageView profileImageView = convertView.findViewById(R.id.profile_image);
        ImageButton deleteButton = convertView.findViewById(R.id.delete_button);

        nameTextView.setText(profile.getName());
        profileImageView.setImageResource(profile.getImageResId());

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profiles.remove(position);
                notifyDataSetChanged();
                Toast.makeText(context, "Profile deleted", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
