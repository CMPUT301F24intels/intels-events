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
import java.util.Map;
/**
 * This adapter is a custom adapter extending BaseAdapter and  is used to populate
 * the entrant gridview under View My Waitlist with Event objects to display the
 * events the entrant has joined the waitlist for. The event manages event deletion
 * for the user to leave the waitlist and provides a toggle switch for the entrant
 * to be considered for reselection in draw.
 * @author Aayushi Shah
 * @see com.example.intels_app.Event Event object
 */

public class CustomAdapterEntrant extends BaseAdapter {
    private Context context;
    private List<Event> data;
    private String deviceId;

    /**
     * Constructor for CustomAdapterEntrant.
     * @param context The context in which the adapter will be used.
     * @param data The list of Event objects to be displayed in the GridView.
     * @param deviceId The unique device ID of the entrant.
     */
    public CustomAdapterEntrant(Context context, List<Event> data, String deviceId) {
        this.context = context;
        this.data = data;
        this.deviceId = deviceId;
    }

    /**
     * Returns the number of items in the data set represented by the adapter.
     * @return The number of events in the adapter.
     */
    @Override
    public int getCount() {
        return data.size();
    }

    /**
     * Returns the Event object at the specified position in the data set.
     * @param position The position of the item within the adapter's data set.
     * @return The Event object at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    /**
     * Returns the row ID of the specified position.
     * @param position The position of the item within the adapter's data set.
     * @return The row ID of the specified item.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Provides a custom view for each item in the data set, including setting event details,
     * handling the "reconsider for draw" switch state, and allowing removal from the waitlist
     * with Firestore updates.
     * @param position The position of the item within the adapter's data set.
     * @param convertView old view to reuse.
     * @param parent The parent view that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
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

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d("CustomAdapterEntrant", "Querying not_selected_entrants with Device ID: " +
                deviceId + ", Event Name: " + event.getEventName());



        db.collection("not_selected_entrants")
                .whereEqualTo("deviceId", deviceId)
                .whereEqualTo("eventName", event.getEventName())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            String profileId = document.getString("profileId");
                            Boolean reconsiderState = document.getBoolean("reconsiderForDraw");

                            if (profileId == null || reconsiderState == null) {
                                Log.e("CustomAdapterEntrant", "Invalid document: Missing profileId or reconsiderForDraw");
                                continue; // Skip invalid documents
                            }

                            // Log valid profiles
                            Log.d("CustomAdapterEntrant", "Fetched not_selected profile: " +
                                    "Profile ID: " + profileId +
                                    ", Event Name: " + document.getString("eventName") +
                                    ", Reconsider for Draw: " + reconsiderState);

                            // Set switch state and visibility
                            reconsiderSwitch.setVisibility(View.VISIBLE);
                            reconsiderSwitch.setChecked(reconsiderState);

                            // Add toggle listener
                            reconsiderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                Log.d("CustomAdapterEntrant", "Reconsider toggled for profile ID: " +
                                        profileId + " | New State: " + isChecked);

                                // Display a toast message
                                if (isChecked) {
                                    Toast.makeText(context, "Reconsider for draw enabled", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Reconsider for draw disabled", Toast.LENGTH_SHORT).show();
                                }

                                // Update Firestore
                                document.getReference().update("reconsiderForDraw", isChecked)
                                        .addOnSuccessListener(aVoid -> Log.d("CustomAdapterEntrant", "Updated reconsiderForDraw for document: " + profileId))
                                        .addOnFailureListener(e -> Log.e("CustomAdapterEntrant", "Error updating reconsiderForDraw: ", e));
                            });
                        }
                    } else {
                        Log.d("CustomAdapterEntrant", "No matching not_selected_entrant for event: " + event.getEventName());
                        reconsiderSwitch.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    reconsiderSwitch.setVisibility(View.GONE);
                    Log.e("CustomAdapterEntrant", "Error fetching not_selected_entrants for event: " + event.getEventName(), e);
                });


        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to leave the waitlist for this event?")
                    .setPositiveButton("Confirm", (dialog, which) -> {
                        // Query Firestore to find the document with the matching deviceId
                        db.collection("waitlisted_entrants")
                                .whereEqualTo("deviceId", deviceId) // Match the device ID
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                                        for (DocumentSnapshot document : task.getResult()) {
                                            // Get the events array
                                            List<Map<String, Object>> events = (List<Map<String, Object>>) document.get("events");

                                            if (events != null) {
                                                // Find the specific event to remove
                                                Map<String, Object> eventToRemove = null;
                                                for (Map<String, Object> eventMap : events) {
                                                    if (eventMap.containsKey("eventName") && eventMap.get("eventName").equals(event.getEventName())) {
                                                        eventToRemove = eventMap;
                                                        break;
                                                    }
                                                }

                                                if (eventToRemove != null) {
                                                    // Remove the event from the array
                                                    events.remove(eventToRemove);

                                                    // Update the document in Firestore
                                                    document.getReference().update("events", events)
                                                            .addOnSuccessListener(aVoid -> {
                                                                data.remove(position);
                                                                notifyDataSetChanged();
                                                                Toast.makeText(context, "Event removed from waitlist", Toast.LENGTH_SHORT).show();
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                Toast.makeText(context, "Failed to update waitlist", Toast.LENGTH_SHORT).show();
                                                                Log.e("Firestore", "Error updating document", e);
                                                            });
                                                } else {
                                                    Toast.makeText(context, "Event not found in waitlist", Toast.LENGTH_SHORT).show();
                                                    Log.w("Firestore", "Event not found in events array.");
                                                }
                                            } else {
                                                Log.w("Firestore", "No events array found.");
                                            }
                                        }
                                    } else {
                                        Toast.makeText(context, "No matching entrant found", Toast.LENGTH_SHORT).show();
                                        Log.w("Firestore", "No matching document found.");
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Error fetching waitlist entry", Toast.LENGTH_SHORT).show();
                                    Log.e("Firestore", "Error fetching document", e);
                                });
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        // Dismiss the dialog if the user cancels
                        dialog.dismiss();
                    })
                    .show();
        });

        return convertView;
    }
}
