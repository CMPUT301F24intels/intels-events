package com.example.intels_app;

import static android.content.ContentValues.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * This class displays a list of notifications for the user, with options
 * to accept or decline specific invitations when selected in lottery. It
 * retrieves notifications from Firebase Firestore and allows the user to
 * clear all notifications.
 * @author Katrina Alejo
 * @see com.example.intels_app.MainActivity Main screen of app
 * @see com.google.firebase.firestore.FirebaseFirestore Firebase
 */
public class NotificationActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView clearAllButton;
    private LinearLayout notificationListLayout;

    private static final String CHANNEL_ID = "notification_channel";
    private FirebaseFirestore db;

    // List to temporarily store notifications before adding them to the view
    private final List<Map<String, Object>> notificationCache = new ArrayList<>();
    private int loadedEventDetailsCount = 0;

    /**
     * Initializes views and loads notifications for the specific device.
     * @param savedInstanceState The saved state of the application.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);

        db = FirebaseFirestore.getInstance();

        FirebaseInstallations.getInstance().getId()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String deviceId = task.getResult();
                        Log.d("NotificationActivity", "Device ID: " + deviceId);

                        // Load notifications specific to this device
                        loadNotificationsFromFirestore(deviceId);
                    } else {
                        Log.e("NotificationActivity", "Unable to get device ID", task.getException());
                        Toast.makeText(this, "Failed to retrieve device ID", Toast.LENGTH_SHORT).show();
                    }
                });

        createNotificationChannel();

        backButton = findViewById(R.id.back_button);
        clearAllButton = findViewById(R.id.tvClearAll);
        notificationListLayout = findViewById(R.id.notificationListLayout);
        db = FirebaseFirestore.getInstance();

        // Set up back button to navigate back to main activity
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(NotificationActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Clear all notifications when 'Clear All' is clicked
        clearAllButton.setOnClickListener(view -> clearAllNotifications());


    }

    /**
     * Loads notifications from Firestore for the given device ID.
     * @param deviceId The unique identifier of the device.
     */
    private void loadNotificationsFromFirestore(String deviceId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notificationsRef = db.collection("notifications");

        notificationsRef.whereEqualTo("deviceId", deviceId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    List<DocumentSnapshot> notifications = querySnapshot.getDocuments();
                    loadedEventDetailsCount = 0; // Reset loaded counter
                    for (DocumentSnapshot notificationDoc : notifications) {
                        String eventName = notificationDoc.getString("eventName");
                        String message = notificationDoc.getString("message");
                        String type = notificationDoc.getString("type");
                        String profileId = notificationDoc.getString("profileId");

                        // Store notification data in the cache
                        Map<String, Object> notificationData = new HashMap<>();
                        notificationData.put("eventName", eventName);
                        notificationData.put("message", message);
                        notificationData.put("type", type);
                        notificationData.put("profileId", profileId);
                        notificationCache.add(notificationData);

                        // Load event details if eventName is available
                        if (eventName != null && !eventName.isEmpty()) {
                            loadEventDetailsForNotification(eventName, notificationData);
                        } else {
                            // No eventName, increment loaded counter
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

    /**
     * Loads additional event details for a specific notification such as the poster URL.
     * @param eventName The name of the event for which details are to be loaded.
     * @param notificationData The notification data map where event details are added.
     */
    private void loadEventDetailsForNotification(String eventName, Map<String, Object> notificationData) {
        DocumentReference eventRef = db.collection("events").document(eventName);
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

    /**
     * Checks if all event details have been loaded and displays all cached notifications in the UI.
     */
    private void checkAndDisplayNotifications() {
        if (loadedEventDetailsCount == notificationCache.size()) {
            for (Map<String, Object> notificationData : notificationCache) {
                String title = (String) notificationData.get("eventName");
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

    /**
     * Adds a notification to the notification list layout in the UI.
     * @param posterUrl The URL of the event poster image.
     * @param title The title of the notification (event name).
     * @param message The notification message.
     * @param type The type of notification (e.g., selected, accepted, declined).
     * @param profileId The profile ID related to the notification.
     */
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
        showSystemNotification(title, message);
    }

    /**
     * Handles the acceptance of a notification by updating the entrant status and notification message in Firestore.
     * @param profileId The profile ID of the entrant accepting the invitation.
     */
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

    /**
     * Handles the decline of a notification by updating the entrant status and notification message in Firestore.
     * @param profileId The profile ID of the entrant declining the invitation.
     */
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

    /**
     * Clears all notifications both from the UI and Firestore.
     */
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


    /**
     * Creates a notification channel for Android devices running API level 26 and above.
     */
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

    /**
     * Displays a system notification with the given title and message.
     * @param title The title of the notification.
     * @param message The message content of the notification.
     */
    private void showSystemNotification(String title, String message) {
        // Create an Intent to open NotificationActivity when the notification is clicked
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.message) // Use your app's notification icon
                .setContentTitle(title != null ? title : "New Notification")
                .setContentText(message != null ? message : "You have a new notification.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true) // Dismiss notification on tap
                .setContentIntent(pendingIntent); // Set the PendingIntent

        // Post the notification to the system
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }


}
