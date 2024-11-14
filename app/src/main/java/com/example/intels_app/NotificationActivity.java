/**
 * This class displays a list of notifications for the user, with options
 * to accept or decline specific invitations when selected in lottery. It
 * retrieves notifications from Firebase Firestore and allows the user to
 * clear all notifications.
 * @author Katrina Alejo
 * @see com.example.intels_app.MainPageActivity Main screen of app
 * @see com.google.firebase.firestore.FirebaseFirestore Firebase
 *
 */

package com.example.intels_app;

import static android.content.ContentValues.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView clearAllButton;
    private LinearLayout notificationListLayout;

    private static final String CHANNEL_ID = "notification_channel";
    private FirebaseFirestore db;

    // List to temporarily store notifications before adding them to the view
    private final List<Map<String, Object>> notificationCache = new ArrayList<>();
    private int loadedEventDetailsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);

        createNotificationChannel();

        backButton = findViewById(R.id.back_button);
        clearAllButton = findViewById(R.id.tvClearAll);
        notificationListLayout = findViewById(R.id.notificationListLayout);
        db = FirebaseFirestore.getInstance();

        // Set up back button to navigate back to main activity
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(NotificationActivity.this, MainPageActivity.class);
            startActivity(intent);
        });

        // Clear all notifications when 'Clear All' is clicked
        clearAllButton.setOnClickListener(view -> clearAllNotifications());

        // Load notifications from Firestore
        loadNotificationsFromFirestore();
    }

    private void loadNotificationsFromFirestore() {
        CollectionReference notificationsRef = db.collection("notifications");
        notificationsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    List<DocumentSnapshot> notifications = querySnapshot.getDocuments();
                    loadedEventDetailsCount = 0; // Reset loaded counter
                    for (DocumentSnapshot notificationDoc : notifications) {
                        String eventId = notificationDoc.getString("eventId");
                        String message = notificationDoc.getString("message");
                        String type = notificationDoc.getString("type");
                        String profileId = notificationDoc.getString("profileId");

                        // Store notification data in the cache
                        Map<String, Object> notificationData = new HashMap<>();
                        notificationData.put("eventId", eventId);
                        notificationData.put("message", message);
                        notificationData.put("type", type);
                        notificationData.put("profileId", profileId);
                        notificationCache.add(notificationData);

                        // Load event details if eventId is available
                        if (eventId != null && !eventId.isEmpty()) {
                            loadEventDetailsForNotification(eventId, notificationData);
                        } else {
                            // No eventId, increment loaded counter
                            loadedEventDetailsCount++;
                            checkAndDisplayNotifications();
                        }
                    }
                } else {
                    Toast.makeText(NotificationActivity.this, "No notifications found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(NotificationActivity.this, "Failed to load notifications.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadEventDetailsForNotification(String eventId, Map<String, Object> notificationData) {
        DocumentReference eventRef = db.collection("events").document(eventId);
        eventRef.get().addOnSuccessListener(eventDoc -> {
            String posterUrl = eventDoc.getString("posterUrl");
            notificationData.put("posterUrl", posterUrl);

            // Increment loaded counter
            loadedEventDetailsCount++;
            checkAndDisplayNotifications();

        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error loading event details for notification", e);

            // Increment loaded counter even on failure
            loadedEventDetailsCount++;
            checkAndDisplayNotifications();
        });
    }

    private void checkAndDisplayNotifications() {
        if (loadedEventDetailsCount == notificationCache.size()) {
            for (Map<String, Object> notificationData : notificationCache) {
                String title = (String) notificationData.get("eventId");
                String message = (String) notificationData.get("message");
                String type = (String) notificationData.get("type");
                String profileId = (String) notificationData.get("profileId");
                String posterUrl = (String) notificationData.get("posterUrl");

                // Display the correct message based on the type
                if ("declined".equals(type)) {
                    message = "You have declined the invitation.";
                } else if ("accepted".equals(type)) {
                    message = "You have accepted the invitation.";
                }

                addNotification(posterUrl, title, message, type, profileId);
            }
            notificationCache.clear();
        }
    }

    private void addNotification(String posterUrl, String title, String message, String type, String profileId) {
        // Inflate the notification item layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View notificationView = inflater.inflate(R.layout.notification_item, null);

        // Set up notification view details
        TextView notificationTitle = notificationView.findViewById(R.id.notification_title);
        TextView notificationMessage = notificationView.findViewById(R.id.notification_message);
        ImageView posterImageView = notificationView.findViewById(R.id.profile_image);
        Button acceptButton = notificationView.findViewById(R.id.accept_button);
        Button declineButton = notificationView.findViewById(R.id.decline_button);

        notificationTitle.setText(title != null ? title : "Unknown Event");
        notificationMessage.setText(message);

        if (posterUrl != null && !posterUrl.isEmpty()) {
            Glide.with(this)
                    .load(posterUrl)
                    .placeholder(R.drawable.pfp_placeholder_image)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(posterImageView);
        } else {
            posterImageView.setImageResource(R.drawable.message);
        }

        // Show accept and decline buttons only if the type is "selected"
        if ("selected".equals(type)) {
            acceptButton.setVisibility(View.VISIBLE);
            declineButton.setVisibility(View.VISIBLE);
        } else {
            acceptButton.setVisibility(View.GONE);
            declineButton.setVisibility(View.GONE);
        }

        // Handle accept button click
        acceptButton.setOnClickListener(view -> {
            handleAcceptNotification(profileId);
            acceptButton.setVisibility(View.GONE);
            declineButton.setVisibility(View.GONE);
            notificationMessage.setText("You have accepted the invitation.");
        });

        // Handle decline button click
        declineButton.setOnClickListener(view -> {
            handleDeclineNotification(profileId);
            acceptButton.setVisibility(View.GONE);
            declineButton.setVisibility(View.GONE);
            notificationMessage.setText("You have declined the invitation.");
        });

        // Set margins programmatically for added view
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 16, 0, 24); // Set top, left, right, bottom margins for extra spacing
        notificationView.setLayoutParams(params);

        // Add the notification view to the notification list layout
        notificationListLayout.addView(notificationView);
    }

    private void handleAcceptNotification(String profileId) {
        if (profileId == null || profileId.trim().isEmpty()) {
            Toast.makeText(NotificationActivity.this, "Profile ID is invalid or empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference entrantDocRef = db.collection("waitlisted_entrants").document(profileId);
        entrantDocRef.update("status", "accepted")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(NotificationActivity.this, "Entrant status updated to 'accepted'", Toast.LENGTH_SHORT).show();
                    Log.d("NotificationActivity", "Entrant status successfully updated for ID: " + profileId);

                    // Retrieve the event ID for this notification from the notification list
                    CollectionReference notificationsRef = db.collection("notifications");
                    notificationsRef.whereEqualTo("profileId", profileId)
                            .get()
                            .addOnSuccessListener(querySnapshot -> {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    // Update the notification message and type for accepted invitations
                                    document.getReference().update("message", "You have accepted the invitation.", "type", "accepted")
                                            .addOnSuccessListener(unused -> Log.d("NotificationActivity", "Notification message updated for ID: " + document.getId()))
                                            .addOnFailureListener(e -> Log.w("NotificationActivity", "Failed to update notification message", e));
                                }
                            })
                            .addOnFailureListener(e -> Log.w("NotificationActivity", "Failed to fetch notifications for profile ID: " + profileId, e));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(NotificationActivity.this, "Failed to update entrant status", Toast.LENGTH_SHORT).show();
                    Log.w("NotificationActivity", "Failed to update entrant status for ID: " + profileId, e);
                });
    }

    private void handleDeclineNotification(String profileId) {
        DocumentReference entrantDocRef = db.collection("waitlisted_entrants").document(profileId);
        entrantDocRef.update("status", "cancelled")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(NotificationActivity.this, "Entrant status updated to 'cancelled'", Toast.LENGTH_SHORT).show();
                    Log.d("NotificationActivity", "Entrant status successfully updated for ID: " + profileId);

                    // Retrieve the event ID for this notification from the notification list
                    CollectionReference notificationsRef = db.collection("notifications");
                    notificationsRef.whereEqualTo("profileId", profileId)
                            .get()
                            .addOnSuccessListener(querySnapshot -> {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    // Update the notification message and type for declined invitations
                                    document.getReference().update("message", "You have declined the invitation.", "type", "declined")
                                            .addOnSuccessListener(unused -> Log.d("NotificationActivity", "Notification message updated for ID: " + document.getId()))
                                            .addOnFailureListener(e -> Log.w("NotificationActivity", "Failed to update notification message", e));
                                }
                            })
                            .addOnFailureListener(e -> Log.w("NotificationActivity", "Failed to fetch notifications for profile ID: " + profileId, e));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(NotificationActivity.this, "Failed to update entrant status", Toast.LENGTH_SHORT).show();
                    Log.w("NotificationActivity", "Failed to update entrant status for ID: " + profileId, e);
                });
    }

    private void clearAllNotifications() {
        // Remove all views from the notification layout in the UI
        notificationListLayout.removeAllViews();
        NotificationManagerCompat.from(this).cancelAll();

        // Clear all notifications from Firestore
        CollectionReference notificationsRef = db.collection("notifications");
        notificationsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                WriteBatch batch = db.batch();
                for (DocumentSnapshot doc : task.getResult()) {
                    batch.delete(doc.getReference());
                }

                // Commit the batch delete to remove all notifications in Firestore
                batch.commit().addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "All notifications cleared from Firestore", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to clear notifications from Firestore", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error clearing notifications from Firestore", e);
                });
            } else {
                Toast.makeText(this, "No notifications found to clear", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load notifications for clearing", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error fetching notifications for clearing", e);
        });
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification Channel";
            String description = "Channel for app notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
