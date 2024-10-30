package com.example.intels_app;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ManageEventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_events);

        // Initialize the GridView and set the adapter
        GridView grid = findViewById(R.id.gridViewEvents);
        ArrayList<String> eventData = new ArrayList<>();
        eventData.add("Event 1");
        eventData.add("Event 2");
        eventData.add("Event 3");
        eventData.add("Event 4");
        eventData.add("Event 5");

        CustomAdapterManageEvents eventsAdapter = new CustomAdapterManageEvents(this, eventData);
        grid.setAdapter(eventsAdapter);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageEventsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ImageButton addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageEventsActivity.this, AddEvent.class);
                startActivity(intent);
            }
        });
    }
}