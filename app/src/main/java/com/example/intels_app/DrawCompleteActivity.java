package com.example.intels_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

/**
 * This class represents the activity displayed when a draw for an event
 * is completed. This activity provides an option to view the selected entrants
 * for the specific event by navigating to the SelectedEntrantActivity.
 * @author Katrina ALejo
 * @see com.example.intels_app.SelectedEntrantActivity Selected entrants in draw
 */

public class DrawCompleteActivity extends AppCompatActivity {

    private Button entrantsSelectedButton;
    private String eventName;
    private LottieAnimationView animationView;

    /**
     * Initializes the layout, starts the completion animation, and sets up a button to navigate
     * to the list of entrants selected in the lottery.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_complete);

        eventName = getIntent().getStringExtra("eventName");

        if (eventName == null || eventName.isEmpty()) {
            finish();
            return;
        }

        animationView = findViewById(R.id.animation_view);
        animationView.setAnimation(R.raw.complete);
        animationView.playAnimation(); // Start the animation

        entrantsSelectedButton = findViewById(R.id.entrants_selected_button);
        entrantsSelectedButton.setOnClickListener(v -> {
            Intent intent = new Intent(DrawCompleteActivity.this, LotteryList.class);
            intent.putExtra("eventName", eventName);
            startActivity(intent);
        });
    }

    /**
     * Called when the activity is paused.
     * Pauses the animation to conserve resources.
     */
    @Override
    protected void onPause() {
        super.onPause();
        animationView.pauseAnimation();
    }

    /**
     * Called when the activity is resumed.
     * Resumes the animation when the activity becomes active.
     */
    @Override
    protected void onResume() {
        super.onResume();
        animationView.resumeAnimation();
    }
}

