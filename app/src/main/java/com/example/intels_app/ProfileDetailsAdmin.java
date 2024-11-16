package com.example.intels_app;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileDetailsAdmin extends AppCompatActivity {

    private ImageView profile_pic;
    private EditText name, email, phone_number;
    private ImageButton back_button;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_details_admin);
    }
}
