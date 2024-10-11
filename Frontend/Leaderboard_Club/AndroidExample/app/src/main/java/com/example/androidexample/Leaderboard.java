package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Leaderboard extends AppCompatActivity {

    private final TextView[] Dividers = new TextView[11];
    private final TextView[] UserNames = new TextView[11];
    private final TextView[] UserClubs = new TextView[11];
    private final TextView[] UserRanks = new TextView[11];
    private final TextView[] UserGames = new TextView[11];
    private final TextView[] UserWins = new TextView[11];
    private final TextView[] UserLosses = new TextView[11];
    private static final String URL_JSON_ARRAY = "http://coms-3090-051.class.las.iastate.edu:8080/users/leaderboard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard);

        // Initialize TextViews
        for (int i = 0; i < UserNames.length; i++) {
            Dividers[i] = findViewById(getResources().getIdentifier("divider" + i, "id", getPackageName()));
            UserNames[i] = findViewById(getResources().getIdentifier("UserName" + i, "id", getPackageName()));
            UserClubs[i] = findViewById(getResources().getIdentifier("UserClub" + i, "id", getPackageName()));
            UserRanks[i] = findViewById(getResources().getIdentifier("UserRank" + i, "id", getPackageName()));
            UserGames[i] = findViewById(getResources().getIdentifier("UserGames" + i, "id", getPackageName()));
            UserWins[i] = findViewById(getResources().getIdentifier("UserWins" + i, "id", getPackageName()));
            UserLosses[i] = findViewById(getResources().getIdentifier("UserLosses" + i, "id", getPackageName()));
        }

        // Initialize the refresh button
        Button btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLeaderboard();
            }
        });

        // Load the initial leaderboard data
        makeJsonArrayReq();
    }

    private void makeJsonArrayReq() {
        String url = URL_JSON_ARRAY;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Volley Response", response.toString());

                        // Clear existing data before populating
                        for (int i = 0; i < UserNames.length; i++) {
                            if (UserNames[i] != null) UserNames[i].setText(""); // Clear previous names
                            if (UserClubs[i] != null) UserClubs[i].setText(""); // Clear previous clubs
                            if (UserRanks[i] != null) UserRanks[i].setText(""); // Clear previous ranks
                            if (UserGames[i] != null) UserGames[i].setText(""); // Clear previous games
                            if (UserWins[i] != null) UserWins[i].setText(""); // Clear previous wins
                            if (UserLosses[i] != null) UserLosses[i].setText(""); // Clear previous losses
                            if (Dividers[i] != null) Dividers[i].setText(""); // Clear previous dividers
                        }

                        for (int i = 0; i < response.length() && i < UserNames.length; i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String name = jsonObject.getString("username");
                                String club = jsonObject.getString("clubname");
                                String rank = jsonObject.getString("rank"); // Ensure this key is correct
                                String games = jsonObject.getString("gamesplayed");
                                String wins = jsonObject.getString("wins");
                                String losses = jsonObject.getString("loss");

                                if (UserNames[i] != null) UserNames[i].setText(name);
                                if (UserClubs[i] != null) UserClubs[i].setText(club);
                                if (UserRanks[i] != null) UserRanks[i].setText(rank);
                                if (UserGames[i] != null) UserGames[i].setText(games);
                                if (UserWins[i] != null) UserWins[i].setText(wins);
                                if (UserLosses[i] != null) UserLosses[i].setText(losses);
                                if (Dividers[i] != null) Dividers[i].setText("/"); // Corrected

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

        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    private void refreshLeaderboard() {
        String url = "http://coms-3090-051.class.las.iastate.edu:8080/refresh";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Refresh Response", response.toString());
                        makeJsonArrayReq();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Refresh Error", error.toString());
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}








