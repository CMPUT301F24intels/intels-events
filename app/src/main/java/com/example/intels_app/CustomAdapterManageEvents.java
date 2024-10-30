package com.example.intels_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapterManageEvents extends BaseAdapter {
    private Context context;
    private ArrayList<String> data;

    public CustomAdapterManageEvents(Context context, ArrayList<String> data) {
        this.context = context;
        this.data = data;
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_manage_event, parent, false);
        }

        TextView eventText = convertView.findViewById(R.id.event_text);
        eventText.setText(data.get(position)); // Populate each item’s text

        return convertView;
    }


}
