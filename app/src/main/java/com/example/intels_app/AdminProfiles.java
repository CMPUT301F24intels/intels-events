package com.example.intels_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AdminProfiles extends AppCompatActivity {
    ImageButton back_button;
    Button profile_button, events_button;
    ListView profile_list;
    private List<Profile> profileList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);

        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminProfiles.this, AdminLogin.class);
                startActivity(intent);
            }
        });

        profile_list = findViewById(R.id.profile_list);
        profileList = new ArrayList<>();
        profileList.add(new Profile("Spongebob", R.drawable.spongebob));
        profileList.add(new Profile("Patrick", R.drawable.patrick));
        profileList.add(new Profile("Squidward", R.drawable.squidward));

        ProfileAdapter adapter = new ProfileAdapter(this, profileList);
        profile_list.setAdapter(adapter);

        profile_button = findViewById(R.id.profile_button);
        events_button = findViewById(R.id.events_button);

        profile_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
        events_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));

        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                events_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
                profile_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));

                Intent intent = new Intent(AdminProfiles.this, AdminProfiles.class);
                startActivity(intent);
            }
        });

        events_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                events_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
                profile_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));

                Intent intent = new Intent(AdminProfiles.this, AdminEvents.class);
                startActivity(intent);
            }
        });
    }
}
