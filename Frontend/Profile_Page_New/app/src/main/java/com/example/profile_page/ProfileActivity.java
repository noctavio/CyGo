package com.example.profile_page;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
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
    private int userId; // To store the fetched user ID
    private String username; //

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
        Button updateButton = findViewById(R.id.update_button);

        // Initialize the RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Set up update button click listener
        updateButton.setOnClickListener(v -> showUpdateDialog());

        // Prompt user for ID and fetch profile data
        promptUserForUsername();
    }

    // AlertDialog to prompt the user to enter their ID
    private void promptUserForUsername() {
        final EditText input = new EditText(this);
        input.setHint("Enter your id");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        new AlertDialog.Builder(this)
                .setTitle("User id Required")
                .setMessage("Please enter your user id to retrieve your profile.")
                .setView(input)
                .setPositiveButton("OK", (dialog, which) -> {
                    userId = Integer.parseInt(input.getText().toString()); // Parse input to int
                    fetchUserProfile(userId);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .setCancelable(false)
                .show();
    }

    // Fetch user profile from the server
    private void fetchUserProfile(int userId) {
        String url = "http://10.90.72.226:8080/Profile/" + userId;

        // String url = "https://f0927cec-f851-4cbc-8cb4-2b00cda87dd4.mock.pstmn.io/test"; //

        // String url = "http://coms-3090-051.class.las.iastate.edu:8080/" + username;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
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

        // Adding the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }

    // Updating the profile UI with the fetched data
    private void updateProfileUI(int id, String username, String profilePicture, String club, String clubPicture, int wins, int losses, int gamesPlayed, int rating) {
            profileName.setText(username);
            rank.setText(String.valueOf(rating));
            gamesNumber.setText(String.valueOf(gamesPlayed));
            winsNumber.setText(String.valueOf(wins));
            lossesNumber.setText(String.valueOf(losses));

    }

    //  AlertDialog for updating user profile
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Profile");

        final EditText inputClub = new EditText(this);
        inputClub.setHint("New Club Name");

        // Layout to hold the input fields
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputClub);

        builder.setView(layout);

        // Add buttons
        builder.setPositiveButton("Update", (dialog, which) -> {
            String club = inputClub.getText().toString();

            updateUserProfile(userId, club);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Show dialog
        builder.show();
    }

    // PUT request to update user profile
    private void updateUserProfile(int id, String club) {
          String url = "http://10.90.72.226:8080/Profile/" + id; // URL for PUT
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("CLUB", club); // Uppercase field
            jsonBody.put("club", club); // Lowercase field

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                    response -> {
                        Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        fetchUserProfile(userId);
                    },
                    error -> Toast.makeText(ProfileActivity.this, "Error updating profile", Toast.LENGTH_SHORT).show()
            );

            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

