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

public class NotificationActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView clearAllButton;
    private LinearLayout notificationListLayout;

    private static final String CHANNEL_ID = "notification_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);

        createNotificationChannel();

        backButton = findViewById(R.id.back_button);
        clearAllButton = findViewById(R.id.tvClearAll);
        notificationListLayout = findViewById(R.id.notificationListLayout);

        // Set up back button to navigate back to main activity
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(NotificationActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Clear all notifications when 'Clear All' is clicked
        clearAllButton.setOnClickListener(view -> clearAllNotifications());

        // Add sample notifications
        addNotification("Chair Sale", "You Have Been Selected.");
        addNotification("Cat Competition", "You have not been selected in first draw.");
        addNotification("Poster Sale", "You Have Been Selected.");
    }

    private void addNotification(String title, String message) {
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

        // Handle accept button click
        acceptButton.setOnClickListener(view -> {
            handleAcceptNotification(title);
            acceptButton.setVisibility(View.GONE);
            declineButton.setVisibility(View.GONE);
            notificationMessage.setText("You have accepted the invitation.");
        });

        // Handle decline button click
        declineButton.setOnClickListener(view -> {
            handleDeclineNotification(title);
            acceptButton.setVisibility(View.GONE);
            declineButton.setVisibility(View.GONE);
            notificationMessage.setText("You have declined the invitation.");
        });

        // Add the notification view to the notification list layout
        notificationListLayout.addView(notificationView);
    }

    private void handleAcceptNotification(String eventName) {
        Toast.makeText(this, "Accepted for " + eventName, Toast.LENGTH_SHORT).show();
    }

    private void handleDeclineNotification(String eventName) {
        Toast.makeText(this, "Declined for " + eventName, Toast.LENGTH_SHORT).show();
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

    @SuppressLint("MissingPermission")
    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.cat)  // Testing with the 'cat' drawable
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }
}
