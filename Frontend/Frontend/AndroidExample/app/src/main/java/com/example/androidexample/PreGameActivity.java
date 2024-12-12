package com.example.androidexample;

import static android.content.Intent.getIntent;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.Response;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class represents the activity which handles the functionality of the pre-game lobby 
 * where users can create or join lobbies, invite or kick players, toggle their ready status,
 * and leave or delete lobbies.
 * It interacts with a backend server using HTTP requests and provides dialogs for user input.
 *
 * @author Eden Basnet
 */

public class PreGameActivity extends AppCompatActivity {

    private int storedUserId; // Logged-in user ID

    private String username; // Logged-in user's username

    private Button btnCreateFriendlyLobby; // Button to create a friendly lobby

    private Button btnJoinLobby; // Button to join an existing lobby

    private Button btnLeaveLobby; // Button to leave the lobby

    private Button btnKickPlayer; // Button to kick a player from the lobby

    private Button btnDeleteLobby; // Button to delete the lobby

    private TextView statusText; // Text view to display ready message

    private Button gameBtn; // Button to start the game

    private Button showPlayersBtn; // Button to show players in the lobby

    private TextView playersTextView; // Text view to display players in the lobby

    private TextView statusMessageTextView; // Text view to display status messages

    /**
     * Called when the activity is starting. Initializes the UI components and sets
     * click listeners for various lobby management actions.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down, this Bundle contains
     * the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_game);

        // Retrieve stored user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginSession", MODE_PRIVATE);
        storedUserId = sharedPreferences.getInt("userId", -1);
        username = sharedPreferences.getString("username", "Guest");

        // Initialize views and buttons
        btnCreateFriendlyLobby = findViewById(R.id.createFriendlyLobbyBtn);
        btnJoinLobby = findViewById(R.id.joinLobbyBtn);
        btnLeaveLobby = findViewById(R.id.btnLeaveLobby);
        btnKickPlayer = findViewById(R.id.btnKickPlayer);
        btnDeleteLobby = findViewById(R.id.btnDeleteLobby);
        gameBtn = findViewById(R.id.GameBtn);
        statusMessageTextView = findViewById(R.id.statusMessageTextView);
        showPlayersBtn = findViewById(R.id.button_show_players);
        playersTextView = findViewById(R.id.textview_lobby_players);
        ImageButton homeButton = findViewById(R.id.home_image_button);

        // Set click listeners for each button
        homeButton.setOnClickListener(v -> {
            // Create an Intent to navigate to MainActivity
            Intent intent = new Intent(PreGameActivity.this, MainActivity.class);
            startActivity(intent);
        });

        btnCreateFriendlyLobby.setOnClickListener(v -> createLobby());
        btnJoinLobby.setOnClickListener(v -> showJoinLobbyDialog());
        btnLeaveLobby.setOnClickListener(v -> leaveLobby());
        btnKickPlayer.setOnClickListener(v -> showKickPlayerDialog());
        showPlayersBtn.setOnClickListener(v -> getLobbyIdDialog());

        gameBtn.setOnClickListener(v -> {
            // Optionally, pass the storedUserId or other data to MainGameActivity
            Intent intent = new Intent(PreGameActivity.this, MainGame.class);
            intent.putExtra("username", username);
            // Start the MainActivity
            startActivity(intent);
        });
    }

    /**
     * Toggles the ready status of a player by sending a PUT request to the server.
     *
     * @param userId the ID of the user whose ready status will be toggled
     */
    private void toggleReadyStatus(int userId) {
        // Update the URL with the correct endpoint
        String url = "http://coms-3090-051.class.las.iastate.edu:8080/lobby/players/" + userId + "/toggleReady";
        Log.d("ReadyStatus", "Sending request to: " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                response -> {
                    // Log the response for debugging
                    Log.d("ReadyStatus", "Response: " + response);

                    // Check if the response contains the expected message format
                    if (response.contains("is now:")) {
                        // Extract the username and status message from the response
                        String[] parts = response.split(" is now: ");
                        String username = parts[0];
                        String status = parts[1];

                        // Show a success message with the username and status
                        Toast.makeText(PreGameActivity.this, username + " is now: " + status, Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle the case where the response is unexpected
                        Toast.makeText(PreGameActivity.this, "Failed to toggle ready status", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Log the error details
                    String errorMessage = "Failed to toggle ready status";
                    if (error.networkResponse != null) {
                        errorMessage += " (Status Code: " + error.networkResponse.statusCode + ")";
                        String responseBody = new String(error.networkResponse.data);
                        Log.e("ReadyStatus", "Error Response Body: " + responseBody);
                    } else {
                        Log.e("ReadyStatus", "No network response (likely timeout or connectivity issue).");
                    }
                    Toast.makeText(PreGameActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                });

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    /**
     * Initializes the game on the server using the specified host ID.
     * @param hostId
     */
    private void initializeGame(int hostId) {
        String url = "hhttp://coms-3090-051.class.las.iastate.edu:8080/lobby/" + hostId + "/initialize/game";
        Log.d("InitializeGame", "Initializing game with host ID: " + hostId);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    // Handle success
                    Log.d("InitializeGame", "Game initialized successfully: " + response);
                    TextView statusText = findViewById(R.id.statusMessageTextView);
                    statusText.setText("Game initialized successfully!");
                    statusText.setTextColor(Color.parseColor("#FFFFCA00")); // Yellow
                    statusText.setVisibility(View.VISIBLE);
                },
                error -> {
                    // Handle error
                    String errorMsg = "Failed to initialize game.";
                    if (error.networkResponse != null) {
                        errorMsg += " Status Code: " + error.networkResponse.statusCode;
                    }
                    Log.e("InitializeGame", errorMsg);

                    TextView statusText = findViewById(R.id.statusMessageTextView);
                    statusText.setText(errorMsg);
                    statusText.setTextColor(Color.RED); // Red for error
                    statusText.setVisibility(View.VISIBLE);
                });

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * Displays a dialog to kick a player from the lobby by entering the host
     * and player user IDs.
     */
    private void showKickPlayerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Kick Player");

        // Create input fields
        final EditText playerUserIdInput = new EditText(this);
        playerUserIdInput.setHint("Enter Player User ID"); // Hint for Player User ID input

        // Create a vertical LinearLayout to hold the input fields
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(playerUserIdInput);

        builder.setView(layout);

        builder.setPositiveButton("Kick", (dialog, which) -> {
            String playerUserId = playerUserIdInput.getText().toString();

            kickPlayerFromLobby(Integer.parseInt(String.valueOf(storedUserId)), Integer.parseInt(playerUserId));
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Displays a dialog to delete the current lobby by entering the host user ID.
     */
    private void showDeleteLobbyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Lobby");

        // Create input field
        final EditText input = new EditText(this);
        input.setHint("Enter Host User ID to delete lobby"); // Hint text for user input
        builder.setView(input);

        builder.setPositiveButton("Delete", (dialog, which) -> {
            String hostUserId = input.getText().toString();
            deleteLobby(Integer.parseInt(hostUserId));
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Creates a new lobby on the server with the stored user ID.
     */
    private void createLobby() {
        String url = "http://coms-3090-051.class.las.iastate.edu:8080/lobby/" + storedUserId + "/create";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Handle the response here
                    Log.d("CreateLobby", "Successfully created lobby: " + response);

                    // Automatically toggle ready status for the user
                    toggleReadyStatus(storedUserId);

                    // Find the statusText TextView in your layout
                    TextView statusText = findViewById(R.id.statusMessageTextView);

                    // Update the statusText with a message
                    statusText.setText(username + " created the lobby.");
                    statusText.setVisibility(View.VISIBLE);

                },
                error -> {
                    // Handle the error here
                    Log.e("CreateLobby", "Error: " + error.getMessage());

                    // Find the statusText TextView and update it to show the error message
                    TextView statusText = findViewById(R.id.statusMessageTextView);
                    statusText.setText("Failed to create lobby.");
                    statusText.setVisibility(View.VISIBLE);
                });

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    /**
     * Displays a dialog for joining a lobby and sends a PUT request to the server.
     */
    private void showJoinLobbyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Join Lobby");

        // Input field for Lobby ID
        final EditText lobbyIdInputField = new EditText(this);
        lobbyIdInputField.setHint("Enter Lobby ID"); // Hint for Lobby ID input
        builder.setView(lobbyIdInputField);

        builder.setPositiveButton("Join", (dialog, which) -> {
            String lobbyId = lobbyIdInputField.getText().toString();

            if (!lobbyId.isEmpty()) {
                try {
                    int lobbyIdInt = Integer.parseInt(lobbyId);

                    joinLobby(lobbyIdInt); // Pass the parsed lobbyId
                } catch (NumberFormatException e) {
                    Toast.makeText(PreGameActivity.this, "Invalid Lobby ID", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(PreGameActivity.this, "Lobby ID cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Joins an existing lobby on the server using the specified lobby ID.
     *
     * @param lobbyId The ID of the lobby to join.
     */
    private void joinLobby(int lobbyId) {
        String url = "http://coms-3090-051.class.las.iastate.edu:8080/lobby/" + storedUserId + "/join/" + lobbyId;
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                response -> {
                    // Handle the response here
                    Log.d("JoinLobby", "Successfully joined the lobby: " + response);

                    // Automatically toggle ready status for the user
                    toggleReadyStatus(storedUserId);

                    // Find the statusText TextView in your layout
                    TextView statusText = findViewById(R.id.statusMessageTextView);

                    // Update the statusText with a success message
                    statusText.setText(username + " joined the lobby.");
                    statusText.setVisibility(View.VISIBLE);
                },
                error -> {
                    // Handle the error here
                    Log.e("JoinLobby", "Error: " + error.getMessage());

                    // Automatically toggle ready status for the user
                    toggleReadyStatus(storedUserId);

                    // Find the statusText TextView and update it to show the error message
                    TextView statusText = findViewById(R.id.statusMessageTextView);
//                    statusText.setText("Failed to join lobby.");
                    statusText.setVisibility(View.VISIBLE);
                });

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    /**
     * Leaves the current lobby using the stored user ID and current lobby ID.
     */
    private void leaveLobby() {
        String url = "http://coms-3090-051.class.las.iastate.edu:8080/lobby/" + storedUserId + "/leave";

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    // Handle the response here
                    Log.d("LeaveLobby", "Successfully left the lobby: " + response);

                    // Find the statusText TextView in your layout
                    TextView statusText = findViewById(R.id.statusMessageTextView);

                    // Update the statusText with a message
                    statusText.setText(username + " left the lobby.");
                    statusText.setVisibility(View.VISIBLE);

                },
                error -> {
                    // Handle the error here
                    Log.e("LeaveLobby", "Error: " + error.getMessage());

                    // Find the statusText TextView and update it to show the error message
                    TextView statusText = findViewById(R.id.statusMessageTextView);
                    statusText.setText("Failed to leave the lobby.");
                    statusText.setVisibility(View.VISIBLE);
                });

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    /**
     * Kicks a player from the lobby using the specified host user ID and player user ID.
     *
     * @param hostUserId The user ID of the host.
     * @param userId     The user ID of the player to be kicked.
     */
    private void kickPlayerFromLobby(int hostUserId, int userId) {
        String url = "http://coms-3090-051.class.las.iastate.edu:8080/lobby/" + hostUserId + "/kick/" + userId;
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    // Handle the response here
                    Log.d("KickPlayer", "Successfully kicked player: " + response);

                    TextView statusText = findViewById(R.id.statusMessageTextView);

                    // Update the statusText with the kicked out message
                    String kickedMessage = "Successfully kicked out of the lobby!";
                    statusText.setText(kickedMessage);
                    statusText.setTextColor(Color.parseColor("#FFFFCA00")); // Set to yellow text color
                    statusText.setVisibility(View.VISIBLE);
                },
                error -> {
                    // Handle the error here
                    Log.e("KickPlayer", "Error: " + error.getMessage());

                    TextView statusText = findViewById(R.id.statusMessageTextView);
                    statusText.setText("Failed to kick player");
                    statusText.setTextColor(Color.parseColor("#FFFFCA00")); // Set to yellow text color
                    statusText.setVisibility(View.VISIBLE);
                });

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

     /**
     * Deletes the current lobby on the server using the specified host user ID.
     *
     * @param hostUserId The user ID of the host who created the lobby.
     */
    private void deleteLobby(int hostUserId) {
        String url = "http://coms-3090-051.class.las.iastate.edu:8080/lobby/" + hostUserId + "/killLobby";
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    // Handle the response here
                    Log.d("DeleteLobby", "Successfully deleted lobby: " + response);
                },
                error -> {
                    // Handle the error here
                    Log.e("DeleteLobby", "Error: " + error.getMessage());
                });

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    /**
     * Displays a dialog to get the lobby ID from the user.
     */
    private void getLobbyIdDialog() {
        // Create an AlertDialog to prompt the user for the lobby ID
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Lobby ID");

        // Create an EditText to allow the user to input the lobby ID
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER); // Ensure it's a number
        builder.setView(input);

        // Set up the dialog buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String lobbyIdStr = input.getText().toString();
            if (!lobbyIdStr.isEmpty()) {
                try {
                    // Convert the input to an integer
                    int lobbyId = Integer.parseInt(lobbyIdStr);
                    // Call the method to fetch players in the given lobby
                    fetchPlayersInLobby(lobbyId);
                } catch (NumberFormatException e) {
                    // Handle invalid input
                    Toast.makeText(this, "Invalid Lobby ID. Please enter a valid number.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter a Lobby ID.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        builder.show();
    }

    private void fetchPlayersInLobby(int lobbyId) {
        // Define the URL to get the players in the lobby
        String url = "http://coms-3090-051.class.las.iastate.edu:8080/lobby/allPlayers/" + lobbyId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    // Handle the response
                    Log.d("GetPlayers", "Successfully fetched players: " + response.toString());

                    // Process the response and display player names
                    StringBuilder playersList = new StringBuilder("Players in Lobby:\n");

                    try {
                        // Iterate over the JSON array and build a list of player names
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject player = response.getJSONObject(i);
                            String playerName = player.getString("username");  // Assuming player object contains "username"
                            playersList.append(playerName).append("\n");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Display the list of players in the TextView
                    playersTextView.setText(playersList.toString());
                    playersTextView.setTextColor(Color.parseColor("#FFFFCA00")); // Yellow color
                    playersTextView.setVisibility(View.VISIBLE);
                },
                error -> {
                    // Handle the error here
                    Log.e("GetPlayers", "Error: " + error.getMessage());
                    playersTextView.setText("Failed to fetch players.");
                    playersTextView.setVisibility(View.VISIBLE);
                });

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(request);
    }

    private void displayPlayers(JSONArray players) {
        // Find the TextView where players will be shown
        TextView playersTextView = findViewById(R.id.textview_lobby_players);

        // Clear the previous text
        playersTextView.setText("");

        // Iterate through the JSON array and display each player
        try {
            StringBuilder playersList = new StringBuilder("Players in Lobby:\n");

            for (int i = 0; i < players.length(); i++) {
                JSONObject player = players.getJSONObject(i);
                String username = player.getJSONObject("profile").getString("username");
                playersList.append(username).append("\n");
            }

            // Set the formatted list to the TextView
            playersTextView.setText(playersList.toString());
            playersTextView.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            Log.e("DisplayPlayers", "Error parsing players: " + e.getMessage());
        }
    }   
}
