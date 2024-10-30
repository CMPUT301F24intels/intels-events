package com.example.intels_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class FinalList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_list);

        ListView entrantlist = findViewById(R.id.entrant_list);
        ArrayList<String> entrantDataList = new ArrayList<>();
        entrantDataList.add("Dhanshri");
        entrantDataList.add("Aayushi");
        entrantDataList.add("Janan");
        entrantDataList.add("Het");
        entrantDataList.add("Kanishka");
        entrantDataList.add("Katrina");

        CustomAdapterFinalList adapter = new CustomAdapterFinalList(this, entrantDataList);
        entrantlist.setAdapter(adapter);

        ImageButton backbutton = findViewById(R.id.back_button);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent (FinalList.this, )
                //startActivity(intent);
            }
        });
    }
}
