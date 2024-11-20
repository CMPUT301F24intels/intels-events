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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class CustomAdapterManageEvents extends BaseAdapter {
    private Context context;
    private ArrayList<Event> data;
    private OnEventClickListener listener;
    Event event;

    // Define the interface inside the adapter
    public interface OnEventClickListener {
        void onEventClick(int position);
    }

    /**
     * Takes a list of events
     * @param context Context of the activity
     * @param data List of events
     */
    public CustomAdapterManageEvents(Context context, ArrayList<Event> data, OnEventClickListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
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
            FirebaseFirestore.getInstance().collection("events")
                    .document(data.get(position).getEventName())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            event = documentSnapshot.toObject(Event.class);

                            // Ensure that event is not null before proceeding
                            if (event != null) {
                                // Delete poster from storage
                                FirebaseStorage.getInstance().getReferenceFromUrl(event.getPosterUrl()).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d(TAG, "Poster successfully deleted.");
                                            }
                                        }).addOnFailureListener(e -> Log.w(TAG, "Failed to delete poster.", e));

                                // Delete QR from storage
                                FirebaseStorage.getInstance().getReferenceFromUrl(event.getQrCodeUrl()).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d(TAG, "QR successfully deleted.");
                                            }
                                        }).addOnFailureListener(e -> Log.w(TAG, "Failed to delete QR code.", e));

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
                            } else {
                                Log.w(TAG, "Event data is null, cannot delete.");
                                Toast.makeText(context, "Failed to retrieve event data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(e -> Log.w(TAG, "Failed to fetch event details for deletion", e));
        });

        convertView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEventClick(position);
            }
        });

        return convertView;
    }

}
