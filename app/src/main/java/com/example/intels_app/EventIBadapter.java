package com.example.intels_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class EventIBadapter extends BaseAdapter {
    private ArrayList<EventDataClass> eventDataClassArrayList;
    private Context context;
    private LayoutInflater layoutInflater;

    // Constructor
    public EventIBadapter(ArrayList<EventDataClass> eventDataClassArrayList, Context context) {
        this.eventDataClassArrayList = eventDataClassArrayList;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return eventDataClassArrayList.size();  // Return the size of the event list
    }

    @Override
    public Object getItem(int position) {
        return eventDataClassArrayList.get(position);  // Return the event data at the given position
    }

    @Override
    public long getItemId(int position) {
        return position;  // Return the position as the ID
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // If the view has not been recycled, inflate a new view and create a ViewHolder
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.eventpostersgrid, parent, false);
            holder = new ViewHolder();
            holder.gridImage = convertView.findViewById(R.id.gridImage);  // Find the ImageView in the layout
            convertView.setTag(holder);  // Store the holder for future reuse
        } else {
            holder = (ViewHolder) convertView.getTag();  // Reuse the view holder
        }

        // Get the event data at the current position
        EventDataClass eventData = eventDataClassArrayList.get(position);

        // Load the event image into the ImageView using Glide
        Glide.with(context).load(eventData.getImageUrl()).into(holder.gridImage);

        return convertView;  // Return the inflated view with the event image
    }

    // ViewHolder class to hold references to the views in the layout
    private static class ViewHolder {
        ImageView gridImage;
    }
}
