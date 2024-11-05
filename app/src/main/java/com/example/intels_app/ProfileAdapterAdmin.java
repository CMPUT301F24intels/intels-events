package com.example.intels_app;

import android.util.Log;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ProfileAdapterAdmin extends BaseAdapter {
    private Context context;
    private List<Profile> profiles;

    public ProfileAdapterAdmin(Context context, List<Profile> profiles) {
        this.context = context;
        this.profiles = new ArrayList<>(profiles);
    }

    @Override
    public int getCount() {
        return profiles.size();
    }

    @Override
    public Object getItem(int i) {
        return profiles.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("Adapter", "Hi");
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.profile_list_view, parent, false);
        }

        Profile profile = profiles.get(position);
        Log.d("Adapter", profile.getName() + " " + profile.getImageResId());


        TextView nameTextView = convertView.findViewById(R.id.profile_name);
        ImageView profileImageView = convertView.findViewById(R.id.profile_image);

        nameTextView.setText(profile.getName());
        profileImageView.setImageResource(profile.getImageResId());

        return convertView;
    }

}
