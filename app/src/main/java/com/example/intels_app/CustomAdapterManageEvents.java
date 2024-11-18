/**
 * Takes a list of events and inflates it into the gridview
 * @author Janan Panchal
 * @see com.example.intels_app.EventDetailsOrganizer Event details page for event just created
 */
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

import com.example.intels_app.EventDetailsOrganizer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CustomAdapterManageEvents extends BaseAdapter {
    private Context context;
    private ArrayList<Event> data;

    /**
     * Takes a list of events
     * @param context Context of the activity
     * @param data List of events
     */
    public CustomAdapterManageEvents(Context context, ArrayList<Event> data) {
        this.context = context;
        this.data = data;
    }

    /**
     * Returns the number of items in the list
     * @return The number of items in the event list
     */
    @Override
    public int getCount() {
        return data.size();
    }

    /**
     * Returns the item at the given position
     * @param position The position of the item in the list
     * @return The item at the given position
     */
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    /**
     * Returns the id of the item at the given position
     * @param position The position of the item in the list
     * @return The id of the item at the given position
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Inflates the event data into gridview
     * @param position The position of the item in the list
     * @param convertView The view to be inflated
     * @param parent The parent view
     * @return The inflated view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Inflate the layout for each item
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_manage_event, parent, false);
        }

        // Set the text for each item
        TextView eventText = convertView.findViewById(R.id.event_text);
        eventText.setText(data.get(position).getEventName()); // Populate each item’s text

        // Set up delete button functionality
        ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> {

            // Delete the event from Firestore
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
            intent.putExtra("Event Name", data.get(position).getEventName()); // Pass the event ID or name
            context.startActivity(intent);
        });

        return convertView;
    }
}
