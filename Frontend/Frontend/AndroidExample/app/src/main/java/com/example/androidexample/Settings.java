package com.example.androidexample;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Activity for managing user profile settings including username and board/stone preferences.
 * Handles actions such as changing the username and updating preferences.
 *
 * @author Matthew Hill
 */
public class Settings extends AppCompatActivity {

    private TextView profileName;
    private ImageView profilePicture;
    private String userId; // Holds the user's ID dynamically fetched based on the logged-in user
    private String currentUsername; // Holds the current username to track it until changed

    /**
     * Called when the activity is created.
     * Initializes UI components and sets up listeners for various settings options.
     *
     * @param savedInstanceState The saved state of the activity, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);  // Your XML layout file

        profileName = findViewById(R.id.profileName);
        profilePicture = findViewById(R.id.profilePicture);

        // Initialize UI elements for board and stone preferences
        ImageView board1Button = findViewById(R.id.board1);
        ImageView board2Button = findViewById(R.id.board2);
        ImageView board3Button = findViewById(R.id.board3);

        ImageView stone1Button = findViewById(R.id.stone1);
        ImageView stone2Button = findViewById(R.id.stone2);
        ImageView stone3Button = findViewById(R.id.stone3);

        // Set listeners to update board and stone preferences
        board1Button.setOnClickListener(v -> onBoardButtonClicked(1));
        board2Button.setOnClickListener(v -> onBoardButtonClicked(2));
        board3Button.setOnClickListener(v -> onBoardButtonClicked(3));

        stone1Button.setOnClickListener(v -> onStoneButtonClicked(1));
        stone2Button.setOnClickListener(v -> onStoneButtonClicked(2));
        stone3Button.setOnClickListener(v -> onStoneButtonClicked(3));

        // Fetch the user's profile asynchronously
        new FetchUserProfileTask().execute();

        // Set up button to allow username change
        Button changeUsernameButton = findViewById(R.id.changeUsernameButton);
        changeUsernameButton.setOnClickListener(v -> onChangeUsername(v));
    }

    /**
     * AsyncTask to fetch user profile details from the server.
     * Fetches the profile associated with the currently logged-in user.
     */
    private class FetchUserProfileTask extends AsyncTask<Void, Void, String> {

        /**
         * Fetches user profile details from the server in the background.
         *
         * @param params The parameters passed to the task (unused).
         * @return A JSON response string containing profile details.
         */
        @Override
        protected String doInBackground(Void... params) {
            return fetchProfileDetails(); // Fetch from the old URL
        }

