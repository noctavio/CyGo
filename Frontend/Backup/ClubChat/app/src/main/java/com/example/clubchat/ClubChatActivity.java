package com.example.clubchat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class ClubChatActivity extends AppCompatActivity {
    private WebSocketClient webSocketClient;
    private TextView chatHistoryTv;
    private TextView activeUsersTv;
    private EditText messageInputEt;
    private Button sendMessageBtn;
    private Button disconnectBtn;
    private String clubName;
    private String username;

    private List<String> activeUsersList = new ArrayList<>(); // List to store active users

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_chat);

        chatHistoryTv= findViewById(R.id.chatHistoryTv);
        activeUsersTv = findViewById(R.id.activeUsersTv);  // Find the TextView for active users
        messageInputEt = findViewById(R.id.messageInputEt);
        sendMessageBtn = findViewById(R.id.sendMessageBtn);
        disconnectBtn = findViewById(R.id.disconnectBtn);

        // Set click listener for sending messages
        sendMessageBtn.setOnClickListener(v -> sendMessage());

        // Set click listener for disconnecting
        disconnectBtn.setOnClickListener(v -> handleDisconnect());

        // Show login dialog to get username and club name
        showLoginDialog();
    }

    private void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Club Name and Username");

        final EditText inputClubName = new EditText(this);
        inputClubName.setHint("Club Name");

        final EditText inputUsername = new EditText(this);
        inputUsername.setHint("Username");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputClubName);
        layout.addView(inputUsername);
        builder.setView(layout);

        builder.setPositiveButton("Join", (dialog, which) -> {
            clubName = inputClubName.getText().toString().trim();
            username = inputUsername.getText().toString().trim();

            // Initialize WebSocket after getting club name and username
            if (!username.isEmpty()) {
                initWebSocket();
            } else {
                showLoginDialog(); // Re-prompt if fields are empty
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    private void initWebSocket() {
        try {
            URI serverUri = new URI("ws://10.90.72.226:8080/" + username + "/join/gamechat");
            webSocketClient = new WebSocketClient(serverUri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    runOnUiThread(() -> appendMessageToChat("Connected to chat"));
                }

                @Override
                public void onMessage(String message) {

                    runOnUiThread(() -> {
                        // Append regular messages (from active users)
                        chatHistoryTv.append(message + "\n");
                    });
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    runOnUiThread(() -> appendMessageToChat("Disconnected: " + reason));
                }

                @Override
                public void onError(Exception ex) {
                    runOnUiThread(() -> appendMessageToChat("Error: " + ex.getMessage()));
                }
            };
            webSocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = messageInputEt.getText().toString().trim();
        if (!message.isEmpty()) {
            webSocketClient.send(message);
            messageInputEt.setText("");  // Clear input after sending
        }
        else {
            appendMessageToChat("Unable to send message: WebSocket not open.");
        }
    }

    private void appendMessageToChat(String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        chatHistoryTv.append("[" + timestamp + "] " + message + "\n");
    }

    private void addActiveUser(String username) {
        if (!activeUsersList.contains(username)) {
            activeUsersList.add(username);
            runOnUiThread(() -> updateActiveUsersDisplay());
        }
    }

    private void removeActiveUser(String username) {
        if (activeUsersList.contains(username)) {
            activeUsersList.remove(username);
            runOnUiThread(() -> updateActiveUsersDisplay());
        }
    }

    private void updateActiveUsersDisplay() {
        StringBuilder usersDisplay = new StringBuilder("Active Users:\n");
        for (String user : activeUsersList) {
            usersDisplay.append(user).append("\n");
        }
        activeUsersTv.setText(usersDisplay.toString());
    }


    // Handle disconnect functionality
    private void handleDisconnect() {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.close(); // Close the WebSocket connection
            appendMessageToChat("Disconnected from chat.");
        } else {
            appendMessageToChat("Already disconnected.");
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }
}
