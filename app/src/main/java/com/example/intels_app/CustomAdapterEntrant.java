package com.example.intels_app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import java.util.List;

public class CustomAdapterEntrant extends BaseAdapter {
    private Context context;
    private List<Event> data;

    public CustomAdapterEntrant(Context context, List<Event> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_entrant, parent, false);
        }

        TextView eventText = convertView.findViewById(R.id.event_text);
        ImageButton deleteButton = convertView.findViewById(R.id.delete_button);
        SwitchCompat reconsiderSwitch = convertView.findViewById(R.id.reconsider_switch);

        Event event = data.get(position);
        eventText.setText(event.getEventName());

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove item from data list and notify adapter
                data.remove(position);
                notifyDataSetChanged();
                Toast.makeText(context, "Event deleted", Toast.LENGTH_SHORT).show();
            }
        });

        reconsiderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(context, "Reconsider for draw enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Reconsider for draw disabled", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
