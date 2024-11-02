package com.example.intels_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class FinalList extends AppCompatActivity {
    private ListView entrantList;
    private List<Profile> entrantDataList;
    private ImageButton back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_list);

        entrantList = findViewById(R.id.entrant_list);

        entrantDataList = new ArrayList<>();
        entrantDataList.add(new Profile("Dhanshri", R.drawable.cat));
        entrantDataList.add(new Profile("Aayushii", R.drawable.bean));

        ProfileAdapter adapter = new ProfileAdapter(this, entrantDataList);
        entrantList.setAdapter(adapter);

        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (FinalList.this, EntrantInWaitlist.class);
                startActivity(intent);
            }
        });
    }
}
