package com.example.intels_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

//import com.example.intels_app.databinding.ActivityMainBinding;
//import com.google.firebase.firestore.FirebaseFirestore;

import android.widget.ImageButton;

//import com.google.zxing.integration.android.IntentIntegrator;
//import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {
    //private FirebaseFirestore db; *error out for me so commented out for now
    private AppBarConfiguration appBarConfiguration;
    //private ActivityMainBinding binding; *error out for me so commented out for now
    private QRCodeScanner qrCodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //db = FirebaseFirestore.getInstance(); *error out for me so commented out for now

        setContentView(R.layout.main_page);
        qrCodeScanner = new QRCodeScanner(this);

        // Change it to qr code scanner button ion UI later
        //qrCodeScanner.startScan();
        ImageButton ViewWaitListButton = findViewById(R.id.imageButton7);
        ViewWaitListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EventGridActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        qrCodeScanner.handleActivityResult(requestCode, resultCode, data);
    }
}