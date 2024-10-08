package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

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

        makeJsonArrayReq();
    }

    private void makeJsonArrayReq() {
        String url = URL_JSON_ARRAY;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Volley Response", response.toString());

                        for (int i = 0; i < response.length() && i < UserNames.length; i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String name = jsonObject.getString("username");
                                String club = jsonObject.getString("clubname");
                                String rank = jsonObject.getString("rating");
                                String games = jsonObject.getString("gamesplayed");
                                String wins = jsonObject.getString("wins");
                                String losses = jsonObject.getString("loss");

                                if (UserNames[i] != null) UserNames[i].setText(name);
                                if (UserClubs[i] != null) UserClubs[i].setText(club);
                                if (UserRanks[i] != null) UserRanks[i].setText(rank);
                                if (UserGames[i] != null) UserGames[i].setText(games);
                                if (UserWins[i] != null) UserWins[i].setText(wins);
                                if (UserWins[i] != null) Dividers[i].setText("/");
                                if (UserLosses[i] != null) UserLosses[i].setText(losses);

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
}