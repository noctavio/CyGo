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

public class Settings extends AppCompatActivity {

    TextView profileName;
    ImageView profilePicture;
    String userId; // This will be dynamically fetched based on the logged-in user
    String currentUsername; // Holds the current username to track it until changed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);  // Your XML layout file

        profileName = findViewById(R.id.profileName);
        profilePicture = findViewById(R.id.profilePicture);

        ImageView board1Button = findViewById(R.id.board1);
        ImageView board2Button = findViewById(R.id.board2);
        ImageView board3Button = findViewById(R.id.board3);

        ImageView stone1Button = findViewById(R.id.stone1);
        ImageView stone2Button = findViewById(R.id.stone2);
        ImageView stone3Button = findViewById(R.id.stone3);

        board1Button.setOnClickListener(v -> onBoardButtonClicked(1));
        board2Button.setOnClickListener(v -> onBoardButtonClicked(2));
        board3Button.setOnClickListener(v -> onBoardButtonClicked(3));

        stone1Button.setOnClickListener(v -> onStoneButtonClicked(1));
        stone2Button.setOnClickListener(v -> onStoneButtonClicked(2));
        stone3Button.setOnClickListener(v -> onStoneButtonClicked(3));

        new FetchUserProfileTask().execute();

        Button changeUsernameButton = findViewById(R.id.changeUsernameButton);
        changeUsernameButton.setOnClickListener(v -> onChangeUsername(v));
    }

    private class FetchUserProfileTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return fetchProfileDetails(); // Fetch from the old URL
        }

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

                            profileName.setText(currentUsername);

                            if (!profilePictureUrl.equals("-/-")) {
                                // Load custom profile picture if available
                                // For example, using Picasso or Glide
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

    private class UpdateUsernameTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String newUsername = params[0];
            return updateUsername(newUsername); // Update using the new URL
        }

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

        public String updateUsername(String newUsername) {
            String response = "";
            try {
                URL url = new URL("https://6731788f7aaf2a9aff10ba68.mockapi.io/settings/user/" + userId);  // New URL
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

    private void onBoardButtonClicked(int boardValue) {
        new UpdateBoardStoneTask().execute("board", boardValue);
    }

    private void onStoneButtonClicked(int stoneValue) {
        new UpdateBoardStoneTask().execute("stone", stoneValue);
    }

    private class UpdateBoardStoneTask extends AsyncTask<Object, Void, String> {

        @Override
        protected String doInBackground(Object... params) {
            String type = (String) params[0];  // "board" or "stone"
            int value = (int) params[1];       // 1, 2, or 3 (depending on the button clicked)
            return updateBoardStone(type, value);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                Log.d("Settings", "Board/Stone value updated successfully");
            } else {
                Log.e("Settings", "Failed to update Board/Stone value");
            }
        }

        private String updateBoardStone(String type, int value) {
            String response = "";
            try {
                URL url = new URL("https://6731788f7aaf2a9aff10ba68.mockapi.io/settings/boardstones/1"); // Using id 1 as example
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
