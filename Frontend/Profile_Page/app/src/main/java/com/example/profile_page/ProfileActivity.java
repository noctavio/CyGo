package com.example.profile_page;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {
    private TextView profileName, rank, gamesNumber, winsNumber, lossesNumber;
    private RequestQueue requestQueue;
    private String userId; // To store the fetched user ID
    private String username; // To store the fetched username

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        profileName = findViewById(R.id.profile_name);
        rank = findViewById(R.id.rank);
        gamesNumber = findViewById(R.id.games_number);
        winsNumber = findViewById(R.id.wins_number);
        lossesNumber = findViewById(R.id.loss_number);
        Button updateButton = findViewById(R.id.update_button); // Button for opening the update dialog

        // Initialize the RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Set up update button click listener
        updateButton.setOnClickListener(v -> showUpdateDialog());

        // Prompt user for ID and fetch profile data
        promptUserForUsername(); // Call this method on Activity start
    }

    //  Show an AlertDialog to prompt the user to enter their ID
    private void promptUserForUsername() {
        final EditText input = new EditText(this);
        input.setHint("Enter your username");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS); // Allow any text input without suggestions


        new AlertDialog.Builder(this)
                .setTitle("Username Required")
                .setMessage("Please enter your username to retrieve your profile.")
                .setView(input)
                .setPositiveButton("OK", (dialog, which) -> {
                    username = input.getText().toString();
                    // Step 2: Fetch user profile after entering ID
                    fetchUserProfile(username);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .setCancelable(false) // Make sure the user can't dismiss the dialog without providing an ID
                .show();
    }

    // Fetch user profile from the server
    private void fetchUserProfile(String username) {
         String url = "http://10.90.72.226:8080/Profile/" + username; // Adjust URL as necessary

        // String url = "https://b1c3b02a-2a7a-4643-9315-fe36d8b14877.mock.pstmn.io/test"; // Adjust URL as necessary

        // String url = "http://coms-3090-051.class.las.iastate.edu:8080/" + username;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // Assuming the response returns user info directly as a JSON object
                        int id = response.getInt("id");
                        String fetchedUsername = response.getString("USERNAME");
                        String profilePicture = response.getString("PROFILE_PICTURE");
                        String club = response.getString("CLUB");
                        String clubPicture = response.getString("CLUB_PICTURE");
                        int wins = response.getInt("WINS");
                        int losses = response.getInt("LOSSES");
                        int gamesPlayed = response.getInt("GAMES_PLAYED");
                        int rating = response.getInt("RATING");

                        // Update the profile UI with the retrieved data
                        updateProfileUI(id, fetchedUsername, profilePicture, club, clubPicture, wins, losses, gamesPlayed, rating);
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

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }

    // Step 3: Update the profile UI with the fetched data
    private void updateProfileUI(int id, String username, String profilePicture, String club, String clubPicture, int wins, int losses, int gamesPlayed, int rating) {
            profileName.setText(username);
            rank.setText(String.valueOf(rating)); // Assuming rank is represented by rating
            gamesNumber.setText(String.valueOf(gamesPlayed));
            winsNumber.setText(String.valueOf(wins));
            lossesNumber.setText(String.valueOf(losses));

    }

    // Optional: AlertDialog for updating user profile (existing code)
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Profile");

        // Create input fields
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
        // String url = "http://10.90.72.226:8080/Profile/" + username; // Updated URL for PUT
       String url = "https://b1c3b02a-2a7a-4643-9315-fe36d8b14877.mock.pstmn.io/test";
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
                        fetchUserProfile(userId); // Refresh profile data
                    },
                    error -> Toast.makeText(ProfileActivity.this, "Error updating profile", Toast.LENGTH_SHORT).show()
            );

            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