        /**
         * Updates the UI with the fetched user profile details.
         *
         * @param result The JSON response containing the profile details.
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                try {
                    JSONArray profiles = new JSONArray(result);
                    for (int i = 0; i < profiles.length(); i++) {
                        JSONObject profile = profiles.getJSONObject(i);
                        if (profile.getJSONObject("user").getBoolean("isLoggedIn")) {
                            userId = String.valueOf(profile.getInt("profile_id"));
                            currentUsername = profile.getString("username"); // Set the initial username
                            String profilePictureUrl = profile.getString("profilepicture");

                            // Update profile name in the UI
                            profileName.setText(currentUsername);

                            // Load the profile picture (default or custom based on URL)
                            if (!profilePictureUrl.equals("-/-")) {
                                // Load custom profile picture if available (e.g., using Picasso or Glide)
                            } else {
                                profilePicture.setImageResource(R.drawable.default_picture); // Default image
                            }
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Fetches the user profile details from the server using an HTTP GET request.
         *
         * @return A string containing the server's response with user profiles.
         */
        private String fetchProfileDetails() {
            String response = "";
            try {
                URL url = new URL("http://coms-3090-051.class.las.iastate.edu:8080/profiles");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    response = stringBuilder.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
    }

    /**
     * Handles the event when the "Change Username" button is clicked.
     * Prompts the user to input a new username via an AlertDialog.
     *
     * @param view The view that was clicked (the change username button).
     */
    public void onChangeUsername(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Username");

        final EditText input = new EditText(this);
        input.setHint("Enter new username");
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newUsername = input.getText().toString().trim();
            if (!newUsername.isEmpty()) {
                new UpdateUsernameTask().execute(newUsername);
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    /**
     * AsyncTask to update the user's username on the server.
     */
    private class UpdateUsernameTask extends AsyncTask<String, Void, String> {

        /**
         * Sends the new username to the server in the background.
         *
         * @param params The new username to be set.
         * @return The server's response containing the updated profile.
         */
        @Override
        protected String doInBackground(String... params) {
            String newUsername = params[0];
            return updateUsername(newUsername); // Update using the new URL
        }

        /**
         * Updates the UI with the new username after the server responds.
         *
         * @param result The response from the server containing the updated profile.
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    JSONObject updatedProfile = new JSONObject(result);
                    String updatedUsername = updatedProfile.getString("username");

                    profileName.setText(updatedUsername);
                    currentUsername = updatedUsername; // Update the current username
                    Log.d("Settings", "Username updated successfully");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("Settings", "Failed to update username");
            }
        }

        /**
         * Sends the new username to the server using an HTTP PUT request.
         *
         * @param newUsername The new username to be updated on the server.
         * @return The server's response containing the updated profile.
         */
        public String updateUsername(String newUsername) {
            String response = "";
            try {
                URL url = new URL("https://6731788f7aaf2a9aff10ba68.mockapi.io/settings/user/" + userId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", "application/json");

                JSONObject requestBody = new JSONObject();
                requestBody.put("username", newUsername);

                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                os.write(requestBody.toString().getBytes());
                os.flush();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    response = stringBuilder.toString();
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return response;
        }
    }

    /**
     * Handles the event when a board button is clicked.
     * Updates the user's board preference.
     *
     * @param boardValue The board value that corresponds to the clicked button.
     */
    private void onBoardButtonClicked(int boardValue) {
        new UpdateBoardStoneTask().execute("board", boardValue);
    }

    /**
     * Handles the event when a stone button is clicked.
     * Updates the user's stone preference.
     *
     * @param stoneValue The stone value that corresponds to the clicked button.
     */
    private void onStoneButtonClicked(int stoneValue) {
        new UpdateBoardStoneTask().execute("stone", stoneValue);
    }

    /**
     * AsyncTask to update board or stone preferences on the server.
     */
    private class UpdateBoardStoneTask extends AsyncTask<Object, Void, String> {

        /**
         * Sends the updated board or stone preference to the server.
         *
         * @param params The type of preference ("board" or "stone") and the corresponding value.
         * @return The server's response indicating success or failure.
         */
        @Override
        protected String doInBackground(Object... params) {
            String type = (String) params[0];  // "board" or "stone"
            int value = (int) params[1];       // 1, 2, or 3 (depending on the button clicked)
            return updateBoardStone(type, value);
        }

        /**
         * Updates the UI after the board or stone value has been updated.
         *
         * @param result The response from the server indicating success or failure.
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                Log.d("Settings", "Board/Stone value updated successfully");
            } else {
                Log.e("Settings", "Failed to update Board/Stone value");
            }
        }

        /**
         * Sends the updated board or stone preference to the server using an HTTP PUT request.
         *
         * @param type The type of preference ("board" or "stone").
         * @param value The value for the preference (1, 2, or 3).
         * @return The server's response indicating success or failure.
         */
        private String updateBoardStone(String type, int value) {
            String response = "";
            try {
                URL url = new URL("https://6731788f7aaf2a9aff10ba68.mockapi.io/settings/boardstones/1");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", "application/json");

                JSONObject requestBody = new JSONObject();
                if (type.equals("board")) {
                    requestBody.put("board", value);
                } else if (type.equals("stone")) {
                    requestBody.put("stone", value);
                }

                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                os.write(requestBody.toString().getBytes());
                os.flush();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    response = stringBuilder.toString();
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return response;
        }
    }
}
