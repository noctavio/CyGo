package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Activity to display and manage the leaderboard.
 * Retrieves leaderboard data from a server and displays it in a list.
 *
 * @author Matthew Hill
 */
public class Leaderboard extends AppCompatActivity {

    // Arrays to store references to TextViews for each leaderboard entry
    private final TextView[] Dividers = new TextView[11];
    private final TextView[] UserNames = new TextView[11];
    private final TextView[] UserClubs = new TextView[11];
    private final TextView[] UserRanks = new TextView[11];
    private final TextView[] UserGames = new TextView[11];
    private final TextView[] UserWins = new TextView[11];
    private final TextView[] UserLosses = new TextView[11];

    // URL for leaderboard data
    private static final String URL_JSON_ARRAY = "http://coms-3090-051.class.las.iastate.edu:8080/profiles";

    /**
     * Called when the activity is created.
     * Initializes the layout, UI elements, and sets up the refresh button.
     *
     * @param savedInstanceState The saved state of the activity, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard); // Set the layout for the activity

        // Initialize TextViews for leaderboard data
        for (int i = 0; i < UserNames.length; i++) {
            Dividers[i] = findViewById(getResources().getIdentifier("divider" + i, "id", getPackageName()));
            UserNames[i] = findViewById(getResources().getIdentifier("UserName" + i, "id", getPackageName()));
            UserClubs[i] = findViewById(getResources().getIdentifier("UserClub" + i, "id", getPackageName()));
            UserRanks[i] = findViewById(getResources().getIdentifier("UserRank" + i, "id", getPackageName()));
            UserGames[i] = findViewById(getResources().getIdentifier("UserGames" + i, "id", getPackageName()));
            UserWins[i] = findViewById(getResources().getIdentifier("UserWins" + i, "id", getPackageName()));
            UserLosses[i] = findViewById(getResources().getIdentifier("UserLosses" + i, "id", getPackageName()));
        }

        // Initialize the refresh button and set a click listener to reload leaderboard
        Button btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLeaderboard();
            }
        });

        ImageButton homebtn = findViewById(R.id.homebtn); // Initialize your home button

        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to MainActivity
                Intent intent = new Intent(Leaderboard.this, MainActivity.class);

                // Optionally, if you want to clear the current activity stack (finish the current activity), use:
                // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Start the MainActivity
                startActivity(intent);

                // Finish the current activity if you want to close it after navigating
                finish();
            }
        });


        // Load initial leaderboard data when the activity is created
        makeJsonArrayReq();
    }

    /**
     * Makes a network request to retrieve the leaderboard data from the server.
     * The data is fetched as a JSON array and displayed on the UI.
     */
    private void makeJsonArrayReq() {
        String url = URL_JSON_ARRAY;

        // Create a request to fetch the leaderboard data as a JSON array
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Volley Response", response.toString());

                        // Clear existing data before populating new leaderboard data
                        for (int i = 0; i < UserNames.length; i++) {
                            if (UserNames[i] != null) UserNames[i].setText(""); // Clear previous names
                            if (UserClubs[i] != null) UserClubs[i].setText(""); // Clear previous clubs
                            if (UserRanks[i] != null) UserRanks[i].setText(""); // Clear previous ranks
                            if (UserGames[i] != null) UserGames[i].setText(""); // Clear previous games
                            if (UserWins[i] != null) UserWins[i].setText(""); // Clear previous wins
                            if (UserLosses[i] != null) UserLosses[i].setText(""); // Clear previous losses
                            if (Dividers[i] != null) Dividers[i].setText(""); // Clear previous dividers
                        }

                        // Populate the leaderboard with the fetched data
                        for (int i = 0; i < response.length() && i < UserNames.length; i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String name = jsonObject.getString("username");
                                String club = jsonObject.getString("club");
                                String rank = jsonObject.getString("rank");
                                String games = String.valueOf(jsonObject.getInt("games"));
                                String wins = String.valueOf(jsonObject.getInt("wins"));
                                String losses = String.valueOf(jsonObject.getInt("loss"));

                                // Set the TextViews with the fetched data
                                if (UserNames[i] != null) UserNames[i].setText(name);
                                if (UserClubs[i] != null) UserClubs[i].setText(club);
                                if (UserRanks[i] != null) UserRanks[i].setText(rank);
                                if (UserGames[i] != null) UserGames[i].setText(games);
                                if (UserWins[i] != null) UserWins[i].setText(wins);
                                if (UserLosses[i] != null) UserLosses[i].setText(losses);

                                // Optionally, update dividers if needed
                                if (Dividers[i] != null) Dividers[i].setText("--------------------------");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                    }
                });

        // Add the request to the Volley request queue
        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    /**
     * Refreshes the leaderboard by making a new request to fetch data.
     */
    private void refreshLeaderboard() {
        makeJsonArrayReq();
    }
}
