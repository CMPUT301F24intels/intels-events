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
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_manage_event, parent, false);
        }

        Event currentEvent = data.get(position);

        TextView eventText = convertView.findViewById(R.id.event_text);
        eventText.setText(currentEvent.getEventName());// Populate each item’s text

        ImageView infoButton = convertView.findViewById(R.id.infoButton);
        infoButton.setVisibility(View.INVISIBLE);

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EntrantInWaitlist.class);
            intent.putExtra("eventName", data.get(position).getEventName());
            context.startActivity(intent);
        });

        return convertView;
    }
}
