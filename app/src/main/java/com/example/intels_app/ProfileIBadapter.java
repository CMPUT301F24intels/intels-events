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
 * This adapter is used to populate a grid view with images in the profile section of the app. It takes a list of `DataClass` objects,
 * which contain URLs of images to be displayed in the grid. It uses the Glide image loading library to efficiently load images into
 * the `ImageView` components in the grid items.
 *
 * The adapter binds each image from the `dataClassArrayList` to a corresponding `ImageView` in the grid layout and optimizes
 * recycling of views with the `ViewHolder` pattern.
 *
 * @author Kanishka Aswani
 * @see com.bumptech.glide.Glide Glide image loading library
 */

public class ProfileIBadapter extends BaseAdapter {
    private ArrayList<DataClass> dataClassArrayList;
    private Context context;
    private LayoutInflater layoutInflater;

    /**
     * Constructor for ProfileIBadapter.
     * @param dataClassArrayList ArrayList of DataClass objects that contain data for each item in the GridView.
     * @param context            Context of the activity using this adapter.
     */
    public ProfileIBadapter(ArrayList<DataClass> dataClassArrayList, Context context) {
        this.dataClassArrayList = dataClassArrayList;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    /**
     * Returns the number of items in the adapter.
     * @return Number of items in the dataClassArrayList.
     */
    @Override
    public int getCount() {
        return dataClassArrayList.size();
    }

    /**
     * Returns the item at a specified position.
     * @param position Position of the item to return.
     * @return The DataClass object at the given position.
     */
    @Override
    public Object getItem(int position) {
        return dataClassArrayList.get(position);
    }

    /**
     * Returns the row ID associated with the specified position.
     * @param position Position of the item whose ID is needed.
     * @return The position itself, used as the ID.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Creates and returns a view for each item in the data set.
     * @param position    The position of the item within the data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent that this view will eventually be attached to.
     * @return The created view with the data bound to it.
     */
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

    /**
     * ViewHolder class to improve performance by avoiding repeated calls to findViewById.
     */
    private static class ViewHolder {
        ImageView gridImage;
    }
}
