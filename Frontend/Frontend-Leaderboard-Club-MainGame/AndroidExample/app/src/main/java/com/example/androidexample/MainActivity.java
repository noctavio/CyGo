package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button LeaderboardBtn;
    private Button ClubBtn;
    private Button GameBtn;
    private Button SettingsBtn;  // Declare SettingsBtn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the buttons
        LeaderboardBtn = findViewById(R.id.LeaderboardBtn);
        ClubBtn = findViewById(R.id.ClubBtn);
        GameBtn = findViewById(R.id.GameBtn);
        SettingsBtn = findViewById(R.id.SettingsBtn);  // Initialize SettingsBtn

        // Set click listeners
        ClubBtn.setOnClickListener(this);
        LeaderboardBtn.setOnClickListener(this);
        GameBtn.setOnClickListener(this);
        SettingsBtn.setOnClickListener(this);  // Set click listener for SettingsBtn
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.LeaderboardBtn) {
            Intent intent = new Intent(MainActivity.this, Leaderboard.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.ClubBtn) {
            Intent intent = new Intent(MainActivity.this, Club.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.GameBtn) {
            Intent intent = new Intent(MainActivity.this, PreGameActivity.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.SettingsBtn) {
            Intent intent = new Intent(MainActivity.this, Settings.class);
            startActivity(intent);
        }
    }
}
