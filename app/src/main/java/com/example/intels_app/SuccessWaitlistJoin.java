package com.example.intels_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class SuccessWaitlistJoin extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.success_waitlist_join); // Replace with your actual layout file name

        // Initialize buttons
        Button viewMyWaitlistButton = findViewById(R.id.ViewMyWaitlist_Button);
        Button homePageButton = findViewById(R.id.HomePage_Button);
        ImageButton backButton = findViewById(R.id.backButton);

        // Set OnClickListener for "View My Waitlists" button
        viewMyWaitlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuccessWaitlistJoin.this, EventGridEntrantActivity.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for "Home" button
        homePageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuccessWaitlistJoin.this, MainPageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SuccessWaitlistJoin.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

}
