package com.example.androidexample;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

// Activity that displays the profile information of a user.
public class ProfileActivity extends AppCompatActivity {
    private TextView profileName, rank, gamesNumber, winsNumber, lossesNumber;
    private RequestQueue requestQueue;
    private int storedUserId; // To store the fetched user ID
    // private String username; //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        profileName = findViewById(R.id.profile_name);
        gamesNumber = findViewById(R.id.games_number);
        winsNumber = findViewById(R.id.wins_number);
        lossesNumber = findViewById(R.id.loss_number);
        rank = findViewById(R.id.rank);

        ImageButton homeButton = findViewById(R.id.home_image_button);
        Button updateButton = findViewById(R.id.update_button);
        Button refreshButton = findViewById(R.id.refresh_button);

        // Initialize the RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Retrieve the username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginSession", MODE_PRIVATE);
        storedUserId = sharedPreferences.getInt("userId", -1);

        // Set up home button click listener
        homeButton.setOnClickListener(v -> finish()); 

        // Set up update button click listener
        updateButton.setOnClickListener(v -> showUpdateDialog());

        // Set up refresh button click listener
        refreshButton.setOnClickListener(v -> refreshProfiles());

        // Fetch and display the user's profile data
        fetchUserProfile(storedUserId);

        // Prompt user for ID and fetch profile data
        // promptUserForUsername();
    }

    // Fetch user profile from the server
    private void fetchUserProfile(int userId) {
        String url = "http://10.90.72.226:8080/profiles/" + userId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject userObject = response.getJSONObject("user");

                        // Extract the profile data from the response
                        String username = userObject.getString("username");
                        String rankValue = response.getString("rank");
                        int games = response.getInt("games");
                        int wins = response.getInt("wins");
                        int losses = response.getInt("loss");

                        // Update UI with the extracted data
                        profileName.setText(username);
                        rank.setText("Rank: " + rankValue);
                        gamesNumber.setText("Games: " + games);
                        winsNumber.setText("Wins: " + wins);
                        lossesNumber.setText("Losses: " + losses);
                    
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ProfileActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(ProfileActivity.this, "Error fetching profile", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        );

        // Adding the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }

    //  AlertDialog for updating user profile
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Profile");

        final EditText inputUsername = new EditText(this);
        inputUsername.setHint("Username");

        final EditText inputRank = new EditText(this);
        inputRank.setHint("Rank");

        final EditText inputGamesPlayed = new EditText(this);
        inputGamesPlayed.setHint("Games Played");
        inputGamesPlayed.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputWins = new EditText(this);
        inputWins.setHint("Wins");
        inputWins.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputLosses = new EditText(this);
        inputLosses.setHint("Losses");
        inputLosses.setInputType(InputType.TYPE_CLASS_NUMBER);

        // Create layout to hold the input fields
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputUsername);
        layout.addView(inputRank);
        layout.addView(inputGamesPlayed);
        layout.addView(inputWins);
        layout.addView(inputLosses);

        builder.setView(layout);

        // Add buttons
        builder.setPositiveButton("Update", (dialog, which) -> {
            String username = inputUsername.getText().toString();
            String rank = inputRank.getText().toString();
            int gamesPlayed = Integer.parseInt(inputGamesPlayed.getText().toString());
            int wins = Integer.parseInt(inputWins.getText().toString());
            int losses = Integer.parseInt(inputLosses.getText().toString());

            updateUserProfile(username, rank, gamesPlayed, wins, losses);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Show dialog
        builder.show();
    }

    // PUT request to update user profile
    private void updateUserProfile(String username, String rank, int gamesPlayed, int wins, int losses) {
        String url = "http://10.90.72.226:8080/users/profile/update"; // Adjust as necessary
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("rank", rank);
            jsonBody.put("gamesplayed", gamesPlayed);
            jsonBody.put("wins", wins);
            jsonBody.put("loss", losses);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                    response -> {
                        Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        fetchUserProfile(storedUserId); // Refresh profile data
                    },
                    error -> Toast.makeText(ProfileActivity.this, "Error updating profile", Toast.LENGTH_SHORT).show()
            );

            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void refreshProfiles() {
        String url = "http://10.90.72.226:8080/profiles/refresh";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(ProfileActivity.this, "Profiles refreshed successfully!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Toast.makeText(ProfileActivity.this, "Failed to refresh profiles.", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                });

        // Add request to the queue
        requestQueue.add(stringRequest);
    }

}

