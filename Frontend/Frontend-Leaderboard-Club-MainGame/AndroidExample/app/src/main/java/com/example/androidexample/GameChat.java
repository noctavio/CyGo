package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GameChat extends AppCompatActivity {

    private WebSocketClient webSocketClient;
    private LinearLayout chatHistoryTv;
    private TextView activeUsersTv;
    private EditText messageInputEt;
    private Button sendMessageBtn;
    private ImageButton disconnectBtn;
    private String username;
    private List<String> activeUsersList = new ArrayList<>();
    int hostID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_chat);
        hostID = getIntent().getIntExtra("hostID", -1);

        chatHistoryTv = findViewById(R.id.messagesContainer);
        activeUsersTv = findViewById(R.id.activeUsersTv);
        messageInputEt = findViewById(R.id.messageInputEt);
        sendMessageBtn = findViewById(R.id.sendMessageBtn);
        disconnectBtn = findViewById(R.id.exitButton);

        sendMessageBtn.setOnClickListener(v -> sendMessage());
        disconnectBtn.setOnClickListener(v -> handleDisconnect());
        showLoginDialog();
    }

    private void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Username");

        final EditText inputUsername = new EditText(this);
        inputUsername.setHint("Username");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputUsername);
        builder.setView(layout);

        builder.setPositiveButton("Join", (dialog, which) -> {
            username = inputUsername.getText().toString().trim();

            if (!username.isEmpty()) {
                initWebSocket();
                activeUsersTv.setText("Active Users: " + username);
            } else {
                showLoginDialog();
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
                    Log.d("GameChat", "Received message: " + message);
                    runOnUiThread(() -> {
                        if (message.startsWith("User Joined:")) {
                            addActiveUser(message.split(":")[1].trim());
                        } else if (message.startsWith("User Left:")) {
                            removeActiveUser(message.split(":")[1].trim());
                        } else {
                            appendMessageToChat(message);
                        }
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
            Log.d("GameChat", "Sending message: " + message);
            webSocketClient.send(message); // Send only, without appending
            messageInputEt.setText("");  // Clear input after sending
        } else {
            Log.d("GameChat", "Message is empty.");
            appendMessageToChat("Unable to send empty message.");
        }
    }

    private void appendMessageToChat(String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        TextView newMessageView = new TextView(this);
        newMessageView.setText("[" + timestamp + "] " + message);
        newMessageView.setTextSize(14); // Set the text size if needed
        newMessageView.setTextColor(getResources().getColor(android.R.color.black)); // Set the text color

        chatHistoryTv.addView(newMessageView);

        Log.d("GameChat", "Added message to chat: " + message);
    }


    private void addActiveUser(String username) {
        if (!activeUsersList.contains(username)) {
            activeUsersList.add(username);
            runOnUiThread(this::updateActiveUsersDisplay);
        }
    }

    private void removeActiveUser(String username) {
        if (activeUsersList.contains(username)) {
            activeUsersList.remove(username);
            runOnUiThread(this::updateActiveUsersDisplay);
        }
    }

    private void updateActiveUsersDisplay() {
        StringBuilder usersDisplay = new StringBuilder("Active Users:\n");
        for (String user : activeUsersList) {
            usersDisplay.append(user).append("\n");
        }
        activeUsersTv.setText(usersDisplay.toString());
    }

    private void handleDisconnect() {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.close(); // Close the WebSocket connection
            appendMessageToChat("Disconnected from chat.");
        } else {
            appendMessageToChat("Already disconnected.");
        }

        Intent intent = new Intent(GameChat.this, MainGame.class);
        intent.putExtra("hostUserId", hostID);
        startActivity(intent);
        finish(); // Close the current GameChat activity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }
}
