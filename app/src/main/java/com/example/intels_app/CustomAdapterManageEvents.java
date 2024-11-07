package com.example.intels_app;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CustomAdapterManageEvents extends BaseAdapter {
    private Context context;
    private ArrayList<Event> data;

    public CustomAdapterManageEvents(Context context, ArrayList<Event> data) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_manage_event, parent, false);
        }

        TextView eventText = convertView.findViewById(R.id.event_text);
        eventText.setText(data.get(position).getEventName()); // Populate each item’s text

        // Set up delete button functionality
        ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> {
            FirebaseFirestore.getInstance().collection("events")
                    .document(data.get(position).getEventName())
                    .delete()
                    .addOnSuccessListener(unused -> {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        Toast.makeText(context, "Event deleted", Toast.LENGTH_SHORT).show();
                        data.remove(position);
                        notifyDataSetChanged();
                    }).addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
        });

        // Set up click listener to navigate to EventDetailsOrganizer
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailsOrganizer.class);
            intent.putExtra("eventId", data.get(position).getEventName()); // Pass the event ID or name
            context.startActivity(intent);
        });

        return convertView;
    }
}
