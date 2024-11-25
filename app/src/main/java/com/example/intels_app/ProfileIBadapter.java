package com.example.intels_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ProfileIBadapter extends BaseAdapter {
    private ArrayList<DataClass> dataClassArrayList;
    private Context context;
    private LayoutInflater layoutInflater;

    public ProfileIBadapter(ArrayList<DataClass> dataClassArrayList, Context context) {
        this.dataClassArrayList = dataClassArrayList;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return dataClassArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataClassArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.profilegrid, parent, false);
            holder = new ViewHolder();
            holder.gridImage = convertView.findViewById(R.id.gridImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Load image into the ImageView using Glide
        DataClass data = dataClassArrayList.get(position);
        Glide.with(context).load(data.getImageUrl()).into(holder.gridImage);

        return convertView;
    }

    private static class ViewHolder {
        ImageView gridImage;
    }
}
