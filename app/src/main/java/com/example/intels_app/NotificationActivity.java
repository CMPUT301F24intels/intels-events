package com.example.intels_app;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView clearAllButton;
    private LinearLayout notificationListLayout;

    private static final String CHANNEL_ID = "notification_channel";
    private FirebaseFirestore db;

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
                    for (DocumentSnapshot notificationDoc : notifications) {
                        String title = notificationDoc.getString("eventId");
                        String message = notificationDoc.getString("message");
                        String type = notificationDoc.getString("type");
                        String profileId = notificationDoc.getString("profileId");

                        addNotification(title, message, type, profileId);
                    }
                } else {
                    Toast.makeText(NotificationActivity.this, "No notifications found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(NotificationActivity.this, "Failed to load notifications.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addNotification(String title, String message, String type, String profileId) {
        // Inflate the notification item layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View notificationView = inflater.inflate(R.layout.notification_item, null);

        // Set up notification view details
        TextView notificationTitle = notificationView.findViewById(R.id.notification_title);
        TextView notificationMessage = notificationView.findViewById(R.id.notification_message);
        Button acceptButton = notificationView.findViewById(R.id.accept_button);
        Button declineButton = notificationView.findViewById(R.id.decline_button);

        notificationTitle.setText(title);
        notificationMessage.setText(message);

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
            handleAcceptNotification(title);
            acceptButton.setVisibility(View.GONE);
            declineButton.setVisibility(View.GONE);
            notificationMessage.setText("You have accepted the invitation.");
        });

        // Handle decline button click
        declineButton.setOnClickListener(view -> {
            handleDeclineNotification(profileId);  // Pass the profileId to handleDeclineNotification()
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

    private void handleAcceptNotification(String eventName) {
        Toast.makeText(this, "Accepted for " + eventName, Toast.LENGTH_SHORT).show();
    }

    private void handleDeclineNotification(String profileId) {
        // Update the profile status to "cancelled" in Firestore
        db.collection("profiles").document(profileId)
                .update("status", "cancelled")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(NotificationActivity.this, "Profile status updated to 'cancelled'", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(NotificationActivity.this, "Failed to update profile status", Toast.LENGTH_SHORT).show();
                });
    }

    private void clearAllNotifications() {
        notificationListLayout.removeAllViews();
        NotificationManagerCompat.from(this).cancelAll();
        Toast.makeText(this, "All notifications cleared", Toast.LENGTH_SHORT).show();
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
