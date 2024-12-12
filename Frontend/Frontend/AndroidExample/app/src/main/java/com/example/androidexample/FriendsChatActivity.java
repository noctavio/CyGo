package com.example.appdemo;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import androidx.appcompat.app.AppCompatActivity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class FriendsChatActivity extends AppCompatActivity {

    private TextView chatMessages;
    private EditText chatInput;
    private Button sendChatButton;
    private ScrollView chatScrollView;
    private WebSocketClient mWebSocketClient;

    private String user1;
    private String user2;
    private String chatUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendschat);

        // Retrieve the extras passed from the previous activity
        user1 = getIntent().getStringExtra("user1");
        user2 = getIntent().getStringExtra("user2");
        chatUrl = "ws://10.90.72.226:8080/dmChat/" + user1 + "/" + user2; // WebSocket URL

        chatMessages = findViewById(R.id.chatMessages);
        chatInput = findViewById(R.id.chatInput);
        sendChatButton = findViewById(R.id.sendChatButton);
        chatScrollView = findViewById(R.id.chatScrollView);
        ImageButton homeButton = findViewById(R.id.home_image_button);

        // Set click listeners for each button
        homeButton.setOnClickListener(v -> {
            // Create an Intent to navigate to MainMenuActivity
            Intent intent = new Intent(FriendsChatActivity.this, MainMenuActivity.class);

            // Start the MainMenuActivity
            startActivity(intent);
        });

        sendChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        // Now that we have user1, user2, and the chat URL, we can initialize the WebSocket
        connectToWebSocket();
    }

    // Connect to the WebSocket server or backend
    private void connectToWebSocket() {
        try {
            // Use the chat URL from the server endpoint with user1 and user2
            URI uri = new URI(chatUrl);
            mWebSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chatMessages.append("Connected to chat with " + user2 + "\n");
                        }
                    });
                }

                @Override
                public void onMessage(final String message) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chatMessages.append(message + "\n");
                            chatScrollView.fullScroll(ScrollView.FOCUS_DOWN); // Scroll to the bottom
                        }
                    });
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chatMessages.append("Disconnected from the chat.\n");
                        }
                    });
                }

                @Override
                public void onError(Exception ex) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chatMessages.append("Error: " + ex.getMessage() + "\n");
                        }
                    });
                }
            };
            mWebSocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    // Send message to WebSocket server
    private void sendMessage() {
        String message = chatInput.getText().toString().trim();
        if (!message.isEmpty()) {
            mWebSocketClient.send(message);
            chatInput.setText(""); // Clear input field

            // Update UI with sent message
            chatMessages.append("You: " + message + "\n");
            chatScrollView.fullScroll(ScrollView.FOCUS_DOWN); // Scroll to the bottom
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            mWebSocketClient.close(); // Close WebSocket when activity is destroyed
        }
    }
}