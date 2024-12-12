package com.example.androidexample;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.getIntent;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;


public class AdminActivity extends AppCompatActivity {

    private Button btnDeleteAccount, btnBanPlayer, btnJoinLobby, btnSpectateGame;
    private String adminUsername;  // Store the logged-in admin's username or ID
    private int adminUserId;
    private ImageView homeButton;
    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        requestQueue = Volley.newRequestQueue(this);

        // Retrieve stored user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginSession", MODE_PRIVATE);
        adminUserId = sharedPreferences.getInt("userId", -1);
        adminUsername = sharedPreferences.getString("username", "Guest");


        // Initialize buttons
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        btnBanPlayer = findViewById(R.id.btnBanPlayer);
        btnJoinLobby = findViewById(R.id.btnJoinLobby);
        btnSpectateGame = findViewById(R.id.btnSpectateGame);
        homeButton = findViewById(R.id.home_image_button);

        // Retrieve admin username or ID from login/session
        adminUsername = getIntent().getStringExtra("adminUsername");

        // Set button click listeners
        btnDeleteAccount.setOnClickListener(v -> showDeleteUserDialog());
        btnBanPlayer.setOnClickListener(v -> showBanUserDialog());
        btnJoinLobby.setOnClickListener(v -> joinLobby());
        // btnSpectateGame.setOnClickListener(v -> spectateGame());

        // Set click listeners for each button
        homeButton.setOnClickListener(v -> {
            // Create an Intent to navigate to MainActivity
            Intent intent = new Intent(AdminActivity.this, MainActivity.class);

            // Start the MainActivity
            startActivity(intent);
        });
    }

    private void showDeleteUserDialog() {
        // Create layout for the dialog with an EditText for userId
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText userIdInput = new EditText(this);
        userIdInput.setHint("Enter User ID");
        userIdInput.setInputType(InputType.TYPE_CLASS_NUMBER); // Only numbers allowed

        layout.addView(userIdInput);

        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Please enter the user ID to delete the user.")
                .setView(layout)
                .setPositiveButton("Delete", (dialog, which) -> {
                    String userIdStr = userIdInput.getText().toString().trim();
                    if (!userIdStr.isEmpty()) {
                        int userId = Integer.parseInt(userIdStr);
                        deleteUser(userId);
                    } else {
                        Toast.makeText(this, "User ID cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteUser(int userId) {
        String url = "http://10.90.72.226:8080/users/{adminId}/hardDelete/{id}";
        url = url.replace("{adminId}", String.valueOf(adminUserId))  // Replace with your adminId
                .replace("{id}", String.valueOf(userId));  // Replace with the target userId

        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    Toast.makeText(AdminActivity.this, "User deleted successfully!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Toast.makeText(AdminActivity.this, "Failed to delete user: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(deleteRequest);
    }

    private void showBanUserDialog() {
        // Create layout for the dialog with two EditTexts (one for username, one for ban length)
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText usernameInput = new EditText(this);
        usernameInput.setHint("Enter Username");

        EditText banLengthInput = new EditText(this);
        banLengthInput.setHint("Enter Ban Duration (minutes)");
        banLengthInput.setInputType(InputType.TYPE_CLASS_NUMBER); // Only numbers allowed

        layout.addView(usernameInput);
        layout.addView(banLengthInput);

        new AlertDialog.Builder(this)
                .setTitle("Ban User")
                .setMessage("Please enter the username and ban duration (in minutes) to ban a user.")
                .setView(layout)
                .setPositiveButton("Ban", (dialog, which) -> {
                    String username = usernameInput.getText().toString().trim();
                    String banLengthStr = banLengthInput.getText().toString().trim();
                    if (!username.isEmpty() && !banLengthStr.isEmpty()) {
                        int banLength = Integer.parseInt(banLengthStr);
                        banUser(username, banLength);
                    } else {
                        Toast.makeText(this, "Username and ban length cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void banUser(String username, int banLength) {
        String url = "http://10.90.72.226:8080/users/{adminId}/ban/{username}/length/{minuteLength}";
        url = url.replace("{adminId}", String.valueOf(adminUserId))  // Replace with your adminId
                .replace("{username}", username)  // Replace with the target username
                .replace("{minuteLength}", String.valueOf(banLength));  // Replace with the ban duration

        StringRequest banRequest = new StringRequest(Request.Method.PUT, url,
                response -> {
                    Toast.makeText(AdminActivity.this, "User banned successfully!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Toast.makeText(AdminActivity.this, "Failed to ban user: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(banRequest);
    }

    private void joinLobby() {
        // Logic to join a lobby
        // You can check if the lobby has space for the admin and other players
        // Update the backend as needed and navigate to the lobby screen
        Intent intent = new Intent(AdminActivity.this, PreGameActivity.class);
        startActivity(intent);
    }

    /**
    private void spectateGame() {
        // Logic to allow the admin to spectate a game
        // You might need to show a list of active games and let the admin pick one to spectate
        Intent intent = new Intent(AdminActivity.this, SpectateGameActivity.class);
        startActivity(intent);
    }
     */
}
