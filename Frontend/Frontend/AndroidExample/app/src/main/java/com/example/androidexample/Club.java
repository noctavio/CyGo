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

/**
 * This class represents the activity where users can manage their club.
 * It allows users to create, update, delete, and retrieve club information
 * through interaction with a server using Volley for networking.
 *
 * @author Matthew Hill
 */
public class Club extends Activity {

    private EditText clubNameInput;
    private final EditText[] memberInputs = new EditText[20];
    private RequestQueue requestQueue;

    /**
     * Called when the activity is created. Initializes UI components and sets up
     * button listeners for handling user actions to create, update, delete, or fetch club data.
     *
     * @param savedInstanceState The saved instance state, if available.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.club);
        clubNameInput = findViewById(R.id.club_name_input);

        // Initialize member inputs
        for (int i = 0; i < 20; i++) {
            String memberId = "member" + (i + 1) + "_input";
            int resId = getResources().getIdentifier(memberId, "id", getPackageName());
            memberInputs[i] = findViewById(resId);
        }

        // Initialize buttons
        Button createClubButton = findViewById(R.id.create_club_button);
        Button updateClubButton = findViewById(R.id.update_club_button);
        Button deleteClubButton = findViewById(R.id.delete_club_button);
        Button getClubButton = findViewById(R.id.get_club_button);

        requestQueue = Volley.newRequestQueue(this);

        // Set up button listeners
        createClubButton.setOnClickListener(v -> {
            if (validateInputs()) {
                createClub();
            }
        });

        updateClubButton.setOnClickListener(v -> {
            if (validateInputs()) {
                updateClub();
            }
        });

        deleteClubButton.setOnClickListener(v -> deleteClub());

        getClubButton.setOnClickListener(v -> getClubInfo());
    }

    /**
     * Creates a new club and sends the data to the server.
     * The club's name and member names are retrieved from input fields and sent in a POST request.
     */
    private void createClub() {
        String url = "http://coms-3090-051.class.las.iastate.edu:8080/Club";

        JSONObject clubData = new JSONObject();
        try {
            clubData.put("CLUB", clubNameInput.getText().toString());
            for (int i = 0; i < 20; i++) {
                String memberKey = "member" + (i + 1);
                clubData.put(memberKey, memberInputs[i].getText().toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, clubData,
                response -> Toast.makeText(Club.this, "Club Created!", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(Club.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }

    /**
     * Updates an existing club's information on the server.
     * It sends the club name and updated member names via a PUT request.
     */
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
                response -> Toast.makeText(Club.this, "Club Updated!", Toast.LENGTH_SHORT).show(),
                error -> {
                    String message = "Error: ";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        message += new String(error.networkResponse.data);
                    } else {
                        message += error.getMessage();
                    }
                    Log.e("UpdateClubError", message);
                    Toast.makeText(Club.this, message, Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(request);
    }

    /**
     * Deletes a club by sending a DELETE request to the server with the club name.
     * The name is sanitized to lowercase before sending.
     */
    private void deleteClub() {
        String clubName = clubNameInput.getText().toString().toLowerCase().trim();
        if (clubName.isEmpty()) {
            Toast.makeText(this, "Please enter a club name to delete.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://coms-3090-051.class.las.iastate.edu:8080/Club/" + clubName;

        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                response -> Toast.makeText(Club.this, "Club Deleted!", Toast.LENGTH_SHORT).show(),
                error -> {
                    String message = "Error: ";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        message += new String(error.networkResponse.data);
                    } else {
                        message += error.getMessage();
                    }
                    Log.e("DeleteClubError", message);
                    Toast.makeText(Club.this, message, Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(request);
    }

    /**
     * Fetches the club's information from the server based on the club name.
     * Displays the retrieved data in the respective input fields.
     */
    private void getClubInfo() {
        String clubName = clubNameInput.getText().toString();
        String url = "http://coms-3090-051.class.las.iastate.edu:8080/Club/" + clubName;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
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
                },
                error -> {
                    Log.e("ClubError", "Error: " + error.toString());
                    Toast.makeText(Club.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(request);
    }

    /**
     * Validates the inputs to ensure that all fields are filled out before submitting a request.
     * Displays a toast message if any input is missing.
     *
     * @return true if all fields are filled; false otherwise.
     */
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

