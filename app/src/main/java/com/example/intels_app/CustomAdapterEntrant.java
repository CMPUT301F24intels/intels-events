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
                        db.collection("waitlisted_entrants")
                                .whereEqualTo("deviceId", deviceId)
                                .whereEqualTo("eventName", event.getEventName())
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                                        for (DocumentSnapshot doc : task.getResult()) {
                                            doc.getReference().delete()
                                                    .addOnSuccessListener(aVoid -> {
                                                        data.remove(position);
                                                        notifyDataSetChanged();
                                                        Toast.makeText(context, "Event removed from waitlist", Toast.LENGTH_SHORT).show();
                                                    })
                                                    .addOnFailureListener(e -> Log.e("Firestore", "Error deleting document", e));
                                        }
                                    } else {
                                        Log.d("Firestore", "No matching waitlisted entrant found.");
                                    }
                                });
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        return convertView;
    }
}
