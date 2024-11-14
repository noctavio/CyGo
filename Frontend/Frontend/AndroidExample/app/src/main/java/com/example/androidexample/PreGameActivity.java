package com.example.androidexample;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PreGameActivity extends AppCompatActivity {

    private Button btnCreateFriendlyLobby, btnJoinLobby, btnInvitePlayer, btnReady, btnLeaveLobby, btnKickPlayer, btnDeleteLobby, gameBtn;
    private TextView statusText;
    private int userId; // Initially set to 0, but updated from the user input when toggling ready status

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_game);

        // Initialize views
        btnCreateFriendlyLobby = findViewById(R.id.createFriendlyLobbyBtn);
        btnJoinLobby = findViewById(R.id.joinLobbyBtn);
        btnInvitePlayer = findViewById(R.id.invitePlayerBtn);
        btnReady = findViewById(R.id.readyBtn);
        btnLeaveLobby = findViewById(R.id.btnLeaveLobby);
        btnKickPlayer = findViewById(R.id.btnKickPlayer);
        btnDeleteLobby = findViewById(R.id.btnDeleteLobby);
        statusText = findViewById(R.id.statusText);
        gameBtn = findViewById(R.id.GameBtn);

        // Set click listeners for buttons
        btnCreateFriendlyLobby.setOnClickListener(v -> showCreateLobbyDialog());
        btnJoinLobby.setOnClickListener(v -> showJoinLobbyDialog());
        btnInvitePlayer.setOnClickListener(v -> showInvitePlayerDialog());
        btnLeaveLobby.setOnClickListener(v -> showLeaveLobbyDialog());
        btnKickPlayer.setOnClickListener(v -> showKickPlayerDialog());
        btnDeleteLobby.setOnClickListener(v -> showDeleteLobbyDialog());

        // Handle btnReady click for toggling ready status
        btnReady.setOnClickListener(v -> {
            // Prompt user to enter their user ID
            promptForUserId();
        });
    }

    // Method to prompt the user for their userId and then toggle ready status
    private void promptForUserId() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Your User ID");

        final EditText input = new EditText(this);
        input.setHint("Enter User ID");
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String userIdString = input.getText().toString();
            if (!userIdString.isEmpty()) {
                try {
                    userId = Integer.parseInt(userIdString);  // Parse the input to an integer
                    toggleReadyStatus(userId); // Call the method to toggle the ready status
                } catch (NumberFormatException e) {
                    Toast.makeText(PreGameActivity.this, "Invalid User ID", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(PreGameActivity.this, "User ID cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Method to toggle the ready status of a player
    private void toggleReadyStatus(int userId) {
        // Update the URL with the correct endpoint
        String url = "http://coms-3090-051.class.las.iastate.edu:8080/lobby/players/" + userId + "/toggleReady";
        Log.d("ReadyStatus", "Sending request to: " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                response -> {
                    // Log the response for debugging
                    Log.d("ReadyStatus", "Response: " + response);
                    if (response.contains("Success")) {  // Assuming a success message is returned
                        Toast.makeText(PreGameActivity.this, "Ready status toggled successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PreGameActivity.this, "Failed to toggle ready status", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Log the error and response status code
                    Log.e("ReadyStatus", "Error: " + error.getMessage());
                    if (error.networkResponse != null) {
                        Log.e("ReadyStatus", "Error Code: " + error.networkResponse.statusCode);
                    }
                    Toast.makeText(PreGameActivity.this, "Failed to toggle ready status", Toast.LENGTH_SHORT).show();
                });

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    // Dialog methods for other features (not modified, just copied from your original code)
    private void showCreateLobbyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Lobby");

        final EditText input = new EditText(this);
        input.setHint("Enter User ID to create lobby");
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String userIdString = input.getText().toString();
            int userId = Integer.parseInt(userIdString);
            createLobby(userId);

            gameBtn.setOnClickListener(v -> {
                Intent intent = new Intent(PreGameActivity.this, MainGame.class);
                intent.putExtra("hostUserId", userId);
                startActivity(intent);
            });
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showJoinLobbyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Join Lobby");

        final EditText lobbyIdInputField = new EditText(this);
        lobbyIdInputField.setHint("Enter Lobby ID");
        final EditText userIdInputField = new EditText(this);
        userIdInputField.setHint("Enter User ID");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(lobbyIdInputField);
        layout.addView(userIdInputField);

        builder.setView(layout);

        builder.setPositiveButton("Join", (dialog, which) -> {
            String lobbyId = lobbyIdInputField.getText().toString();
            String userId = userIdInputField.getText().toString();
            int lobbyIdInt = Integer.parseInt(lobbyId);
            int userIdInt = Integer.parseInt(userId);
            joinLobby(lobbyIdInt, userIdInt);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showInvitePlayerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Invite Player");

        final EditText inviteInput = new EditText(this);
        inviteInput.setHint("Enter Player User ID");
        builder.setView(inviteInput);

        builder.setPositiveButton("Invite", (dialog, which) -> {
            String playerId = inviteInput.getText().toString();
            invitePlayer(Integer.parseInt(playerId));
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showLeaveLobbyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Leave Lobby");

        final EditText input = new EditText(this);
        input.setHint("Enter User ID to leave lobby");
        builder.setView(input);

        builder.setPositiveButton("Leave", (dialog, which) -> {
            String userIdString = input.getText().toString();
            int userId = Integer.parseInt(userIdString);
            leaveLobby(userId);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showKickPlayerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Kick Player");

        final EditText hostUserIdInput = new EditText(this);
        hostUserIdInput.setHint("Enter Host User ID");
        final EditText playerUserIdInput = new EditText(this);
        playerUserIdInput.setHint("Enter Player User ID");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(hostUserIdInput);
        layout.addView(playerUserIdInput);

        builder.setView(layout);

        builder.setPositiveButton("Kick", (dialog, which) -> {
            String hostUserId = hostUserIdInput.getText().toString();
            String playerUserId = playerUserIdInput.getText().toString();
            kickPlayerFromLobby(Integer.parseInt(hostUserId), Integer.parseInt(playerUserId));
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showDeleteLobbyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Lobby");

        final EditText input = new EditText(this);
        input.setHint("Enter Host User ID to delete lobby");
        builder.setView(input);

        builder.setPositiveButton("Delete", (dialog, which) -> {
            String hostUserId = input.getText().toString();
            deleteLobby(Integer.parseInt(hostUserId));
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

// Method to create a lobby
    private void createLobby(int userId) {
        String url = "http://10.90.72.226:8080/lobby/" + userId + "/create";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Handle the response here
                    Log.d("CreateLobby", "Successfully created lobby: " + response);
                },
                error -> {
                    // Handle the error here
                    Log.e("CreateLobby", "Error: " + error.getMessage());
                });

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    // Method to join a lobby
    private void joinLobby(int lobbyId, int userId) {
        String url = "http://10.90.72.226:8080/lobby/" + userId + "/join/" + lobbyId;
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                response -> {
                    // Handle the response here
                    Log.d("JoinLobby", "Successfully joined lobby: " + response);
                },
                error -> {
                    // Handle the error here
                    Log.e("JoinLobby", "Error: " + error.getMessage());
                });

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    // Method to invite a player
    private void invitePlayer(int playerId) {
        String url = "http://10.90.72.226:8080/lobby/invite/" + playerId;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Handle the response here
                    Log.d("InvitePlayer", "Successfully invited player: " + response);
                },
                error -> {
                    // Handle the error here
                    Log.e("InvitePlayer", "Error: " + error.getMessage());
                });

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    // Method to leave a lobby
    private void leaveLobby(int userId) {
        String url = "http://10.90.72.226:8080/lobby/" + userId + "/leave";
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    // Handle the response here
                    Log.d("LeaveLobby", "Successfully left the lobby: " + response);
                },
                error -> {
                    // Handle the error here
                    Log.e("LeaveLobby", "Error: " + error.getMessage());
                });

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    // Method to kick a player
    private void kickPlayerFromLobby(int hostUserId, int userId) {
        String url = "http://10.90.72.226:8080/lobby/" + hostUserId + "/kick/" + userId;
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    // Handle the response here
                    Log.d("KickPlayer", "Successfully kicked player: " + response);
                },
                error -> {
                    // Handle the error here
                    Log.e("KickPlayer", "Error: " + error.getMessage());
                });

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    // Method to delete a lobby
    private void deleteLobby(int hostUserId) {
        String url = "http://10.90.72.226:8080/lobby/" + hostUserId + "/killLobby";
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
}
