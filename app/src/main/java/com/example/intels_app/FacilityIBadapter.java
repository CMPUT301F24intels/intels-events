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
 * This adapter is responsible for providing facility image data to be displayed in a grid view.
 * It loads the images from URLs into ImageViews using Glide, based on the data in the facility list.
 * The adapter works with the custom layout `facilityimagegrid`.
 *
 * @author Kaniskha Aswani
 */

public class FacilityIBadapter extends BaseAdapter {
    private ArrayList<FacilityDataClass> facilityDataList;
    private Context context;
    private LayoutInflater layoutInflater;

    /**
     * Constructor for the FacilityIBadapter.
     * @param facilityDataList The list of facility data to be displayed.
     * @param context The context in which the adapter is being used.
     */
    public FacilityIBadapter(ArrayList<FacilityDataClass> facilityDataList, Context context) {
        this.facilityDataList = facilityDataList;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    /**
     * Returns the number of items in the facility data list.
     * @return The size of the facility data list.
     */
    @Override
    public int getCount() {
        return facilityDataList.size();
    }

    /**
     * Returns the item at the specified position in the facility data list.
     * @param position The position of the item.
     * @return The FacilityDataClass object at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return facilityDataList.get(position);
    }

    /**
     * Returns the row ID associated with the specified position.
     * @param position The position of the item.
     * @return The ID of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Returns the view for each facility image in the facility data list.
     * @param position The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     * @return The View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.facilityimagegrid, parent, false);
            holder = new ViewHolder();
            holder.gridImage = convertView.findViewById(R.id.gridImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Load image into ImageView using Glide
        FacilityDataClass data = facilityDataList.get(position);
        Glide.with(context).load(data.getImageUrl()).into(holder.gridImage);

        return convertView;
    }

    /**
     * ViewHolder class to hold references to the views in each grid item,
     * which is respective facility images.
     */
    private static class ViewHolder {
        ImageView gridImage;
    }
}
