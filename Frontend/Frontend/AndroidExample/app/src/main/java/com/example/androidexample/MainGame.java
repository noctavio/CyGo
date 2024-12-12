package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.view.View;
import android.os.AsyncTask;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

public class MainGame extends AppCompatActivity {

    // Declare your UI elements
    TextView playerAName, playerARank, playerBName, playerBRank;
    TextView playerCName, playerCRank, playerDName, playerDRank;
    TextView gameTime1, gameTime2;
    boolean passTurn = false;
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
    int hostID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maingame);

        // Initialize your UI elements
        playerAName = findViewById(R.id.player_a_name);
        playerARank = findViewById(R.id.player_a_rank);
        playerBName = findViewById(R.id.player_b_name);
        playerBRank = findViewById(R.id.player_b_rank);
        playerCName = findViewById(R.id.player_c_name);
        playerCRank = findViewById(R.id.player_c_rank);
        playerDName = findViewById(R.id.player_d_name);
        playerDRank = findViewById(R.id.player_d_rank);
        gameTime1 = findViewById(R.id.central_clock);
        gameTime2 = findViewById(R.id.central_clock2);
        boardImageView = findViewById(R.id.go_board);
        new FetchBoardStoneDetails().execute();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String url = "http://coms-3090-051.class.las.iastate.edu:8080/lobby";
        String jsonResponse = fetchDataFromUrl(url);
        Log.d("JSON Response", jsonResponse);

        String usernameToFind = getIntent().getStringExtra("username");

        try {
            // Parse the JSON response to get the lobby data
            JSONArray lobbyData = new JSONArray(jsonResponse);
            JSONObject targetLobby = null;

            // Loop through each lobby to check if the user is in any team
            for (int i = 0; i < lobbyData.length(); i++) {
                JSONObject lobby = lobbyData.getJSONObject(i);

                // Check both teams in the lobby for the player
                JSONObject team1 = lobby.getJSONObject("team1");
                JSONObject player1 = team1.getJSONObject("player1");
                JSONObject player2 = team1.getJSONObject("player2");

                JSONObject team2 = lobby.getJSONObject("team2");
                JSONObject player3 = team2.getJSONObject("player1");
                JSONObject player4 = team2.getJSONObject("player2");

                // Get the usernames of all players in the lobby
                String player1Username = player1.getJSONObject("profile").getJSONObject("user").getString("username");
                String player2Username = player2.getJSONObject("profile").getJSONObject("user").getString("username");
                String player3Username = player3.getJSONObject("profile").getJSONObject("user").getString("username");
                String player4Username = player4.getJSONObject("profile").getJSONObject("user").getString("username");

                // Compare each player's username with the one you're looking for
                if (player1Username.equals(usernameToFind) || player2Username.equals(usernameToFind) ||
                        player3Username.equals(usernameToFind) || player4Username.equals(usernameToFind)) {
                    // If a match is found, store the lobby
                    targetLobby = lobby;
                    break;
                }
            }

            if (targetLobby != null) {
                lobbyId = targetLobby.optInt("lobby_id", -1);
                Log.d("LobbyID", "Lobby ID: " + lobbyId);

                Toast.makeText(this, "Lobby ID: " + lobbyId, Toast.LENGTH_LONG).show();

                int gameTime = targetLobby.optInt("gameTime", 0);
                Log.d("GameTime", "Game Time: " + gameTime);
                if (gameTime1 != null && gameTime2 != null) {
                    gameTime1.setText(gameTime + ":00");
                    gameTime2.setText(gameTime + ":00");
                } else {
                    Log.e("GameTime", "TextViews are not initialized properly");
                }

                JSONObject team1 = targetLobby.getJSONObject("team1");
                JSONObject playerA = team1.getJSONObject("player1");
                JSONObject playerB = team1.getJSONObject("player2");

                String playerANameText = playerA.getJSONObject("profile").getString("username");

                JSONObject userProfile = playerA.getJSONObject("profile").getJSONObject("user");
                if (userProfile.has("user_id")) {
                    hostID = userProfile.getInt("user_id");
                    Log.d("HostID", "Host ID: " + hostID); // Check if the value is correct
                } else {
                    Log.e("HostID", "user_id key not found in JSON.");
                }
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

        Integer getPlayerUserIdWithTurn = getPlayerUserIdWithTurn(jsonResponse);
        if (getPlayerUserIdWithTurn != null) {
            highlightActivePlayer(getPlayerUsernameWithTurn(jsonResponse));
        }
        initializeGame(hostID);
        setupBoardButtonListeners();
        Log.d("MainGame", "Lobby ID just before fetching board state: " + lobbyId);
        fetchBoardState(lobbyId);

        findViewById(R.id.pass_button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highlightActivePlayer(getPlayerUsernameWithTurn(fetchDataFromUrl(url)));
                int userId = getPlayerUserIdWithTurn(fetchDataFromUrl("http://coms-3090-051.class.las.iastate.edu:8080/lobby"));
                new PassTurnTask(userId).execute();
            }
        });
        findViewById(R.id.pass_button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highlightActivePlayer(getPlayerUsernameWithTurn(fetchDataFromUrl(url)));
                int userId = getPlayerUserIdWithTurn(fetchDataFromUrl("http://coms-3090-051.class.las.iastate.edu:8080/lobby"));
                new PassTurnTask(userId).execute();
            }
        });
        findViewById(R.id.home_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainGame.this, MainActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.message_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainGame.this, GameChat.class);
                intent.putExtra("hostID", hostID); // Pass hostID to GameChat
                startActivityForResult(intent, 1); // Start GameChat for result
            }
        });
    }
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
                        Log.d("MainGame", "Current Board: " + currentBoard);
                        Log.d("MainGame", "Current Stone: " + currentStone);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainGame.this, "Error parsing board/stone data", Toast.LENGTH_SHORT).show();
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
                Log.e("MainGame", "Invalid stone value: " + stone);
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
                Log.e("MainGame", "Invalid board value: " + board);
                break;
        }

    }

    // Use currentBoard and currentStone as needed in your game logic
    // For example:
    private void useBoardAndStoneValues() {
        if (!currentBoard.isEmpty() && !currentStone.isEmpty()) {
            // Handle logic based on currentBoard and currentStone
            Log.d("MainGame", "Using Board: " + currentBoard + " and Stone: " + currentStone);
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
                            String url = "http://coms-3090-051.class.las.iastate.edu:8080/lobby";
                            int playerID = getPlayerUserIdWithTurn(fetchDataFromUrl(url));
                            highlightActivePlayer(getPlayerUsernameWithTurn(fetchDataFromUrl(url)));
                            new PlaceStoneTask(playerID, finalX, finalY).execute();
                            passTurn = false;
                            fetchBoardState(lobbyId);
                            highlightActivePlayer(getPlayerUsernameWithTurn(fetchDataFromUrl(url)));
//                            boolean isUserBlackTeam = isUserBlackTeam(fetchDataFromUrl(url), getPlayerUsernameWithTurn(fetchDataFromUrl(url)));
//                            if (isUserBlackTeam == true) {
//                                lastMoveColor = "B";
//                            }
//                            else {
//                                lastMoveColor = "W";
//                            }
//                            lastMoveX = finalX;
//                            lastMoveY = finalY;
                        }
                    });
                }
            }
        }
    }
    public Boolean isUserBlackTeam(String jsonResponse, String username) {
        try {
            JSONArray gameData = new JSONArray(jsonResponse);  // Parse JSON string to JSONArray
            for (int i = 0; i < gameData.length(); i++) {
                JSONObject lobby = gameData.getJSONObject(i);

                JSONObject team1 = lobby.optJSONObject("team1");
                if (team1 != null && isUserOnTeam(team1, username)) {
                    return team1.getBoolean("isBlack");
                }

                JSONObject team2 = lobby.optJSONObject("team2");
                if (team2 != null && isUserOnTeam(team2, username)) {
                    return team2.getBoolean("isBlack");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private boolean isUserOnTeam(JSONObject team, String username) {
        for (int i = 1; i <= 2; i++) {  // Assumes player1 and player2
            JSONObject player = team.optJSONObject("player" + i);
            if (player != null && username.equals(player.optString("username"))) {
                return true;
            }
        }
        return false;
    }
    public Integer getPlayerUserIdWithTurn(String jsonResponse) {
        try {
            JSONArray lobbyData = new JSONArray(jsonResponse);
            if (lobbyData.length() == 0) {
                return null;  // Handle empty lobby data case
            }

            JSONObject lobby = lobbyData.optJSONObject(0); // Use optJSONObject instead of getJSONObject
            if (lobby == null) {
                return null;  // If lobby is null, return early
            }

            JSONObject team1 = lobby.optJSONObject("team1");
            JSONObject team2 = lobby.optJSONObject("team2");

            if (team1 != null) {
                int playerCount1 = team1.optInt("playerCount", 0);
                for (int i = 1; i <= playerCount1; i++) {
                    JSONObject player = team1.optJSONObject("player" + i);
                    if (player != null && player.optBoolean("isTurn", false)) {
                        JSONObject profile = player.optJSONObject("profile");
                        if (profile != null) {
                            JSONObject user = profile.optJSONObject("user");
                            if (user != null) {
                                Integer userId = user.optInt("user_id", -1); // Return the user_id
                                if (userId != -1) {
                                    return userId;
                                } else {
                                    Log.w("MainGame", "User ID not found in team1 player " + i);
                                }
                            } else {
                                Log.w("MainGame", "User profile is null for team1 player " + i);
                            }
                        } else {
                            Log.w("MainGame", "Profile is null for team1 player " + i);
                        }
                    }
                }
            }

            if (team2 != null) {
                int playerCount2 = team2.optInt("playerCount", 0);
                for (int i = 1; i <= playerCount2; i++) {
                    JSONObject player = team2.optJSONObject("player" + i);
                    if (player != null && player.optBoolean("isTurn", false)) {
                        JSONObject profile = player.optJSONObject("profile");
                        if (profile != null) {
                            JSONObject user = profile.optJSONObject("user");
                            if (user != null) {
                                Integer userId = user.optInt("user_id", -1); // Return the user_id
                                if (userId != -1) {
                                    return userId;
                                } else {
                                    Log.w("MainGame", "User ID not found in team2 player " + i);
                                }
                            } else {
                                Log.w("MainGame", "User profile is null for team2 player " + i);
                            }
                        } else {
                            Log.w("MainGame", "Profile is null for team2 player " + i);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e("MainGame", "Error parsing JSON in getPlayerUserIdWithTurn: " + e.getMessage());
        }
        return null; // Return null if no player with isTurn = true is found
    }


    public String getPlayerUsernameWithTurn(String jsonResponse) {
        try {
            JSONArray lobbyData = new JSONArray(jsonResponse);
            if (lobbyData.length() == 0) {
                return null;  // Handle empty lobby data case
            }

            JSONObject lobby = lobbyData.optJSONObject(1); // Use optJSONObject instead of getJSONObject
            if (lobby == null) {
                return null;  // If lobby is null, return early
            }

            JSONObject team1 = lobby.optJSONObject("team1");
            JSONObject team2 = lobby.optJSONObject("team2");

            if (team1 != null) {
                int playerCount1 = team1.optInt("playerCount", 0);
                for (int i = 1; i <= playerCount1; i++) {
                    JSONObject player = team1.optJSONObject("player" + i);
                    if (player != null && player.optBoolean("isTurn", false)) {
                        JSONObject profile = player.optJSONObject("profile");
                        if (profile != null) {
                            JSONObject user = profile.optJSONObject("user");
                            if (user != null) {
                                return user.optString("username", "Unknown Player");
                            }
                        }
                    }
                }
            }

            if (team2 != null) {
                int playerCount2 = team2.optInt("playerCount", 0);
                for (int i = 1; i <= playerCount2; i++) {
                    JSONObject player = team2.optJSONObject("player" + i);
                    if (player != null && player.optBoolean("isTurn", false)) {
                        // Get the username from the profile object
                        JSONObject profile = player.optJSONObject("profile");
                        if (profile != null) {
                            JSONObject user = profile.optJSONObject("user");
                            if (user != null) {
                                return user.optString("username", "Unknown Player");
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e("MainGame", "Error parsing JSON in getPlayerUsernameWithTurn: " + e.getMessage());
        }
        return null; // Return null if no player with isTurn = true is found
    }


    private void highlightActivePlayer(String playerString) {
        // Reset all names to default color or style
        playerAName.setTextColor(Color.parseColor("#FFFFFF"));
        playerBName.setTextColor(Color.parseColor("#FFFFFF"));
        playerCName.setTextColor(Color.parseColor("#FFFFFF"));
        playerDName.setTextColor(Color.parseColor("#FFFFFF"));

        if (playerString.equals(playerAName.getText().toString())) {
            playerAName.setTextColor(Color.parseColor("#FFFF00"));
        } else if (playerString.equals(playerBName.getText().toString())) {
            playerBName.setTextColor(Color.parseColor("#FFFF00"));
        } else if (playerString.equals(playerCName.getText().toString())) {
            playerCName.setTextColor(Color.parseColor("#FFFF00"));
        } else if (playerString.equals(playerDName.getText().toString())) {
            playerDName.setTextColor(Color.parseColor("#FFFF00"));
        }
        // Highlight the current player
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
                String urlString = "http://coms-3090-051.class.las.iastate.edu:8080/goban/" + userId + "/place/" + x + "/" + y;
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    result = "Stone placed successfully!";
                } else {
                    result = "Failed to place stone!";
                }

                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                result = "Error placing stone: " + e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(MainGame.this, result, Toast.LENGTH_SHORT).show();
        }
    }
    private void initializeGame(int hostId) {
        try {
            String urlString = "http://coms-3090-051.class.las.iastate.edu:8080/lobby/" + hostId + "/initialize/game";
            System.out.println("Initializing game with hostId: " + hostId);
            System.out.println("Request URL: " + urlString);

            HttpURLConnection urlConnection = (HttpURLConnection) new URL(urlString).openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);

            urlConnection.connect();
            System.out.println("Connection established. Sending request...");

            int responseCode = urlConnection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Game initialized successfully");
            } else {
                System.out.println("Failed to initialize game. Response code: " + responseCode);
            }

            // Close the connection
            urlConnection.disconnect();
            System.out.println("Connection closed.");

        } catch (Exception e) {
            // Catch any exceptions and log the error message
            e.printStackTrace();
            System.out.println("Error initializing game: " + e.getMessage());
        }
    }

    private class PassTurnTask extends AsyncTask<Void, Void, String> {
        private int userId;

        public PassTurnTask(int userId) {
            this.userId = userId;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = null;
            try {
                String urlString = "http://coms-3090-051.class.las.iastate.edu:8080/goban/" + userId + "/pass";
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                // Set up the connection properties
                urlConnection.setRequestMethod("PUT");
                urlConnection.setDoOutput(true);

                // Connect and get the response
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    result = "Turn passed successfully!";
                    passTurn = true;  // Set the pass flag
                } else {
                    result = "Failed to pass turn!";
                }

                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                result = "Error passing turn: " + e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(MainGame.this, result, Toast.LENGTH_SHORT).show();
        }
    }


    private void fetchBoardState(int lobbyId) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String boardState = null;
                try {
                    String urlString = "http://coms-3090-051.class.las.iastate.edu:8080/goban/" + lobbyId + "/board";

                    Log.d("MainGame", "Requesting board state from URL: " + urlString);

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

                        Log.d("MainGame", "Raw board state response: " + boardState);
                    } else {
                        Log.e("MainGame", "Failed to fetch board state, Response code: " + responseCode);
                    }

                    urlConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("MainGame", "Error fetching board state: " + e.getMessage());
                }
                return boardState;
            }

            @Override
            protected void onPostExecute(String boardState) {
                if (boardState != null) {
                    Log.d("MainGame", "Fetched Board State: " + boardState); // Log the fetched board state here
                    updateBoardWithState(boardState);
                } else {
                    Log.e("MainGame", "Failed to fetch board state");
                }
            }
        }.execute();
    }




    private void updateBoardWithState(String boardState) {
        Log.d("MainGame", "Board State: " + boardState); // Log the board state to see what's being received

        String[] cells = boardState.trim().split("\\s+");

        int validPositionCount = 0;  // Count valid board positions
        int index = 0;  // Index to track each button position based on column-major order

        for (String cell : cells) {
            cell = cell.trim();  // Remove any whitespace around the cell

            if (cell.equals("'") || cell.isEmpty()) {
                Log.d("MainGame", "Ignoring empty or standalone apostrophe token");
                continue;  // Skip standalone apostrophes or empty tokens
            }

            if (cell.equals("W") || cell.equals("B") || cell.equals("Wc") || cell.equals("Bc") || cell.equals("X")) {
                validPositionCount++;
            } else {
                Log.w("MainGame", "Unexpected token: " + cell);
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
                        button.setImageResource(R.drawable.empty_stone);
                        break;
                    case "Bc":
                        button.setImageResource(R.drawable.empty_stone);
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
            Log.e("MainGame", "Invalid board state received! Valid positions: " + validPositionCount);
        }
    }
}



