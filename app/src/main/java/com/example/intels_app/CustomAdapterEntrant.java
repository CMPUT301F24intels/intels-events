/**
 * This adapter is a custom adapter extending BaseAdapter and  is used to populate
 * the entrant gridview under View My Waitlist with Event objects to display the
 * events the entrant has joined the waitlist for. The event manages event deletion
 * for the user to leave the waitlist and provides a toggle switch for the entrant
 * to be considered for reselection in draw.
 * @author Aayushi Shah
 * @see com.example.intels_app.Event Event object
 */

package com.example.intels_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CustomAdapterEntrant extends BaseAdapter {
    private Context context;
    private List<Event> data;
    private String deviceId;

    public CustomAdapterEntrant(Context context, List<Event> data, String deviceId) {
        this.context = context;
        this.data = data;
        this.deviceId = deviceId;
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

        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to leave the waitlist for this event?")
                    .setPositiveButton("Confirm", (dialog, which) -> {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        // Query Firestore to find the document with the matching deviceId and eventName
                        db.collection("waitlisted_entrants")
                                .whereEqualTo("deviceId", deviceId) // Match the device ID
                                .whereEqualTo("eventName", event.getEventName()) // Match the event name
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                                        for (DocumentSnapshot document : task.getResult()) {
                                            document.getReference().delete()
                                                    .addOnSuccessListener(aVoid -> {
                                                        data.remove(position);
                                                        notifyDataSetChanged();
                                                        Toast.makeText(context, "Event removed from waitlist", Toast.LENGTH_SHORT).show();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(context, "Failed to delete event", Toast.LENGTH_SHORT).show();
                                                        Log.e("Firestore", "Error deleting document", e);
                                                    });
                                        }
                                    } else {
                                        Toast.makeText(context, "No matching event found", Toast.LENGTH_SHORT).show();
                                        Log.w("Firestore", "No matching document found.");
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Error fetching event", Toast.LENGTH_SHORT).show();
                                    Log.e("Firestore", "Error fetching document", e);
                                });
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        // Dismiss the dialog if the user cancels
                        dialog.dismiss();
                    })
                    .show();
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
