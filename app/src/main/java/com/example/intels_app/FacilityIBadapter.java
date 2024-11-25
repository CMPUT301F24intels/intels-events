package com.example.intels_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class FacilityIBadapter extends BaseAdapter {
    private ArrayList<FacilityDataClass> facilityDataList;
    private Context context;
    private LayoutInflater layoutInflater;

    public FacilityIBadapter(ArrayList<FacilityDataClass> facilityDataList, Context context) {
        this.facilityDataList = facilityDataList;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return facilityDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return facilityDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

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

    private static class ViewHolder {
        ImageView gridImage;
    }
}
