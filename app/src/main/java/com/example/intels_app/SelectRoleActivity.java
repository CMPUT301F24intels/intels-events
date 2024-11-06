package com.example.intels_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class SelectRoleActivity extends AppCompatActivity {
    ImageButton backButton;
    Button join_as_entrant_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_role);

        // Set up back button
        backButton = findViewById(R.id.back_button_2);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectRoleActivity.this, JoinWaitlistActivity.class);
                startActivity(intent);
            }
        });
        join_as_entrant_button = findViewById(R.id.join_as_entrant);
        join_as_entrant_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectRoleActivity.this, SignUp.class);
                startActivity(intent);
            }
        });
    }
}
