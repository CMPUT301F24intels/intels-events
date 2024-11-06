package com.example.intels_app;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView clearAllButton;

    private static final String CHANNEL_ID = "notification_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);

        createNotificationChannel();

        backButton = findViewById(R.id.back_button);
        clearAllButton = findViewById(R.id.tvClearAll);

        // Set up back button to navigate back to main activity
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(NotificationActivity.this, MainPageActivity.class);
            startActivity(intent);
        });

        // Clear all notifications when 'Clear All' is clicked
        clearAllButton.setOnClickListener(view -> clearAllNotifications());

        // Setting up Accept and Decline buttons for notifications
        setupNotificationActions();
    }

    private void setupNotificationActions() {
        Button acceptButton1 = findViewById(R.id.accept_button_1);
        Button declineButton1 = findViewById(R.id.decline_button_1);

        acceptButton1.setOnClickListener(view -> handleAcceptNotification("Chair Sale"));
        declineButton1.setOnClickListener(view -> handleDeclineNotification("Chair Sale"));
    }

    private void handleAcceptNotification(String eventName) {
        Toast.makeText(this, "Accepted for " + eventName, Toast.LENGTH_SHORT).show();
    }

    private void handleDeclineNotification(String eventName) {
        Toast.makeText(this, "Declined for " + eventName, Toast.LENGTH_SHORT).show();
    }

    private void clearAllNotifications() {
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
                .setSmallIcon(R.drawable.cat) // testing
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }
}
