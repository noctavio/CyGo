package com.example.androidexample;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Joseki extends AppCompatActivity {
    private static final String BASE_URL = "http://coms-3090-051.class.las.iastate.edu:8080/gobanjoseki/";
    private String testingUsername = "Dummy1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joseki);

        try {
            System.out.println("Creating board for username: " + testingUsername);
            String response = createBoard(testingUsername);
            System.out.println("Board created: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageButton backArrowButton = findViewById(R.id.back_arrow);
        if (backArrowButton != null) { // Check for null
            backArrowButton.setOnClickListener(v -> {
                try {
                    System.out.println("Ending game for username: " + testingUsername);
                    String response = endGame(testingUsername);
                    System.out.println("Game ended: " + response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            System.out.println("Back arrow button not found!");
        }

        disableButtonListeners();
    }

    private void disableButtonListeners() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String buttonId = "button" + String.format("%02d", i) + String.format("%02d", j);
                int resId = getResources().getIdentifier(buttonId, "id", getPackageName());
                ImageButton button = findViewById(resId);
                if (button != null) {
                    button.setOnClickListener(null);
                } else {
                    System.out.println("Button not found: " + buttonId);
                }
            }
        }
    }

    public static String createBoard(String username) throws Exception {
        String endpoint = BASE_URL + username;
        HttpURLConnection connection = createConnection(endpoint, "POST");
        return getResponse(connection);
    }

    public static String endGame(String username) throws Exception {
        String endpoint = BASE_URL + username + "/end";
        HttpURLConnection connection = createConnection(endpoint, "DELETE");
        return getResponse(connection);
    }

    private static HttpURLConnection createConnection(String endpoint, String method) throws Exception {
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        return connection;
    }

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
