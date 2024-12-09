package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class Welcome extends AppCompatActivity {

    // Declare buttons
    private ImageButton playButton;
    private Button infoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome); // Assuming this is your layout file

        // Initialize the buttons
        playButton = findViewById(R.id.playButton);
        infoButton = findViewById(R.id.infoButton);

        // Set up play button click listener
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Login Activity when the play button is clicked
                Intent intent = new Intent(Welcome.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Set up small button click listener
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Authors Activity when the small button is clicked
                Intent intent = new Intent(Welcome.this, AuthorsActivity.class);
                startActivity(intent);
            }
        });
    }
}
