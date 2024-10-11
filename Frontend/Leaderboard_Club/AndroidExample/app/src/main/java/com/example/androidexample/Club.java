package com.example.androidexample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Club extends Activity {

    private EditText clubNameInput;
    private final EditText[] memberInputs = new EditText[20];
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.club);
        clubNameInput = findViewById(R.id.club_name_input);

        for (int i = 0; i < 20; i++) {
            String memberId = "member" + (i + 1) + "_input";
            int resId = getResources().getIdentifier(memberId, "id", getPackageName());
            memberInputs[i] = findViewById(resId);
        }

        Button createClubButton = findViewById(R.id.create_club_button);
        Button updateClubButton = findViewById(R.id.update_club_button);
        Button deleteClubButton = findViewById(R.id.delete_club_button);
        Button getClubButton = findViewById(R.id.get_club_button);


        requestQueue = Volley.newRequestQueue(this);

        createClubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    createClub();
                }
            }
        });

        updateClubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    updateClub();
                }
            }
        });

        deleteClubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteClub();
            }
        });

        getClubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getClubInfo();
            }
        });
    }

    private void createClub() {
        String url = "http://coms-3090-051.class.las.iastate.edu:8080/Club";

        JSONObject clubData = new JSONObject();
        try {
            clubData.put("CLUB", clubNameInput.getText().toString()); // Use "CLUB" as key
            for (int i = 0; i < 20; i++) {
                String memberKey = "member" + (i + 1);
                clubData.put(memberKey, memberInputs[i].getText().toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, clubData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(Club.this, "Club Created!", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Club.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(request);
    }

    private void updateClub() {
        String clubName = clubNameInput.getText().toString().toUpperCase();
        String url = "http://coms-3090-051.class.las.iastate.edu:8080/Club/update/" + clubName;

        JSONObject clubData = new JSONObject();
        try {
            clubData.put("CLUB", clubName);
            for (int i = 0; i < 20; i++) {
                String memberKey = "member" + (i + 1);
                clubData.put(memberKey, memberInputs[i].getText().toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, clubData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(Club.this, "Club Updated!", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = "Error: ";
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    message += new String(error.networkResponse.data);
                } else {
                    message += error.getMessage();
                }
                Log.e("UpdateClubError", message);
                Toast.makeText(Club.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(request);
    }

    private void deleteClub() {
        String clubName = clubNameInput.getText().toString().toLowerCase().trim(); // Ensure lowercase and trimmed
        if (clubName.isEmpty()) {
            Toast.makeText(this, "Please enter a club name to delete.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://coms-3090-051.class.las.iastate.edu:8080/Club/" + clubName;

        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(Club.this, "Club Deleted!", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message = "Error: ";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            message += new String(error.networkResponse.data);
                        } else {
                            message += error.getMessage();
                        }
                        Log.e("DeleteClubError", message); // Log the error for debugging
                        Toast.makeText(Club.this, message, Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(request);
    }

    private void getClubInfo() {
        String clubName = clubNameInput.getText().toString();
        String url = "http://coms-3090-051.class.las.iastate.edu:8080/Club/" + clubName;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("ClubResponse", response.toString());
                            clubNameInput.setText(response.getString("CLUB"));
                            for (int i = 0; i < 20; i++) {
                                String memberKey = "member" + (i + 1);
                                memberInputs[i].setText(response.optString(memberKey, ""));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Club.this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ClubError", "Error: " + error.toString());
                Toast.makeText(Club.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(request);
    }

    private boolean validateInputs() {
        if (clubNameInput.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter a club name", Toast.LENGTH_SHORT).show();
            return false;
        }

        for (EditText memberInput : memberInputs) {
            if (memberInput.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please enter all member names", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
}
