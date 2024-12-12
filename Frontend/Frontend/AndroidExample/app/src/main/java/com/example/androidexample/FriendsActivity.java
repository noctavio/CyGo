package com.example.appdemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The following class is responsible for managing the user's friends list.
 * This activity displays the list of friends, allows the user to add a new friend,
 * and provides the functionality to remove existing friends
 */
public class FriendsActivity extends AppCompatActivity {

    private Button addFriendButton;
    private Button removeFriendButton;
    private Button chatFriendButton;
    private int userId;
    private String username;
    private TextView noFriendsMessage; // TextView to show when no friends are found
    private TextView friendsTextView; // TextView to display the list of friends

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        addFriendButton = findViewById(R.id.addFriendButton);
        removeFriendButton = findViewById(R.id.removeFriendButton);
        chatFriendButton = findViewById(R.id.chatFriendButton);
        noFriendsMessage = findViewById(R.id.noFriendsMessage);
        friendsTextView = findViewById(R.id.textview_friends);
        ImageButton homeButton = findViewById(R.id.home_image_button);

        // Set click listeners for each button
        homeButton.setOnClickListener(v -> {
            // Create an Intent to navigate to MainMenuActivity
            Intent intent = new Intent(FriendsActivity.this, MainMenuActivity.class);

            // Start the MainMenuActivity
            startActivity(intent);
        });


        // Retrieve the userId from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginSession", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);
        username = sharedPreferences.getString("username", "Guest");

