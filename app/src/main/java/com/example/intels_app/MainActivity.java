package com.example.intels_app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.ui.AppBarConfiguration;

import android.view.MenuInflater;
import android.view.View;

//import com.example.intels_app.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;
//import com.google.firebase.auth.FirebaseAuth;
import com.example.intels_app.CreateFacility;
import com.example.intels_app.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.view.MenuItem;
import android.widget.ImageButton;

import android.widget.PopupMenu;
import android.Manifest;

import java.util.ArrayList;

/**
 * MainActivity serves as the main dashboard of the application, providing navigation to various
 * sections including event management, joining events, and administrative functions. It also includes
 * a popup menu for additional options like viewing notifications and accessing user settings.
 *
 * This activity initializes Firebase and sets up click listeners for key buttons that navigate to
 * different parts of the app.
 * Author: Dhanshri Patel, Janan Panchal, Aayushi Shah, Katrina Alejo, Het Patel, Kanishka Aswani
 */

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    //private FirebaseAuth fAuth;
    private AppBarConfiguration appBarConfiguration;
    private StorageReference storageRef;
    //private ActivityMainBinding binding;
    private QRCodeScanner qrCodeScanner;
    private static final String CHANNEL_ID = "default_channel";
    private static final int NOTIFICATION_ID = 1;
    private String textTitle = "Sample Notification";
    private String textContent = "This is a test notification.";

    /**
     * Called when the activity is first created.
     * Attempts to retrieve the Firebase Device ID, and uses it to
     * check if the user's profile exists.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down, this Bundle contains the most recent data.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        db = FirebaseFirestore.getInstance();
        //fAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        setContentView(R.layout.main_page);
        qrCodeScanner = new QRCodeScanner(this);

        ImageButton optionsButton = findViewById(R.id.imageButton8);
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        // Change it to qr code scanner button ion UI later
        //qrCodeScanner.startScan();
        ImageButton ViewWaitListButton = findViewById(R.id.imageButton7);
        ViewWaitListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EventGridEntrantActivity.class);
                startActivity(intent);
            }
        });

        // Set up the Join Events button to navigate to ScanQRActivity
        ImageButton joinEventButton = findViewById(R.id.joinEventButton);
        joinEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ScanQRActivity.class); //Change back to ScanQRActivity
                startActivity(intent);
            }
        });

        ImageButton manageApp = findViewById(R.id.manageAppButton);
        manageApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AdminLogin.class);
                startActivity(intent);
            }
        });

        ImageButton manageEventsButton = findViewById(R.id.manageEventsButton);
        manageEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ManageEventsActivity.class);
                startActivity(intent);
            }
        });

        createNotificationChannel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }

        /*Button testNotificationButton = findViewById(R.id.testNotificationButton);
        testNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotification();
            }
        });*/

    }
    /**
     * Shows a popup menu when the specified view is clicked.
     * The menu contains options to navigate to the NotificationActivity or EditProfileActivity.
     * @param view The view that triggers the popup menu.
     */
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_main, popupMenu.getMenu());

        // Handle menu item clicks
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_show_notifications) {
                    // Navigate to NotificationActivity
                    Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.action_settings) {
                    Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        // Show the popup menu
        popupMenu.show();
    }

    /**
     * Handles the result from a started activity of the QR code scanner.
     * @param requestCode The request code passed to the activity.
     * @param resultCode  The result code returned by the activity.
     * @param data        An Intent containing any returned data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (qrCodeScanner != null) {
            qrCodeScanner.handleActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Handles the result of a permission request.
     * Specifically handles notification permission requests, showing a toast message indicating success or failure.
     * @param requestCode  The request code passed to the permission request.
     * @param permissions  The requested permissions.
     * @param grantResults The results of the permission requests.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Creates a notification channel to support notifications on Android versions O and above.
     * The channel is used for general notifications with default importance.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Default Channel";
            String description = "Channel for general notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Displays a notification with a title, content, and an intent to open NotificationActivity.
     * The notification has a pending intent that navigates the user to NotificationActivity when clicked.
     */
    private void showNotification() {
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.message)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}