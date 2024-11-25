/**
 * Takes a list of events and inflates it into the gridview
 * @author Janan Panchal
 * @see com.example.intels_app.EventDetailsOrganizer Event details page for event just created
 */
package com.example.intels_app;

import static android.content.ContentValues.TAG;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlertDialog;
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
    private ArrayList<Event> eventData;
    private ArrayList<Facility> facilityData;
    private OnEventClickListener listener;
    private boolean isEventAdapter; // To differentiate between Event and Facility types
    Event event;
    Facility facility;

    // Define the interface inside the adapter
    public interface OnEventClickListener {
        void onEventClick(int position);
    }

    /**
     * Takes a list of events
     *
     * @param context Context of the activity
     * @param data    List of events
     */
    public CustomAdapterManageEvents(Context context, ArrayList<Event> data, OnEventClickListener listener) {
        this.context = context;
        this.eventData = data;
        this.listener = listener;
        this.isEventAdapter = true;
    }

    public CustomAdapterManageEvents(Context context, ArrayList<Facility> data) {
        this.context = context;
        this.facilityData = data;
        this.isEventAdapter = false;
    }

    /**
     * Returns the number of items in the list
     *
     * @return The number of items in the event list
     */
    @Override
    public int getCount() {
        if (isEventAdapter && eventData != null) {
            return eventData.size();
        } else if (!isEventAdapter && facilityData != null) {
            return facilityData.size();
        }
        return 0;
    }

    /**
     * Returns the item at the given position
     *
     * @param position The position of the item in the list
     * @return The item at the given position
     */
    @Override
    public Object getItem(int position) {
        if (isEventAdapter && eventData != null) {
            return eventData.get(position);
        } else if (!isEventAdapter && facilityData != null) {
            return facilityData.get(position);
        }
        return null;
    }

    /**
     * Returns the id of the item at the given position
     *
     * @param position The position of the item in the list
     * @return The id of the item at the given position
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Inflates the event data into gridview
     *
     * @param position    The position of the item in the list
     * @param convertView The view to be inflated
     * @param parent      The parent view
     * @return The inflated view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Inflate the layout for each item
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_manage_event, parent, false);
        }

        // Populate the view based on the data type
        if (isEventAdapter && eventData != null) {

            // Handling Event type
            TextView eventText = convertView.findViewById(R.id.event_text);
            eventText.setText(eventData.get(position).getEventName());

            // Set up delete button functionality for Event
            ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete this event?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Delete event-related operations
                            Log.d(TAG, "Deleting event at position: " + position);
                            deleteEvent(position);
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            // Dismiss the dialog if the user cancels
                            dialog.dismiss();
                        })
                        .show();
            });

            // Set click listener for the event item
            convertView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEventClick(position);
                }
            });

        } else if (!isEventAdapter && facilityData != null) {

            // Set the text for each item
            TextView facilityText = convertView.findViewById(R.id.event_text);
            facilityText.setText(facilityData.get(position).getFacilityName()); // Populate each item’s text

            // Set up delete button functionality for Facility
            ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete this facility?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Delete event-related operations
                            deleteFacility(position);
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            // Dismiss the dialog if the user cancels
                            dialog.dismiss();
                        })
                        .show();
            });

            convertView.setOnClickListener(v -> {
                Intent intent = new Intent(context, FacilityDetailsAdmin.class);
                intent.putExtra("Facility Name", facilityData.get(position).getFacilityName());
                context.startActivity(intent);
            });
        }

        return convertView;
    }

    // Method to delete event data from Firestore and Storage
    private void deleteEvent(int position) {
        Log.d(TAG, "Deleting event: " + eventData.get(position).getEventName());
        String eventToDelete = eventData.get(position).getEventName();

        FirebaseFirestore.getInstance().collection("events")
                .document(eventToDelete)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    event = documentSnapshot.toObject(Event.class);
                    Log.d(TAG, "Event data retrieved: " + event.getEventName());

                    if (event != null) {
                        Log.d(TAG, "Event data is not null.");

                        // Delete poster and QR code from Firebase Storage
                        FirebaseStorage.getInstance().getReferenceFromUrl(event.getPosterUrl()).delete()
                                .addOnSuccessListener(unused -> Log.d(TAG, "Poster successfully deleted."))
                                .addOnFailureListener(e -> Log.w(TAG, "Failed to delete poster.", e));

                        // Delete QR code from Firebase Storage
                        Log.d(TAG, "QR code URL: " + event.getQrCodeUrl());

                        FirebaseStorage.getInstance().getReferenceFromUrl(event.getQrCodeUrl()).delete()
                                .addOnSuccessListener(unused -> Log.d(TAG, "QR successfully deleted."))
                                .addOnFailureListener(e -> Log.w(TAG, "Failed to delete QR code.", e));

                        // Delete event from Firestore
                        FirebaseFirestore.getInstance().collection("events")
                                .document(eventData.get(position).getEventName())
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    Toast.makeText(context, "Event deleted", Toast.LENGTH_SHORT).show();
                                    eventData.remove(position);
                                    notifyDataSetChanged();
                                })
                                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
                    } else {
                        Log.w(TAG, "Event data is null, cannot delete.");
                        Toast.makeText(context, "Failed to retrieve event data", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.w(TAG, "Failed to fetch event details for deletion", e));
    }

    // Method to delete facility data from FireStore and Storage
    private void deleteFacility(int position) {
        FirebaseFirestore.getInstance().collection("facilities")
                .document(facilityData.get(position).getFacilityName())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    facility = documentSnapshot.toObject(Facility.class);

                    if (facility != null) {
                        // Delete facility image from Firebase Storage
                        FirebaseStorage.getInstance().getReferenceFromUrl(facility.getFacilityImageUrl()).delete()
                                .addOnSuccessListener(unused -> Log.d(TAG, "Image successfully deleted."))
                                .addOnFailureListener(e -> Log.w(TAG, "Failed to delete image.", e));

                        // Delete facility from Firestore
                        FirebaseFirestore.getInstance().collection("facilities")
                                .document(facilityData.get(position).getFacilityName())
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    Toast.makeText(context, "Facility deleted", Toast.LENGTH_SHORT).show();
                                    facilityData.remove(position);
                                    notifyDataSetChanged();
                                })
                                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
                    } else {
                        Log.w(TAG, "Facility data is null, cannot delete.");
                        Toast.makeText(context, "Failed to retrieve facility data", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.w(TAG, "Failed to fetch facility details for deletion", e));
    }
}