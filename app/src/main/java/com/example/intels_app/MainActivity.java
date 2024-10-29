package com.example.intels_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuInflater;
import android.view.View;

//import com.example.intels_app.databinding.ActivityMainBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import android.view.MenuItem;
import android.widget.ImageButton;

import android.widget.PopupMenu;


public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private AppBarConfiguration appBarConfiguration;
    //private ActivityMainBinding binding;
    private QRCodeScanner qrCodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

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
                Intent intent = new Intent(MainActivity.this, EventGridOrganizerActivity.class);
                startActivity(intent);
            }
        });

        // Set up the Join Events button to navigate to ScanQRActivity
        ImageButton joinEventButton = findViewById(R.id.joinEventButton);
        joinEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ScanQRActivity.class);
                startActivity(intent);
            }
        });
    }

    // Method to display the popup menu
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
                }
                return false;
            }
        });

        // Show the popup menu
        popupMenu.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (qrCodeScanner != null) {
            qrCodeScanner.handleActivityResult(requestCode, resultCode, data);
        }
    }
}