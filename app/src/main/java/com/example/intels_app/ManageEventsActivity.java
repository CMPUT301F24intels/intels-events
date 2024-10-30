package com.example.intels_app;

import android.os.Bundle;
import android.widget.GridView;
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
        List<String> eventData = new ArrayList<>();
        eventData.add("Event 1");
        eventData.add("Event 2");
        eventData.add("Event 3");
        eventData.add("Event 4");
        eventData.add("Event 5");


        CustomAdapter adapter = new CustomAdapter(this, eventData);
        grid.setAdapter(adapter);
    }
}
