package com.example.intels_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class EntrantInWaitlist extends AppCompatActivity {
    private Button waitlist_button, cancelled_button;
    private ListView listView;
    private List<Profile> profileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waitlist_with_entrants);

        listView = findViewById(R.id.profile_list);

        profileList = new ArrayList<>();
        profileList.add(new Profile("Gopi Modi", R.drawable.gopimodi));
        profileList.add(new Profile("Mr.Bean", R.drawable.bean));

        ProfileAdapter adapter = new ProfileAdapter(this, profileList);
        listView.setAdapter(adapter);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // This will close the current activity and return to the previous one
            }
        });

        waitlist_button = findViewById(R.id.btn_waitlist);
        cancelled_button = findViewById(R.id.btn_cancelled);

        cancelled_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
        waitlist_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));

        waitlist_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelled_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));
                waitlist_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));

                Intent intent = new Intent(EntrantInWaitlist.this, EntrantInWaitlist.class);
                startActivity(intent);
            }
        });

        cancelled_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelled_button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_color));
                waitlist_button.setBackgroundTintList(getResources().getColorStateList(R.color.default_color));

                Intent intent = new Intent(EntrantInWaitlist.this, EntrantInCancelledWaitlist.class);
                startActivity(intent);
            }
        });
    }
}
