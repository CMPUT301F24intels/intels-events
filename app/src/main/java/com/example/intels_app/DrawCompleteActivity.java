/**
 * This class represents the activity displayed when a draw for an event
 * is completed. This activity provides an option to view the selected entrants
 * for the specific event by navigating to the SelectedEntrantActivity.
 * @author Katrina ALejo
 * @see com.example.intels_app.SelectedEntrantActivity Selected entrants in draw
 */

package com.example.intels_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DrawCompleteActivity extends AppCompatActivity {

    private Button entrantsSelectedButton;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_complete);

        eventId = getIntent().getStringExtra("eventId");

        if (eventId == null || eventId.isEmpty()) {
            finish();
            return;
        }

        entrantsSelectedButton = findViewById(R.id.entrants_selected_button);

        entrantsSelectedButton.setOnClickListener(v -> {
            Intent intent = new Intent(DrawCompleteActivity.this, SelectedEntrantActivity.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
        });
    }
}
