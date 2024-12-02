package com.example.intels_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
/**
 * This adapter is responsible for displaying a list of event images in a GridView.
 * It binds data from an ArrayList of EventDataClass objects to the grid items using Glide to load images.
 *
 * @author Kanishka Aswani
 * @see android.widget.BaseAdapter
 */

public class EventIBadapter extends BaseAdapter {
    private ArrayList<EventDataClass> eventDataClassArrayList;
    private Context context;
    private LayoutInflater layoutInflater;

    /**
     * Constructor for initializing the adapter with event data and context.
     * @param eventDataClassArrayList List of event data objects.
     * @param context                 The context of the activity that creates this adapter.
     */
    public EventIBadapter(ArrayList<EventDataClass> eventDataClassArrayList, Context context) {
        this.eventDataClassArrayList = eventDataClassArrayList;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    /**
     * Returns the number of items in the data set represented by this adapter.
     * @return The size of the event data list.
     */
    @Override
    public int getCount() {
        return eventDataClassArrayList.size();  // Return the size of the event list
    }

    /**
     * Returns the data item associated with the specified position in the data set.
     * @param position The position of the item within the data set.
     * @return The event data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return eventDataClassArrayList.get(position);  // Return the event data at the given position
    }

    /**
     * Returns the row ID associated with the specified position in the list.
     * @param position The position of the item within the adapter's data set.
     * @return The ID of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;  // Return the position as the ID
    }

    /**
     * Returns a view for the item at the specified position.
     * @param position    The position of the item within the data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
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

    /**
     * ViewHolder class to hold references to the views in the layout.
     */
    private static class ViewHolder {
        ImageView gridImage;
    }
}
