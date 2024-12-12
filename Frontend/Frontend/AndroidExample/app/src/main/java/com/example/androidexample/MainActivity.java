package com.example.androidexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button LeaderboardBtn;
    private Button ClubBtn;
    private Button GameBtn;
    private Button SettingsBtn;
    private Button ClubChatBtn;
    private Button RulesBtn;
    private Button LoginBtn;
    private Button WelcomeBtn;
    private Button JosekiBtn;
    private Button LogoutBtn;
    private Button PreGameBtn;
    private Button FriendsBtn;
    private Button ProfileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the buttons
        LeaderboardBtn = findViewById(R.id.LeaderboardBtn);
        ClubBtn = findViewById(R.id.ClubBtn);
        GameBtn = findViewById(R.id.GameBtn);
        SettingsBtn = findViewById(R.id.SettingsBtn);
        ClubChatBtn = findViewById(R.id.ClubChatBtn);
        RulesBtn = findViewById(R.id.Rules);
        LoginBtn = findViewById(R.id.LoginBtn);
        WelcomeBtn = findViewById(R.id.Welcome);
        JosekiBtn = findViewById(R.id.Joseki);
        LogoutBtn = findViewById(R.id.LogoutBtn);
        PreGameBtn = findViewById(R.id.PreGameBtn);
        FriendsBtn = findViewById(R.id.FriendsBtn);
        ProfileBtn = findViewById(R.id.ProfileBtn);

        // Set click listeners
        ClubBtn.setOnClickListener(this);
        LeaderboardBtn.setOnClickListener(this);
        GameBtn.setOnClickListener(this);
        SettingsBtn.setOnClickListener(this);
        ClubChatBtn.setOnClickListener(this);
        RulesBtn.setOnClickListener(this);
        LoginBtn.setOnClickListener(this);
        WelcomeBtn.setOnClickListener(this);
        JosekiBtn.setOnClickListener(this);
        PreGameBtn.setOnClickListener(this);
        FriendsBtn.setOnClickListener(this);
        ProfileBtn.setOnClickListener(this);

        // Set onClickListener for logout
        LogoutBtn.setOnClickListener(view -> logoutUser());

        displayUsername();
    }

    private void logoutUser() {
        // Clear the login session in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();  // Clear all saved session data
        editor.apply();

        // Show a Toast message for feedback
        Toast.makeText(MainActivity.this, "Logged out successfully!", Toast.LENGTH_SHORT).show();

        // Navigate to the Welcome screen
        Intent intent = new Intent(MainActivity.this, Welcome.class);
        startActivity(intent);
        finish();  // Close the current activity so that the user cannot go back to it
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
        else if (view.getId() == R.id.LoginBtn) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.Welcome) {
            Intent intent = new Intent(MainActivity.this, Welcome.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.Joseki) {
            Intent intent = new Intent(MainActivity.this, Joseki.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.FriendsBtn) {
            Intent intent = new Intent(MainActivity.this, FriendsActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.PreGameBtn) {
            Intent intent = new Intent(MainActivity.this, PreGameActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.ProfileBtn) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
    }

    private void displayUsername() {
        // Retrieve the username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginSession", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Guest");

        // Display the username at the top of the screen
        TextView usernameDisplay = findViewById(R.id.userNameDisplay);
        usernameDisplay.setText("Welcome, " + username + "!");
    }
}
