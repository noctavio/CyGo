package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import android.view.View;
import android.os.AsyncTask;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

public class GameCounting extends AppCompatActivity {

    // Declare your UI elements
    TextView playerAName, playerARank, playerBName, playerBRank;
    TextView playerCName, playerCRank, playerDName, playerDRank;
    String currentBoard = "";
    String currentStone = "";
    int white_stoned;
    int black_stoned;
    ImageView boardImageView;
    AppCompatImageView playerAImage, playerBImage, playerCImage, playerDImage;
    int playerAIndex, playerBIndex, playerCIndex, playerDIndex;
    int lobbyId;
    String lastMoveColor = "N";
    int lastMoveX = -1, lastMoveY =-1;
    String hostName;
    // Declare player IDs for PlayerA, PlayerB, PlayerC, PlayerD
    int playerAId, playerCId;

    // Global variable for personCounting as player ID
    int personCounting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_counting);

        // Initialize your UI elements
        playerAName = findViewById(R.id.player_a_name);
        playerARank = findViewById(R.id.player_a_rank);
        playerBName = findViewById(R.id.player_b_name);
        playerBRank = findViewById(R.id.player_b_rank);
        playerCName = findViewById(R.id.player_c_name);
        playerCRank = findViewById(R.id.player_c_rank);
        playerDName = findViewById(R.id.player_d_name);
        playerDRank = findViewById(R.id.player_d_rank);
        boardImageView = findViewById(R.id.go_board);
        new FetchBoardStoneDetails().execute();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String url = "http://coms-3090-051.class.las.iastate.edu:8080/lobby";
        String jsonResponse = fetchDataFromUrl(url);
        Log.d("JSON Response", jsonResponse);

        int hostID = getIntent().getIntExtra("hostUserId", -1);

        try {
            JSONArray lobbyData = new JSONArray(jsonResponse);
            JSONObject targetLobby = null;

            for (int i = 0; i < lobbyData.length(); i++) {
                JSONObject lobby = lobbyData.getJSONObject(i);
                JSONObject team1 = lobby.getJSONObject("team1");
                JSONObject player1 = team1.getJSONObject("player1");
                int currentHostID = player1.getJSONObject("profile").getJSONObject("user").getInt("user_id");

                if (currentHostID == hostID) {
                    targetLobby = lobby;
                    break;
                }
            }

            if (targetLobby != null) {
                lobbyId = targetLobby.optInt("lobby_id", -1);
                Log.d("LobbyID", "Lobby ID: " + lobbyId);

                Toast.makeText(this, "Lobby ID: " + lobbyId, Toast.LENGTH_LONG).show();

                JSONObject team1 = targetLobby.getJSONObject("team1");
                JSONObject playerA = team1.getJSONObject("player1");
                JSONObject playerB = team1.getJSONObject("player2");
                playerAId = playerA.getJSONObject("profile").getJSONObject("user").getInt("user_id");
                String playerANameText = playerA.getJSONObject("profile").getString("username");
                String playerARankText = playerA.getJSONObject("profile").getString("rank");
                if (playerAName != null && playerARank != null) {
                    playerAName.setText(playerANameText);
                    playerARank.setText(playerARankText);
                }

                String playerBNameText = playerB.getJSONObject("profile").getString("username");
                String playerBRankText = playerB.getJSONObject("profile").getString("rank");
                if (playerBName != null && playerBRank != null) {
                    playerBName.setText(playerBNameText);
                    playerBRank.setText(playerBRankText);
                }

                JSONObject team2 = targetLobby.getJSONObject("team2");
                JSONObject playerC = team2.getJSONObject("player1");
                JSONObject playerD = team2.getJSONObject("player2");
                playerCId = playerC.getJSONObject("profile").getJSONObject("user").getInt("user_id");
                String playerCNameText = playerC.getJSONObject("profile").getString("username");
                String playerCRankText = playerC.getJSONObject("profile").getString("rank");
                if (playerCName != null && playerCRank != null) {
                    playerCName.setText(playerCNameText);
                    playerCRank.setText(playerCRankText);
                }

                String playerDNameText = playerD.getJSONObject("profile").getString("username");
                String playerDRankText = playerD.getJSONObject("profile").getString("rank");
                if (playerDName != null && playerDRank != null) {
                    playerDName.setText(playerDNameText);
                    playerDRank.setText(playerDRankText);
                }

            } else {
                Log.e("Lobby", "Lobby with hostID " + hostID + " not found.");
            }
        } catch (JSONException e) {
            Log.e("JSONError", "Error parsing JSON: " + e.getMessage());
        }

        setupBoardButtonListeners();
        Log.d("GameCounting", "Lobby ID just before fetching board state: " + lobbyId);
        fetchBoardState(lobbyId);
        personCounting = playerAId;
        Button countButton = findViewById(R.id.Count); // Use Button instead of ImageButton
        countButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPersonCounting();
            }
        });
        Button DoneButton = findViewById(R.id.Done);
        DoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFinalizeClaim(personCounting);
                switchPersonCounting();
            }
        });
}
    public String toggleFinalizeClaim(Integer userId) {
        try {
            // Construct URL for the API endpoint
            URL url = new URL("http://coms-3090-051.class.las.iastate.edu:8080/goban/" + "/{userId}/toggleFinalizeClaim".replace("{userId}", userId.toString()));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to PUT
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);  // Allow output for sending data

            // Connect to the server
            connection.connect();

            // Get the response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response from the server
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString();  // Return the server response
            } else {
                return "Error: " + responseCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
    private void switchPersonCounting() {
        if (personCounting == playerAId) {
            personCounting = playerCId;  // Switch to PlayerC's ID
        } else if (personCounting == playerCId) {
            personCounting = playerAId;  // Switch back to PlayerA's ID
        } else {
            // You can add more conditions here if you want to support PlayerB, PlayerD, etc.
            personCounting = playerAId;  // Default to PlayerA's ID
        }

        // Update UI or perform other actions based on the current player (optional)
        Log.d("SwitchPersonCounting", "Current personCounting is now: " + personCounting);
    }

    // Add the remaining methods and functionality as needed
    private class FetchBoardStoneDetails extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            return fetchBoardStoneData();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    // Parse the JSON response
                    JSONArray jsonArray = new JSONArray(result);
                    if (jsonArray.length() > 0) {
                        JSONObject boardStoneData = jsonArray.getJSONObject(0);

                        // Assign values from JSON response
                        currentBoard = boardStoneData.getString("board");
                        currentStone = boardStoneData.getString("stone");

                        // Set the stone values based on the stone value
                        setBoardValues(currentBoard);
                        setStoneValues(currentStone);

                        // Optionally, log the values or use them in your app
                        Log.d("GameCounting", "Current Board: " + currentBoard);
                        Log.d("GameCounting", "Current Stone: " + currentStone);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(GameCounting.this, "Error parsing board/stone data", Toast.LENGTH_SHORT).show();
                }
            }
        }

        // Fetch board and stone details from the API
        private String fetchBoardStoneData() {
            String response = "";
            try {
                // The URL to fetch the board and stone data
                URL url = new URL("https://6731788f7aaf2a9aff10ba68.mockapi.io/settings/boardstones");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // Read the response
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                    int data = reader.read();
                    StringBuilder stringBuilder = new StringBuilder();
                    while (data != -1) {
                        stringBuilder.append((char) data);
                        data = reader.read();
                    }
                    response = stringBuilder.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
                response = null;
            }
            return response;
        }
    }

    // Method to set stone values based on the stone value
    private void setStoneValues(String stone) {
        // Set the stone values based on the stone value received from the server
        switch (stone) {
            case "1":
                white_stoned = R.drawable.white_stone;
                black_stoned = R.drawable.black_stone;
                break;

            case "2":
                white_stoned = R.drawable.white_stone2;
                black_stoned = R.drawable.black_stone_2;
                break;

            case "3":
                white_stoned = R.drawable.white_stone3;
                black_stoned = R.drawable.black_stone_3;;
                break;

            default:
                Log.e("GameCounting", "Invalid stone value: " + stone);
                break;
        }

    }
    private void setBoardValues(String board) {
        // Set the stone values based on the stone value received from the server
        switch (board) {
            case "1":
                boardImageView.setImageResource(R.drawable.board);  // Set the first board image
                break;

            case "2":
                boardImageView.setImageResource(R.drawable.board_2);  // Set the second board image
                break;

            case "3":
                boardImageView.setImageResource(R.drawable.board_3);  // Set the third board image
                break;

            default:
                Log.e("GameCounting", "Invalid board value: " + board);
                break;
        }

    }

    // Use currentBoard and currentStone as needed in your game logic
    // For example:
    private void useBoardAndStoneValues() {
        if (!currentBoard.isEmpty() && !currentStone.isEmpty()) {
            // Handle logic based on currentBoard and currentStone
            Log.d("GameCounting", "Using Board: " + currentBoard + " and Stone: " + currentStone);
        }
    }
    private String fetchDataFromUrl(String urlString) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
            int data = inputStreamReader.read();
            while (data != -1) {
                result.append((char) data);
                data = inputStreamReader.read();
            }

            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private void setupBoardButtonListeners() {
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                String buttonID = "button" + x + y; // ID format, e.g., button00, button01, etc.
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());

                ImageButton button = findViewById(resID);

                if (button != null) {
                    int finalX = x;
                    int finalY = y;
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int playerID = personCounting;
                            new PlaceStoneTask(playerID, finalX, finalY).execute();
                            fetchBoardState(lobbyId);
                        }
                    });
                }
            }
        }
    }
    private class PlaceStoneTask extends AsyncTask<Void, Void, String> {
        private int userId;
        private int x;
        private int y;

        public PlaceStoneTask(int userId, int x, int y) {
            this.userId = userId;
            this.x = x;
            this.y = y;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = null;
            try {
                String urlString = "http://coms-3090-051.class.las.iastate.edu:8080/goban/" + userId + "/territory/" + x + "/" + y;
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    result = "Point placed successfully!";
                } else {
                    result = "Failed to place point!";
                }

                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                result = "Error placing point: " + e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(GameCounting.this, result, Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchBoardState(int lobbyId) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String boardState = null;
                try {
                    String urlString = "http://coms-3090-051.class.las.iastate.edu:8080/goban/" + lobbyId + "/board";

                    Log.d("GameCounting", "Requesting board state from URL: " + urlString);

                    URL url = new URL(urlString);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
                        StringBuilder result = new StringBuilder();
                        int data = reader.read();
                        while (data != -1) {
                            result.append((char) data);
                            data = reader.read();
                        }
                        reader.close();
                        boardState = result.toString();

                        Log.d("GameCounting", "Raw board state response: " + boardState);
                    } else {
                        Log.e("GameCounting", "Failed to fetch board state, Response code: " + responseCode);
                    }

                    urlConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("GameCounting", "Error fetching board state: " + e.getMessage());
                }
                return boardState;
            }

            @Override
            protected void onPostExecute(String boardState) {
                if (boardState != null) {
                    Log.d("GameCounting", "Fetched Board State: " + boardState); // Log the fetched board state here
                    updateBoardWithState(boardState);
                } else {
                    Log.e("GameCounting", "Failed to fetch board state");
                }
            }
        }.execute();
    }




    private void updateBoardWithState(String boardState) {
        Log.d("GameCounting", "Board State: " + boardState); // Log the board state to see what's being received

        String[] cells = boardState.trim().split("\\s+");

        int validPositionCount = 0;  // Count valid board positions
        int index = 0;  // Index to track each button position based on column-major order

        for (String cell : cells) {
            cell = cell.trim();  // Remove any whitespace around the cell

            if (cell.equals("'") || cell.isEmpty()) {
                Log.d("GameCounting", "Ignoring empty or standalone apostrophe token");
                continue;  // Skip standalone apostrophes or empty tokens
            }

            if (cell.equals("W") || cell.equals("B") || cell.equals("Wc") || cell.equals("Bc") || cell.equals("Bp") || cell.equals("Wp") || cell.equals("X")) {
                validPositionCount++;
            } else {
                Log.w("GameCounting", "Unexpected token: " + cell);
                continue;
            }

            int row = index / 9;
            int col = index % 9;

            // Generate button ID based on row and column
            String buttonID = "button" + row + col;
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            ImageButton button = findViewById(resID);

            if (button != null) {

                switch (cell) {
                    case "W":
                        button.setImageResource(white_stoned);
                        break;
                    case "B":
                        button.setImageResource(black_stoned);
                        break;
                    case "Wc":
                        button.setImageResource(R.drawable.white_point);
                        break;
                    case "Bc":
                        button.setImageResource(R.drawable.black_point);
                        break;
                    case "Wp":
                        button.setImageResource(R.drawable.white_prisoner);
                        break;
                    case "Bp":
                        button.setImageResource(R.drawable.black_prisoner);
                        break;
                    case "X":
                    default:
                        button.setImageResource(R.drawable.empty_stone);
                        break;
                }
            }

            index++;  // Move to the next token position
        }

        if (validPositionCount != 81) {
            Log.e("GameCounting", "Invalid board state received! Valid positions: " + validPositionCount);
        }
    }
}



