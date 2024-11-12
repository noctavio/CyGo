package com.example.pre_game;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class PreGameActivity extends AppCompatActivity {

    private Button btnCreateFriendlyLobby, btnJoinLobby, btnInvitePlayer, btnReady, btnLeaveLobby, btnKickPlayer, btnDeleteLobby;
    private TextView statusText;
    private Switch friendlySwitch;

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
        friendlySwitch = findViewById(R.id.friendlySwitch);

        // Set click listeners for buttons
        btnCreateFriendlyLobby.setOnClickListener(v -> showCreateLobbyDialog());
        btnJoinLobby.setOnClickListener(v -> showJoinLobbyDialog());
        btnInvitePlayer.setOnClickListener(v -> showInvitePlayerDialog());
        btnLeaveLobby.setOnClickListener(v -> showLeaveLobbyDialog());
        btnKickPlayer.setOnClickListener(v -> showKickPlayerDialog());
        btnDeleteLobby.setOnClickListener(v -> showDeleteLobbyDialog());
    }

    private void showCreateLobbyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Lobby");

        // Create input field
        final EditText input = new EditText(this);
        input.setHint("Enter User ID to create lobby"); // Hint text for user input
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String userIdString = input.getText().toString();
            int userId = Integer.parseInt(userIdString);
            createLobby(userId);

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showJoinLobbyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Join Lobby");

        // Create two input fields
        final EditText lobbyIdInputField = new EditText(this);
        lobbyIdInputField.setHint("Enter Lobby ID"); // Hint for Lobby ID input
        final EditText userIdInputField = new EditText(this);
        userIdInputField.setHint("Enter User ID"); // Hint for User ID input

        // Create a vertical LinearLayout to hold the input fields
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(lobbyIdInputField);
        layout.addView(userIdInputField);

        builder.setView(layout);

        builder.setPositiveButton("Join", (dialog, which) -> {
            String lobbyId = lobbyIdInputField.getText().toString();
            String userId = userIdInputField.getText().toString();
            // Convert the input to integers
            int lobbyIdInt = Integer.parseInt(lobbyId);
            int userIdInt = Integer.parseInt(userId);

            // Call the joinLobby method to join the lobby
            joinLobby(lobbyIdInt, userIdInt);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showInvitePlayerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Invite Player");

        // Create input field
        final EditText inviteInput = new EditText(this);
        inviteInput.setHint("Enter Player User ID"); // Hint for Player User ID input
        builder.setView(inviteInput);

        builder.setPositiveButton("Invite", (dialog, which) -> {
            String playerId = inviteInput.getText().toString();
            // Call API to invite player with playerId
            Toast.makeText(PreGameActivity.this, "Inviting player with User ID: " + playerId, Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showLeaveLobbyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Leave Lobby");

        // Create input field
        final EditText input = new EditText(this);
        input.setHint("Enter User ID to leave lobby"); // Hint text for user input
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

        // Create input fields
        final EditText hostUserIdInput = new EditText(this);
        hostUserIdInput.setHint("Enter Host User ID"); // Hint for Host User ID input
        final EditText playerUserIdInput = new EditText(this);
        playerUserIdInput.setHint("Enter Player User ID"); // Hint for Player User ID input

        // Create a vertical LinearLayout to hold the input fields
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
