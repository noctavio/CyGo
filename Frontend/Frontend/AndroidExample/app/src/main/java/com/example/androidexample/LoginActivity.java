package com.example.androidexample;


import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

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
    private Button logoutButton;        // Button for logging user out
    private Button registerButton;      // Register button
    private Button updateButton;        // Update button
    private Button deleteButton;        // Delete 
    private Button btnToggleAdmin;    // Button for toggling admin status.
    private RequestQueue requestQueue;  // Volley request queue
    private int userId;                 // User ID for the currently logged-in user
    private boolean isAppClosed = false; // Flag to track if the app is closed


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

        // Check if user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences("LoginSession", MODE_PRIVATE);
        int storedUserId = sharedPreferences.getInt("userId", -1);

        btnToggleAdmin = findViewById(R.id.btn_toggle_admin);

        btnToggleAdmin.setOnClickListener(v -> {
            Log.d("LoginActivity", "Button clicked");  // Log button click
            toggleAdminStatus();
        });

        if (storedUserId != -1) {
            // User is already logged in, navigate to MainMenuActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Clear session if needed
            clearSession(this);
            initializeUI();
        }
    }

    private void toggleAdminStatus() {
        // Create the EditText views for user ID and password inside the dialog
        EditText userIdInput = new EditText(this);
        userIdInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        EditText passwordInput = new EditText(this);
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Create TextViews to guide the user
        TextView userIdText = new TextView(this);
        userIdText.setText("Enter User ID:");

        TextView passwordText = new TextView(this);
        passwordText.setText("Enter admin password:");

        // Create a LinearLayout to contain both TextViews and EditText fields
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Add the TextViews and EditTexts to the layout
        layout.addView(userIdText);
        layout.addView(userIdInput);
        layout.addView(passwordText);
        layout.addView(passwordInput);

        // Create an AlertDialog
        new AlertDialog.Builder(this)
                .setMessage("Please enter your user ID and admin password to toggle admin privileges:")
                .setView(layout) // Add the TextViews and EditText fields to the dialog
                .setPositiveButton("OK", (dialog, which) -> {
                    // Get the user ID and password entered by the user
                    String userIdString = userIdInput.getText().toString();
                    String password = passwordInput.getText().toString();

                    // Check if the input is not empty and the password is correct
                    if (!userIdString.isEmpty() && !password.isEmpty()) {
                        if (password.equals("cygo")) {
                            try {
                                // Convert the user ID to an integer
                                int userId = Integer.parseInt(userIdString);
                                // Call the method to toggle admin privileges
                                performToggleAdminRequest(userId);
                            } catch (NumberFormatException e) {
                                // Handle invalid user ID format
                                Toast.makeText(this, "Invalid ID format. Please enter a valid number.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Handle incorrect password
                            Toast.makeText(this, "Incorrect password. Admin privileges not toggled.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "User ID and password cannot be empty.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Handle cancel button press if needed
                    dialog.dismiss();
                })
                .show();
    }

     // Method to perform the actual network request
    private void performToggleAdminRequest(int userId) {
        String url = "http://coms-3090-051.class.las.iastate.edu:8080/users/toggleAdmin/" + userId;

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                response -> Toast.makeText(this, response, Toast.LENGTH_LONG).show(),
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        queue.add(stringRequest);
    }


    // Initialize the UI components
    private void initializeUI() {
        usernameEditText = findViewById(R.id.login_username_edt);
        passwordEditText = findViewById(R.id.login_password_edt);
        loginButton = findViewById(R.id.loginBtn);
        loginStatusText = findViewById(R.id.login_status_text);
        registerButton = findViewById(R.id.register_user_btn);
        updateButton = findViewById(R.id.update_user_btn);
        deleteButton = findViewById(R.id.delete_user_btn);
        requestQueue = Volley.newRequestQueue(this);
        logoutButton = findViewById(R.id.logoutBtn);

        registerButton.setOnClickListener(v -> showRegisterDialog());
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            loginUser(username, password);
        });

        updateButton.setOnClickListener(v -> showUpdateDialog());
        deleteButton.setOnClickListener(v -> showDeleteDialog());

        logoutButton.setOnClickListener(v -> logoutUser(this));
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

        builder.setNegativeButton("Cancel", (dialog, which) ->{
            hideKeyboard(inputId);
            dialog.cancel();
        });
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
        String url = "http://coms-3090-051.class.las.iastate.edu:8080/users/register"; // Register URL

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
                    loginStatusText.setTextColor(getResources().getColor(R.color.plain_yellow));
                    loginStatusText.setVisibility(View.VISIBLE);
                },
                error -> {
                    // Log error response body
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String responseBody = new String(error.networkResponse.data);
                        Log.e("Registration Error", "Response: " + responseBody);
                    }
//                    loginStatusText.setText("Registration failed: " + error.getMessage());
//                    loginStatusText.setTextColor(getResources().getColor(R.color.plain_yellow));
//                    loginStatusText.setVisibility(View.VISIBLE);
                });

        requestQueue.add(jsonObjectRequest);

    }

    /**
     * Logs in a user by validating their credentials with the server.
     *
     * @param username The username to log in with.
     * @param password The password to log in with.
     */
    private void loginUser(String username, String password){

        final String loginUsername = username.trim();
        password = password.trim();

        if (username.isEmpty() || password.isEmpty()) {
            loginStatusText.setText("Username and password cannot be empty.");
            loginStatusText.setVisibility(View.VISIBLE);
            return;
        }

        String loginUrl = "http://coms-3090-051.class.las.iastate.edu:8080/users/login/" +
                Uri.encode(loginUsername) + "/" + Uri.encode(password);

        StringRequest loginRequest = new StringRequest(Request.Method.PUT, loginUrl,
            response -> {
                    getUserId(loginUsername);

                    // Show success message
                    loginStatusText.setText("Login successful!");
                    loginStatusText.setTextColor(getResources().getColor(R.color.plain_yellow));
                    loginStatusText.setVisibility(View.VISIBLE);

                    // Navigate to the MainMenuActivity after a delay
                new Handler().postDelayed(() -> {
                    // Retrieve the isAdmin value from SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("LoginSession", MODE_PRIVATE);
                    boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false);

                    Intent intent;
                    if (isAdmin) {
                        // If user is an admin, go to AdminActivity
                        intent = new Intent(LoginActivity.this, AdminActivity.class);
                    } else {
                        // If user is not an admin, go to MainActivity
                        intent = new Intent(LoginActivity.this, MainActivity.class);
                    }
                    startActivity(intent);
                    finish();
                }, 2000);
            },error -> {
                loginStatusText.setText("Login failed: " + error.getMessage());
                loginStatusText.setTextColor(getResources().getColor(R.color.plain_yellow));
                loginStatusText.setVisibility(View.VISIBLE);
            });

        requestQueue.add(loginRequest);
    }

    /**
     * Updates the user's details on the server.
     *
     * @param userId      The ID of the user to update.
     * @param newUsername The new username.
     * @param newPassword The new password.
     */
    private void updateUser(int userId, String newUsername, String newPassword) {
        String updateUrl = "http://coms-3090-051.class.las.iastate.edu:8080/users/update/" + userId; // Use stored userId

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

        StringRequest updateRequest = new StringRequest(Request.Method.PUT, updateUrl,
                response -> {
                    loginStatusText.setText("User updated successfully!");
                    loginStatusText.setTextColor(getResources().getColor(R.color.plain_yellow));
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
            @Override
            public byte[] getBody() {
                return jsonBody.toString().getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };
        requestQueue.add(updateRequest);
    }

    // Logs out the currently logged-in user by sending a PUT request
    public static void logoutUser(Context context) {
        // Retrieve userId from session
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginSession", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1); // Assuming userId is stored in shared preferences

        if (userId == -1) {
            Toast.makeText(context, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
            clearSession(context); // Clear session if userId is not found
            navigateToWelcome(context);
            return;
        }

        String url = "http://coms-3090-051.class.las.iastate.edu:8080/users/logout/" + userId;

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                response -> {
                    Toast.makeText(context, "Logout successful", Toast.LENGTH_SHORT).show();
                    clearSession(context); // Clear session after logout
                    navigateToWelcome(context);
                },
                error -> {
                    Toast.makeText(context, "Logout failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


    /**
     * Deletes a user from the server.
     *
     * @param userId The ID of the user to delete.
     */
    private void deleteUser(int userId) {

        String deleteUrl = "http://coms-3090-051.class.las.iastate.edu:8080/users/hardDelete/" + userId;

        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, deleteUrl,
                response -> {
                    loginStatusText.setText("User deleted successfully!");
                    loginStatusText.setTextColor(getResources().getColor(R.color.plain_yellow));
                    loginStatusText.setVisibility(View.VISIBLE);
                },
                error -> {
                    loginStatusText.setText("Delete failed: " + error.getMessage());
                    loginStatusText.setTextColor(getResources().getColor(R.color.plain_yellow));
                    loginStatusText.setVisibility(View.VISIBLE);
                });

        requestQueue.add(deleteRequest);
    }

    // Hides the keyboard from the screen.
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // Clears the session data by removing the stored user ID.
    public static void clearSession(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear all saved data in LoginSession
        editor.apply();
        Toast.makeText(context, "Session cleared on app close.", Toast.LENGTH_SHORT).show();
    }

    // Navigates to the welcome activity.
    public static void navigateToWelcome(Context context) {
        Intent intent = new Intent(context, Welcome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }

    private void getUserId(String username) {
        // URL to get the user details using GET method
        String url = "http://coms-3090-051.class.las.iastate.edu:8080/users/" + Uri.encode(username);

        JsonObjectRequest getUserIdRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        int userId = response.getInt("user_id");
                        boolean isAdmin = response.isNull("isAdmin") ? false : response.getBoolean("isAdmin");

                        // Save userId and username in SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("LoginSession", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("userId", userId);
                        editor.putString("username", username);
                        editor.putBoolean("isAdmin", isAdmin);  // Save isAdmin status
                        editor.apply();

                    } catch (JSONException e) {
                        Toast.makeText(this, "Failed to retrieve user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Failed to retrieve user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(getUserIdRequest);
    }
}



