package com.example.profile_page;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button profileButton;     // define login button variable
    private Button jsonObjButton;        // define JSON button variable
    private Button jsonArrButton;        // define JSON button variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);             // link to Main activity XML

        // Initialize profile button
        profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(this);

        // Initialize JSON button
        jsonObjButton = findViewById(R.id.jsonObjButton);
        jsonObjButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the JSON button is clicked, navigate to JsonObjReqActivity
                Intent intent = new Intent(MainActivity.this, JsonObjReqActivity.class);
                startActivity(intent);
            }
        });

        // Initialize JSON Array button
        jsonArrButton = findViewById(R.id.jsonArrButton);
        jsonArrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the JSON Array button is clicked, navigate to JsonArrReqActivity
                Intent intent = new Intent(MainActivity.this, JsonArrReqActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.profileButton) {
            // Navigate to ProfileActivity when profile button is clicked
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
    }

}
