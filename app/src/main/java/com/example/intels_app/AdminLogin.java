package com.example.intels_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminLogin extends AppCompatActivity {
    ImageButton back_button;
    EditText adminName, adminPassword;
    Button continue_button;

    private final String correctusername = "Intels";
    private final String correctpassword = "Intels2024";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_login);

        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminLogin.this, MainActivity.class);
                startActivity(intent);
            }
        });

        adminName = findViewById(R.id.nameEditText);
        adminPassword = findViewById(R.id.adminPasswordEditText);
        continue_button = findViewById(R.id.continue_button);

        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = adminName.getText().toString();
                String password = adminPassword.getText().toString();

                if (username.equals(correctusername) && password.equals(correctpassword)){
                    Toast.makeText(AdminLogin.this, "You are an admin.", Toast.LENGTH_SHORT).show();
                    //Intent intent = new Intent(AdminLogin.this, );
                    //startActivity(intent);
                }
                else {
                    Toast.makeText(AdminLogin.this, "Incorrect login. Please try again.", Toast.LENGTH_SHORT).show();
                    adminName.setText("");
                    adminPassword.setText("");
                }
            }
        });
    }
}
