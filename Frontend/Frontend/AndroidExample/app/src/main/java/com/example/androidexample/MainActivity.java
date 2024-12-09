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
    private Button SettingsBtn;
    private Button ClubChatBtn;
    private Button RulesBtn;
    private Button LoginBtn;
    private Button WelcomeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the buttons
        LeaderboardBtn = findViewById(R.id.LeaderboardBtn);
        ClubBtn = findViewById(R.id.ClubBtn);
        GameBtn = findViewById(R.id.GameBtn);
        SettingsBtn = findViewById(R.id.SettingsBtn);  // Initialize SettingsBtn
        ClubChatBtn = findViewById(R.id.ClubChatBtn);
        RulesBtn = findViewById(R.id.Rules);
        LoginBtn = findViewById(R.id.Login);
        WelcomeBtn = findViewById(R.id.Welcome);

        // Set click listeners
        ClubBtn.setOnClickListener(this);
        LeaderboardBtn.setOnClickListener(this);
        GameBtn.setOnClickListener(this);
        SettingsBtn.setOnClickListener(this);
        ClubChatBtn.setOnClickListener(this);
        RulesBtn.setOnClickListener(this);
        LoginBtn.setOnClickListener(this);
        WelcomeBtn.setOnClickListener(this);
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
        else if (view.getId() == R.id.ClubChatBtn) {
            Intent intent = new Intent(MainActivity.this, ClubChatActivity.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.Rules) {
            Intent intent = new Intent(MainActivity.this, RulesActivity.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.Login) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.Welcome) {
            Intent intent = new Intent(MainActivity.this, Welcome.class);
            startActivity(intent);
        }
    }
}
