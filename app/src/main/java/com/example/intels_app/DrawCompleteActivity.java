package com.example.intels_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DrawCompleteActivity extends AppCompatActivity {

    private Button entrantsSelectedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_complete);

        entrantsSelectedButton = findViewById(R.id.entrants_selected_button);

        entrantsSelectedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to the WaitlistWithEntrantsActivity
                Intent intent = new Intent(DrawCompleteActivity.this, EntrantInWaitlist.class);
                intent.putExtra("eventId", getIntent().getStringExtra("eventId"));
                startActivity(intent);
            }
        });
    }
}