        if (userId == -1) {
            Toast.makeText(this, "Error: Unable to retrieve user ID.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch friends on start
        fetchFriends();

        // Set up Add Friend button
        addFriendButton.setOnClickListener(v -> {
            // Launch the dialog to add a friend
            openAddFriendDialog();
        });

        // Set up Chat Friend button
        chatFriendButton.setOnClickListener(v -> {
            // Show dialog to enter the friend's username and start a DM chat
            showChatDialog();
        });

        // Set up Remove Friend button
        removeFriendButton.setOnClickListener(v -> {
            // Ask for confirmation before removing the friend
            showRemoveFriendDialog();
        });
    }

    private void fetchFriends() {
        // Initialize the friends TextView
        friendsTextView = findViewById(R.id.textview_friends);

        // Define the URL to fetch friends from the server
        String url = "http://10.90.72.226:8080/friends/" + userId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    // Handle the response
                    Log.d("FriendsActivity", "Successfully fetched friends: " + response.toString());

                    // Process the response and display the friend names
                    StringBuilder friendsList = new StringBuilder("Friends:\n");

                    try {
                        // Iterate over the JSON array and build a list of friend names
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject friend = response.getJSONObject(i);

                            // Get the friend's name from the response
                            String friendUsername = friend.optString("username", "Unknown");
                            friendsList.append(friendUsername).append("\n");
                        }
                    } catch (Exception e) {
                        Log.e("FriendsActivity", "Error processing response", e);
                        friendsList.append("Error processing friends list.");
                    }

                    // Display the list of friends in the TextView
                    friendsTextView.setText(friendsList.toString());
                    friendsTextView.setTextColor(Color.parseColor("#FFFFCA00")); // Yellow color
                    friendsTextView.setVisibility(View.VISIBLE); // Make the TextView visible
                },
                error -> {
                    // Handle the error
                    Log.e("FriendsActivity", "Error fetching friends", error);
                    friendsTextView.setText("Failed to fetch friends.");
                    friendsTextView.setTextColor(Color.parseColor("#FFFFCA00")); // Yellow color
                    friendsTextView.setVisibility(View.VISIBLE); // Make the TextView visible
                }
        );

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(request);
    }

    private void openAddFriendDialog() {
        // Show dialog to add a friend
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Friend");

        // A layout for both fields
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // EditText for friend's name
        EditText friendNameInput = new EditText(this);
        friendNameInput.setHint("Enter friend's name");
        layout.addView(friendNameInput);

        /**
        EditText friendIdInput = new EditText(this);
        friendIdInput.setHint("Enter friend's ID");
        friendIdInput.setInputType(InputType.TYPE_CLASS_NUMBER); // Ensure it's numeric
        layout.addView(friendIdInput);
         */

        builder.setView(layout);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String friendName = friendNameInput.getText().toString().trim();
            // String friendIdString = friendIdInput.getText().toString().trim();

            if (!friendName.isEmpty()) {
                try {
                    // Call method to add the friend
                    addFriend(friendName);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Please enter a valid integer for the friend's ID.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter both the friend's name and ID", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void addFriend(String friendName) {
        // Define the URL to add a friend, where {userId} is the userId and {friendName} is the friend's name
        String url = "http://10.90.72.226:8080/friends/" + userId + "/" + friendName;

        // Create the request to add the friend using the POST method
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Handle successful response
                    Log.d("FriendsActivity", "Successfully added friend: " + response);
                    Toast.makeText(this, "Friend added successfully!", Toast.LENGTH_SHORT).show();
                    // Optionally, you can refresh the friends list after adding the friend
                    fetchFriends();
                },
                error -> {
                    // Handle error response
                    Log.e("FriendsActivity", "Error adding friend", error);
                    Toast.makeText(this, "Failed to add friend. Please try again.", Toast.LENGTH_SHORT).show();
                }
        );

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(request);
    }

    private void showRemoveFriendDialog() {
        // Create an EditText for the user to type the friend's username
        EditText editText = new EditText(FriendsActivity.this);
        editText.setHint("Enter friend's username");

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(FriendsActivity.this);
        builder.setTitle("Enter friend's username to remove")
                .setView(editText)  // Set the EditText as the view for the dialog
                .setPositiveButton("Remove Friend", (dialog, which) -> {
                    // Get the entered friend's username
                    String friendUsername = editText.getText().toString().trim();

                    if (friendUsername.isEmpty()) {
                        Toast.makeText(FriendsActivity.this, "Please enter a valid username", Toast.LENGTH_SHORT).show();
                    } else {
                        // Call the method to remove the friend by their username
                        removeFriend(friendUsername);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void removeFriend(String friendName) {
        String url = "http://10.90.72.226:8080/friends/delete/" + userId + "/" + friendName;

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    // Handle successful response
                    Toast.makeText(FriendsActivity.this, "Removed " + friendName, Toast.LENGTH_SHORT).show();

                    // Update the friends list and refresh the UI
                    fetchFriends();
                },
                error -> {
                    // Handle error
                    Toast.makeText(FriendsActivity.this, "Error removing friend: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Add the request to the Volley request queue
        Volley.newRequestQueue(FriendsActivity.this).add(stringRequest);
    }

    private void showChatDialog() {
        // Create an EditText for the user to type the friend's username
        EditText editText = new EditText(FriendsActivity.this);
        editText.setHint("Enter friend's username");

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(FriendsActivity.this);
        builder.setTitle("Enter friend's username")
                .setView(editText) // Set the EditText as the view for the dialog
                .setPositiveButton("Start Chat", (dialog, which) -> {
                    // Get the entered friend's username
                    String friendUsername = editText.getText().toString().trim();

                    if (friendUsername.isEmpty()) {
                        Toast.makeText(FriendsActivity.this, "Please enter a valid username", Toast.LENGTH_SHORT).show();
                    } else {

                        // Start the chat (open WebSocket or backend request)
                        startDMChat(username, friendUsername);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    // This method starts the DM chat by calling the WebSocket endpoint or backend
    private void startDMChat(String user1, String user2) {
        // You can initiate a WebSocket connection or call your backend here
        String url = "http://10.90.72.226:8080/dmChat/" + user1 + "/" + user2;

        // Creating an intent to start the FriendChatActivity
        Intent intent = new Intent(FriendsActivity.this, FriendsChatActivity.class);

        // Pass user1 and user2 to the FriendsChatActivity as extras
        intent.putExtra("user1", user1);
        intent.putExtra("user2", user2);
        intent.putExtra("chat_url", url);

        // Start the activity
        startActivity(intent);
    }
}




