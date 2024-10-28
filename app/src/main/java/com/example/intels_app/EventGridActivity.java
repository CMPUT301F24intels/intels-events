package com.example.intels_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class EventGridActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_with_grid);

        // Initialize the GridView and set the adapter
        GridView gridView = findViewById(R.id.grid_view);
        List<String> eventData = new ArrayList<>();
        eventData.add("Event 1");
        eventData.add("Event 2");
        eventData.add("Event 3");
        eventData.add("Event 4");


        CustomAdapter adapter = new CustomAdapter(this, eventData);
        gridView.setAdapter(adapter);
    }
}