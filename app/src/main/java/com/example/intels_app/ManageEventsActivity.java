package com.example.intels_app;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
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
        ArrayList<Event> eventData = new ArrayList<>();

        Event event = new Event("Event 1", "Facility 1", "Location 1", "DateTime 1", "Description 1", 10, true, true);

        eventData.add(event);

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