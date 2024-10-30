package com.example.intels_app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CustomAdapterOrganizer extends BaseAdapter {
    private Context context;
    private List<Event> data;

    public CustomAdapterOrganizer(Context context, List<Event> data) {
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

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_organizer, parent, false);
        }

        Event currentEvent = data.get(position);

        TextView eventText = convertView.findViewById(R.id.event_text);
        eventText.setText(currentEvent.getEventName());// Populate each item’s text

        return convertView;
    }
}

