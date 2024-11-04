package com.example.intels_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AdminEvents extends AppCompatActivity {
    ImageButton back_button;
    Button profile_button, events_button;
    GridView events_gridview;
    List<Event> list_event;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_view);

        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminEvents.this, AdminLogin.class);
                startActivity(intent);
            }
        });

        events_gridview = findViewById(R.id.events_gridview);
        list_event = new ArrayList<>();
        list_event.add(new Event("1", "Event 1"));
        list_event.add(new Event("2", "Event 2"));
        list_event.add(new Event("3", "Event 3"));
        list_event.add(new Event("4", "Event 4"));
        list_event.add(new Event("5", "Event 5"));
        CustomAdapterOrganizer adapter = new CustomAdapterOrganizer(this, list_event);
        events_gridview.setAdapter(adapter);

        profile_button = findViewById(R.id.profile_button);
        events_button = findViewById(R.id.events_button);

        events_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
        profile_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));

        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                events_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
                profile_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));

                Intent intent = new Intent(AdminEvents.this, AdminProfiles.class);
                startActivity(intent);
            }
        });

        events_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                events_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
                profile_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));

                Intent intent = new Intent(AdminEvents.this, AdminEvents.class);
                startActivity(intent);
            }
        });
    }
}
