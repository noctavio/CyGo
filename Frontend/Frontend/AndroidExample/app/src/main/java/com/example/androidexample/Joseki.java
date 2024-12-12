package com.example.androidexample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class Joseki extends AppCompatActivity {
    private static final String BASE_URL = "http://coms-3090-051.class.las.iastate.edu:8080/gobanjoseki/";
    private String username;  // Global username

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joseki);

        // Fetch username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginSession", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "Guest");  // Default to "Guest" if no username found

        // Now, you can use the 'username' variable in your network requests or wherever you need it.
        Log.d("Joseki", "Username: " + username);

        // Create the board in a background thread
        new Thread(() -> {
            try {
                System.out.println("Creating board for username: " + username);
                String response = createBoard(username);
                runOnUiThread(() -> System.out.println("Board created: " + response));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Set up the back arrow button listener to trigger end game
        ImageButton backArrowButton = findViewById(R.id.back_arrow);
        if (backArrowButton != null) {  // Check if the button is found
            backArrowButton.setOnClickListener(v -> {
                new Thread(() -> {
                    try {
                        System.out.println("Ending game for username: " + username);
                        String response = endGame(username);  // Call endGame when clicked
                        runOnUiThread(() -> System.out.println("Game ended: " + response));
                        Intent intent = new Intent(Joseki.this, MainActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            });
        } else {
            System.out.println("Back arrow button not found!");
        }
        ImageButton resetBoardButton = findViewById(R.id.reload_button);
        if (resetBoardButton != null) {
            resetBoardButton.setOnClickListener(v -> {
                new Thread(() -> {
                    try {
                        System.out.println("Resetting board for username: " + username);
                        String response = resetBoardState(username);
                        runOnUiThread(() -> System.out.println("Reset response: " + response));
                        fetchBoardState(username);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            });
        } else {
            System.out.println("Reset board button not found!");
        }
        ImageButton backButton = findViewById(R.id.back_button);  // Find the back_button by ID
        if (backButton != null) {  // Check if the button is found
            backButton.setOnClickListener(v -> {
                new Thread(() -> {
                    try {
                        System.out.println("Undo move for username: " + username);
                        String response = undoMove(username);  // Call undo when clicked
                        runOnUiThread(() -> System.out.println("Move undone: " + response));
                        fetchBoardState(username);  // Refresh the board state after undo
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            });
        } else {
            System.out.println("Back button not found!");
        }
        // Fetch board and stone data
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String url = "http://coms-3090-051.class.las.iastate.edu:8080/gobanjoseki/";
        String jsonResponse = fetchDataFromUrl(url);
        Log.d("JSON Response", jsonResponse);
        setupBoardButtonListeners();
        Log.d("Joseki", "Username just before fetching board state: " + username);
        fetchBoardState(username);
    }
    private String undoMove(String username) {
        String response = null;
        try {
            // Send a POST request to undo the move
            String urlString = BASE_URL + username + "/undo";  // Use the correct undo endpoint
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);  // To allow sending data if needed

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the server's response
                InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
                StringBuilder responseBuilder = new StringBuilder();
                int data = reader.read();
                while (data != -1) {
                    responseBuilder.append((char) data);
                    data = reader.read();
                }
                reader.close();
                response = responseBuilder.toString();
            } else {
                response = "Error undoing move! Response code: " + responseCode;
            }
            urlConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            response = "Error undoing move: " + e.getMessage();
        }
        return response;
    }

    private String resetBoardState(String username) {
        String response = null;
        try {
            // Send a GET request to reset the board
            String urlString = BASE_URL + "reset/board/" + username;
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the server's response
                InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
                StringBuilder responseBuilder = new StringBuilder();
                int data = reader.read();
                while (data != -1) {
                    responseBuilder.append((char) data);
                    data = reader.read();
                }
                reader.close();
                response = responseBuilder.toString();
            } else {
                response = "Error resetting board! Response code: " + responseCode;
            }
            urlConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            response = "Error resetting board: " + e.getMessage();
        }
        return response;
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
                String buttonID = "button" + x + y;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                ImageButton button = findViewById(resID);

                if (button != null) {
                    int finalX = x;
                    int finalY = y;
                    button.setOnClickListener(v -> new PlaceStoneTask(Joseki.this, username, finalX, finalY).execute());
                }
            }
        }
    }

    private static class PlaceStoneTask extends AsyncTask<Void, Void, String> {
        private WeakReference<Context> contextReference;
        private String username;
        private int x;
        private int y;

        public PlaceStoneTask(Context context, String username, int x, int y) {
            this.contextReference = new WeakReference<>(context);
            this.username = username;
            this.x = x;
            this.y = y;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = null;
            try {
                String urlString = "http://coms-3090-051.class.las.iastate.edu:8080/gobanjoseki/" + username + "/place/" + x + "/" + y;
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the server's response
                    InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
                    StringBuilder response = new StringBuilder();
                    int data = reader.read();
                    while (data != -1) {
                        response.append((char) data);
                        data = reader.read();
                    }
                    reader.close();
                    result = response.toString();
                } else {
                    result = "Error placing stone! Response code: " + responseCode;
                }
                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                result = "Error placing stone: " + e.getMessage();
            }
            return result + " at x: " + x + "y: " + y;
        }

        @Override
        protected void onPostExecute(String result) {
            Context context = contextReference.get();
            if (context != null) {
                // Display the server's response as a toast message
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                Log.d("PlaceStoneTask", "Server response: " + result);

                // Trigger a refresh of the board
                if (context instanceof Joseki) {
                    ((Joseki) context).fetchBoardState(username);
                }
            }
        }
    }

    private void fetchBoardState(String username) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String boardState = null;
                try {
                    String urlString = "http://coms-3090-051.class.las.iastate.edu:8080/gobanjoseki/" + username + "/board";

                    Log.d("Joseki", "Requesting board state from URL: " + urlString);

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
                        Log.e("Board", boardState);

                        Log.d("Joseki", "Raw board state response: " + boardState);
                    } else {
                        Log.e("Joseki", "Failed to fetch board state, Response code: " + responseCode);
                    }

                    urlConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Joseki", "Error fetching board state: " + e.getMessage());

                }
                return boardState;
            }

            @Override
            protected void onPostExecute(String boardState) {
                if (boardState != null) {
                    Log.d("Joseki", "Fetched Board State: " + boardState); // Log the fetched board state here
                    updateBoardWithState(boardState);
                } else {
                    Log.e("Joseki", "Failed to fetch board state");
                }
            }
        }.execute();
    }




    private void updateBoardWithState(String boardState) {
        Log.d("Joseki", "Board State: " + boardState); // Log the board state to see what's being received

        String[] cells = boardState.trim().split("\\s+");

        int validPositionCount = 0;  // Count valid board positions
        int index = 0;  // Index to track each button position based on column-major order

        for (String cell : cells) {
            cell = cell.trim();  // Remove any whitespace around the cell

            if (cell.equals("'") || cell.isEmpty()) {
                Log.d("Joseki", "Ignoring empty or standalone apostrophe token");
                continue;  // Skip standalone apostrophes or empty tokens
            }

            if (cell.equals("W") || cell.equals("B") || cell.equals("1") || cell.equals("2") || cell.equals("3") || cell.equals("X")) {
                validPositionCount++;
            } else {
                Log.w("Joseki", "Unexpected token: " + cell);
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
                        button.setImageResource(R.drawable.white_stone);
                        break;
                    case "B":
                        button.setImageResource(R.drawable.black_stone);
                        break;
                    case "Wc":
                        button.setImageResource(R.drawable.empty_stone);
                        break;
                    case "Bc":
                        button.setImageResource(R.drawable.empty_stone);
                        break;
                    case "1":
                        button.setImageResource(R.drawable.one);
                        break;
                    case "2":
                        button.setImageResource(R.drawable.two);
                        break;
                    case "3":
                        button.setImageResource(R.drawable.three);
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
            Log.e("Joseki", "Invalid board state received! Valid positions: " + validPositionCount);
        }
    }


    // Create the board for a username
    public static String createBoard(String username) throws Exception {
        String endpoint = BASE_URL + username;
        HttpURLConnection connection = createConnection(endpoint, "POST");
        return getResponse(connection);
    }

    // End the game for the username
    public static String endGame(String username) throws Exception {
        String endpoint = BASE_URL + username + "/end";
        HttpURLConnection connection = createConnection(endpoint, "DELETE");
        return getResponse(connection);
    }

    // Helper method to create the connection
    private static HttpURLConnection createConnection(String endpoint, String method) throws Exception {
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        return connection;
    }

    // Helper method to get the response from the server
    private static String getResponse(HttpURLConnection connection) throws Exception {
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {
            throw new Exception("HTTP error code: " + responseCode);
        }
    }
}

