package com.example.intels_app;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.TooltipCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * This adapter is a custom adapter extending BaseAdapter and  is used to populate
 * the organizer gridview under View My Waitlist with Event objects to display the
 * events the organizer has created. Each event item has a event deletion option
 * which will remove the event from the Firestore Database and the adapter view.
 * Each item in the gridview is clickable and will enable opening another activity
 * for organizers to view the entrants associated to the specific event they clicked.
 * @author Aayushi Shah, Dhanshri Patel
 * @see com.example.intels_app.Event Event object
 * @see com.example.intels_app.EntrantInWaitlist Entrant information for an event
 */

public class CustomAdapterOrganizer extends BaseAdapter {
    private Context context;
    private ArrayList<Event> eventData;
    private ArrayList<Facility> facilityData;
    private CustomAdapterOrganizer.OnEventClickListener listener;
    private boolean isEventAdapter; // To differentiate between Event and Facility types
    Event event;
    Facility facility;

    /**
     * Interface for event click handling.
     */
    public interface OnEventClickListener {
        /**
         * Called when an event item is clicked.
         * @param position The position of the clicked event.
         */
        void onEventClick(int position);
    }

    /**
     * Constructor for creating an adapter for events.
     * @param context The context in which the adapter is used.
     * @param data The list of Event objects to be displayed.
     * @param listener The listener for handling event clicks.
     */
    public CustomAdapterOrganizer(Context context, ArrayList<Event> data, CustomAdapterOrganizer.OnEventClickListener listener) {
        this.context = context;
        this.eventData = data;
        this.listener = listener;
        this.isEventAdapter = true;
    }

    /**
     * Constructor for creating an adapter for facilities.
     * @deprecated
     * @param context The context in which the adapter is used.
     * @param data The list of Facility objects to be displayed.
     */
    public CustomAdapterOrganizer(Context context, ArrayList<Facility> data) {
        this.context = context;
        this.facilityData = data;
        this.isEventAdapter = false;
    }

    /**
     * Returns the size of dataset.
     * @return The number of events or facilities in the data set.
     */
    public int getCount() {
        return eventData.size();
    }

    /**
     * Returns the item at the specified position.
     * @param position The position of the item within the adapter's data set.
     * @return The Event or Facility object at the specified position.
     */
    public Object getItem(int position) {
        return eventData.get(position);
    }

    /**
     * Returns the row ID of the specified position.
     * @param position The position of the item within the adapter's data set.
     * @return The row ID of the specified item.
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * Provides a custom view for each item in the adapter's data set, either an event or a facility.
     * For events, it sets the event name and sets up a click listener to handle event selection via the listener interface.
     * For facilities, it sets the facility name and sets up a click listener to navigate to the `FacilityDetailsAdmin` activity.
     *
     * @param position The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent view that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_organizer, parent, false);
        }

        // Populate the view based on the data type
        if (isEventAdapter && eventData != null) {

            Event currentEvent = eventData.get(position);

            TextView eventText = convertView.findViewById(R.id.event_text);
            eventText.setText(currentEvent.getEventName());// Populate each item’s text

            convertView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEventClick(position);
                }
            });

        } else if (!isEventAdapter && facilityData != null) {

            // Set the text for each item
            TextView facilityText = convertView.findViewById(R.id.event_text);
            facilityText.setText(facilityData.get(position).getFacilityName()); // Populate each item’s text

            ImageView infoButton = convertView.findViewById(R.id.infoButton);
            infoButton.setVisibility(View.INVISIBLE);

            convertView.setOnClickListener(v -> {
                Intent intent = new Intent(context, FacilityDetailsAdmin.class);
                intent.putExtra("deviceId", facilityData.get(position).getDeviceId());
                context.startActivity(intent);
            });
        }

        return convertView;
    }
}
