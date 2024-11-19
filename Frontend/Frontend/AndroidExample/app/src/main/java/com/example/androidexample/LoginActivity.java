package com.example.androidexample;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is the activity for users to login to the app
 * It provides functionality for user login, registration, update, 
 * and deletion using a Spring Boot backend server.
 *
 * @author Eden Basnet
 */

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;  // Define username EditText variable
    private EditText passwordEditText;  // Define password EditText variable
    private Button loginButton;         // Define login button variable
    private TextView loginStatusText;   // Define login status TextView variable
    private Button registerButton;      // Register button
    private Button updateButton;        // Update button
    private Button deleteButton;        // Delete button
    private RequestQueue requestQueue;  // Volley request queue
    private int userId;

    /**
     * Called when the activity is starting. Initializes UI components
     * and sets up click listeners for user actions.
     *
     * @param savedInstanceState The state of the activity when it was previously paused.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.login_username_edt);
        passwordEditText = findViewById(R.id.login_password_edt);
        loginButton = findViewById(R.id.login_login_btn);
        loginStatusText = findViewById(R.id.login_status_text);
        registerButton = findViewById(R.id.register_user_btn);
        updateButton = findViewById(R.id.update_user_btn);
        deleteButton = findViewById(R.id.delete_user_btn);
        requestQueue = Volley.newRequestQueue(this);

        registerButton.setOnClickListener(v -> showRegisterDialog());
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            loginUser(username, password);
        });

        updateButton.setOnClickListener(v -> showUpdateDialog());
        deleteButton.setOnClickListener(v -> showDeleteDialog());
    }

    /**
     * Displays an AlertDialog for registering a new user.
     */
    private void showRegisterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Register User");

        final EditText inputUsername = new EditText(this);
        inputUsername.setHint("Username");

        final EditText inputPassword = new EditText(this);
        inputPassword.setHint("Password");
        inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputUsername);
        layout.addView(inputPassword);
        builder.setView(layout);

        builder.setPositiveButton("Register", (dialog, which) -> {
            String username = inputUsername.getText().toString();
            String password = inputPassword.getText().toString();
            registerUser(username, password);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
     * Displays an AlertDialog for updating user details.
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update User");

        final EditText inputId = new EditText(this);
        inputId.setHint("User ID");
        inputId.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText newUsernameInput = new EditText(this);
        newUsernameInput.setHint("New Username");

        final EditText newPasswordInput = new EditText(this);
        newPasswordInput.setHint("New Password");
        newPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputId);
        layout.addView(newUsernameInput);
        layout.addView(newPasswordInput);
        builder.setView(layout);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String idString = inputId.getText().toString().trim();
            String newUsername = newUsernameInput.getText().toString().trim();
            String newPassword = newPasswordInput.getText().toString().trim();

            if (!idString.isEmpty()) {
                int userId = Integer.parseInt(idString);
                updateUser(userId, newUsername, newPassword);
            } else {
                loginStatusText.setText("User ID cannot be empty.");
                loginStatusText.setVisibility(View.VISIBLE);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
     * Displays an AlertDialog for deleting a user.
     */
    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete User");

        final EditText inputUserId = new EditText(this);
        inputUserId.setHint("User ID");
        inputUserId.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(inputUserId);

        builder.setPositiveButton("Delete", (dialog, which) -> {
            String userIdString = inputUserId.getText().toString().trim();
            if (!userIdString.isEmpty()) {
                int userId = Integer.parseInt(userIdString);
                deleteUser(userId);
            } else {
                loginStatusText.setText("User ID cannot be empty.");
                loginStatusText.setVisibility(View.VISIBLE);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
     * Registers a new user by sending their details to the server.
     *
     * @param username The username to register.
     * @param password The password to register.
     */
    private void registerUser(String username, String password) {
        String url = "http://10.90.72.226:8080/users/register"; // Register URL

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    loginStatusText.setText("Registration successful!");
                    loginStatusText.setVisibility(View.VISIBLE);
                },
                error -> {
                    loginStatusText.setText("Registration failed: " + error.getMessage());
                    loginStatusText.setVisibility(View.VISIBLE);
                });

        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Logs in a user by validating their credentials with the server.
     *
     * @param username The username to log in with.
     * @param password The password to log in with.
     */
    private void loginUser(String username, String password) {
        // Trim whitespace from inputs
        username = username.trim();
        password = password.trim();

        if (username.isEmpty() || password.isEmpty()) {
            loginStatusText.setText("Username and password cannot be empty.");
            loginStatusText.setVisibility(View.VISIBLE);
            return;
        }

        String url = "http://10.90.72.226:8080/users/login?username=" + username + "&password=" + password;
        // String url =  "http://coms-3090-051.class.las.iastate.edu:8080/users/login?username=" + username + "&password=" + password;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    loginStatusText.setText("Login successful!");
                    loginStatusText.setVisibility(View.VISIBLE);
                },
                error -> {
                    loginStatusText.setText("Login failed: " + error.getMessage());
                    loginStatusText.setVisibility(View.VISIBLE);
                });

        requestQueue.add(stringRequest);
    }

    /**
     * Updates the user's details on the server.
     *
     * @param userId      The ID of the user to update.
     * @param newUsername The new username.
     * @param newPassword The new password.
     */
    private void updateUser(int userId, String newUsername, String newPassword) {
        String updateUrl = "http://10.90.72.226:8080/users/update/" + userId; // Use stored userId

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", newUsername);
            jsonBody.put("password", newPassword);

        } catch (JSONException e) {
            e.printStackTrace();
            loginStatusText.setText("Error creating JSON: " + e.getMessage());
            loginStatusText.setVisibility(View.VISIBLE);
            return; // Exit the method on error
        }

        JsonObjectRequest updateRequest = new JsonObjectRequest(Request.Method.PUT, updateUrl, jsonBody,
                response -> {
                    loginStatusText.setText("User updated successfully!");
                    loginStatusText.setVisibility(View.VISIBLE);
                },
                error -> {
                    String errorMessage = "Update failed: " + error.getMessage();
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        errorMessage += " - " + new String(error.networkResponse.data);
                    }
                    loginStatusText.setText(errorMessage);
                    loginStatusText.setVisibility(View.VISIBLE);
                }) {
        };
        requestQueue.add(updateRequest);
    }

    /**
     * Deletes a user from the server.
     *
     * @param userId The ID of the user to delete.
     */
    private void deleteUser(int userId) {

        String deleteUrl = "http://10.90.72.226:8080/users/" + userId;

        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, deleteUrl,
                response -> {
                    loginStatusText.setText("User deleted successfully!");
                    loginStatusText.setVisibility(View.VISIBLE);
                },
                error -> {
                    loginStatusText.setText("Delete failed: " + error.getMessage());
                    loginStatusText.setVisibility(View.VISIBLE);
                });

        requestQueue.add(deleteRequest);
    }
}


