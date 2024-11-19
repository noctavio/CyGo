package com.example.androidexample;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the activity that is responsible for managing the game board, 
 * player turns,and hints in a Go game.
 * It handles user interactions with the board, and provides strategic hints to players.
 * It communicates with a backend server to validate and update the game state.
 *
 * @author Eden Basnet
 */
public class RulesActivity extends AppCompatActivity {

    private static final int BOARD_SIZE = 9;
    private static final String serverUrl = "http://10.90.72.226:8080/goban";
    private GridLayout boardLayout;
    private RequestQueue requestQueue;
    private boolean isBlackTurn = true; // Black goes first
    private View lastPlacedStone = null; // Track the last placed stone
    private int userId = 1;
    private static final int EMPTY = 0;
    private static final int BLACK = 1;
    private static final int WHITE = 2;
    private int[][] boardState = new int[BOARD_SIZE][BOARD_SIZE];

    /**
     * Called when the activity is first created. Sets up the board and initializes components.
     *
     * @param savedInstanceState The saved instance state from a previous session, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        requestQueue = Volley.newRequestQueue(this);
        boardLayout = findViewById(R.id.boardLayout);

        // Initialize the board
        boardLayout.post(() -> {
            setupBoard();
        });


        // Hints button
        Button hintsButton = findViewById(R.id.hintsButton);
        hintsButton.setOnClickListener(v -> showHint());
    }

    /**
     * Sets up the board by dynamically creating ImageButton views for each cell.
     * Each button corresponds to a board position and allows players to place stones.
     */
    private void setupBoard() {
        int boardWidth = boardLayout.getWidth();
        int stoneSize = boardWidth / BOARD_SIZE;

        // Create buttons for each board cell
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ImageButton stoneButton = new ImageButton(this);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = stoneSize;
                params.height = stoneSize;
                params.rowSpec = GridLayout.spec(row);
                params.columnSpec = GridLayout.spec(col);
                params.setMargins(1, 1, 1, 1);

                stoneButton.setLayoutParams(params);
                stoneButton.setBackgroundColor(android.graphics.Color.TRANSPARENT);

                // Set up the on-click listener for placing a stone
                final int x = row;
                final int y = col;
                stoneButton.setOnClickListener(v -> placeStone(x, y, stoneButton));
                boardLayout.addView(stoneButton);
            }
        }
    }

    /**
     * Handles placing a stone on the board. Sends the move to the backend server for validation.
     *
     * @param x            The x-coordinate on the board.
     * @param y            The y-coordinate on the board.
     * @param stoneButton  The button representing the stone to be placed.
     */
    private void placeStone(int x, int y, ImageButton stoneButton) {
        String url = serverUrl + "/" + userId + "/place/" + x + "/" + y;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Handle valid move
                    updateBoardWithStone(x, y, stoneButton);
                    stoneButton.setEnabled(false);
                },
                error -> {
                    // Handle invalid move
                    Toast.makeText(this, "Move not valid", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(postRequest);
    }

    /**
     * Updates the board with a placed stone. Tracks the current turn and the last placed stone.
     *
     * @param x            The x-coordinate of the placed stone.
     * @param y            The y-coordinate of the placed stone.
     * @param stoneButton  The button representing the placed stone.
     */
    private void updateBoardWithStone(int x, int y, ImageButton stoneButton) {
        // Reset the previous stone to a regular stone if applicable
        if (lastPlacedStone != null) {
            lastPlacedStone.setBackgroundResource(isBlackTurn ? R.drawable.black_stone : R.drawable.white_stone);
        }

        // Set the new stone as the current stone
        stoneButton.setBackgroundResource(isBlackTurn ? R.drawable.black_stone_current : R.drawable.white_stone_current);

        // Update the board state
        boardState[x][y] = isBlackTurn ? BLACK : WHITE;

        // Update turn and track the last placed stone
        lastPlacedStone = stoneButton;
        isBlackTurn = !isBlackTurn;
    }

    /**
     * Displays a hint dialog to the player with strategic advice based on the current game state.
     */
    private void showHint() {
        String hintMessage = generateHintMessage();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hint")
                .setMessage(hintMessage)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    /**
     * Generates a hint message based on the current board state, offering strategic advice.
     *
     * @return A string containing the hint message.
     */
    private String generateHintMessage() {
        List<String> hints = new ArrayList<>();

        if (shouldGiveOffensiveHint()) {
            hints.add("Try placing stones near your opponent to gain control.");
        }
        if (shouldGiveDefensiveHint()) {
            hints.add("Strengthen your group by placing stones nearby.");
        }
        if (shouldGiveTerritoryHint()) {
            hints.add("Expand towards the edges to secure territory.");
        }
        if (shouldGiveCaptureHint()) {
            hints.add("You can capture an opponent’s group with your next move!");
        }
        if (shouldAvoidOverConcentrationHint()) {
            hints.add("Avoid over-concentrating stones in one area.");
        }

        // If no hints were generated, provide a default hint
        if (hints.isEmpty()) {
            return "Keep focusing on your strategy!";
        }

        // Join all hints with line breaks for better readability
        return String.join("\n", hints);
    }

    /**
     * Determines whether an offensive hint should be given, based on the presence of adjacent black stones.
     *
     * @return True if an offensive hint should be given, false otherwise.
     */
    private boolean shouldGiveOffensiveHint() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (boardState[row][col] == WHITE && hasAdjacentBlackStone(row, col)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if a given position has an adjacent black stone.
     *
     * @param row The row of the current position.
     * @param col The column of the current position.
     * @return True if there is an adjacent black stone, false otherwise.
     */
    private boolean hasAdjacentBlackStone(int row, int col) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (isInBounds(newRow, newCol) && boardState[newRow][newCol] == BLACK) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a given position is within the bounds of the board.
     *
     * @param row The row of the position.
     * @param col The column of the position.
     * @return True if the position is within bounds, false otherwise.
     */
    private boolean isInBounds(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    /**
     * Determines whether a defensive hint should be given, based on the presence of empty adjacent cells near black stones.
     *
     * @return True if a defensive hint should be given, false otherwise.
     */
    private boolean shouldGiveDefensiveHint() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (boardState[row][col] == BLACK && hasEmptyAdjacentCell(row, col)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if a given position has an empty adjacent cell.
     *
     * @param row The row of the current position.
     * @param col The column of the current position.
     * @return True if there is an empty adjacent cell, false otherwise.
     */
    private boolean hasEmptyAdjacentCell(int row, int col) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (isInBounds(newRow, newCol) && boardState[newRow][newCol] == EMPTY) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines whether a territory hint should be given to the player based on the presence of empty cells near the edges.
     *
     *  @return True if there are empty cells adjacent to the edges of the board and a black stone is nearby, false otherwise.
     */
    private boolean shouldGiveTerritoryHint() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            // Check if there are empty cells near the edges (top, bottom, left, right)
            if ((boardState[0][i] == EMPTY && hasAdjacentBlackStone(0, i)) || // Top edge
                    (boardState[BOARD_SIZE - 1][i] == EMPTY && hasAdjacentBlackStone(BOARD_SIZE - 1, i)) || // Bottom edge
                    (boardState[i][0] == EMPTY && hasAdjacentBlackStone(i, 0)) || // Left edge
                    (boardState[i][BOARD_SIZE - 1] == EMPTY && hasAdjacentBlackStone(i, BOARD_SIZE - 1))) { // Right edge
                return true;
            }
        }
        return false;
    }

    /**
     * Determines whether a capture hint should be given to the player. A capture hint is shown when an opponent's stone
     * has only one liberty left (an adjacent empty spot) and can be captured on the next move.
     *
     * @return True if there is a white stone with only one liberty remaining, false otherwise.
     */
    private boolean shouldGiveCaptureHint() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (boardState[row][col] == WHITE && hasSingleLiberty(row, col)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks whether a given position has only one liberty left. A liberty is defined as an adjacent empty space.
     * The method checks the four adjacent directions (up, down, left, right) to count how many liberties the stone has.
     *
     * @param row The row of the stone to check.
     * @param col The column of the stone to check.
     * @return True if the stone has exactly one liberty, false otherwise.
     */
    private boolean hasSingleLiberty(int row, int col) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        int libertyCount = 0;
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (isInBounds(newRow, newCol) && boardState[newRow][newCol] == EMPTY) {
                libertyCount++;
            }
        }
        return libertyCount == 1; // Only one liberty left
    }

    /**
     * Determines whether the player should be warned about over-concentration of black stones. Over-concentration occurs
     * when there is a cluster of black stones occupying a 2x2 grid. The player is advised to avoid concentrating too many
     * stones in a small area.
     *
     * @return True if there are more than one cluster of black stones in a 2x2 grid, false otherwise.
     */
    private boolean shouldAvoidOverConcentrationHint() {
        int blackStoneClusters = 0;

        for (int row = 0; row < BOARD_SIZE - 1; row++) {
            for (int col = 0; col < BOARD_SIZE - 1; col++) {
                // Check if there’s a cluster of black stones in a 2x2 grid
                if (boardState[row][col] == BLACK &&
                        boardState[row + 1][col] == BLACK &&
                        boardState[row][col + 1] == BLACK &&
                        boardState[row + 1][col + 1] == BLACK) {
                    blackStoneClusters++;
                }
            }
        }

        return blackStoneClusters > 1; // Example threshold for over-concentration
    }
}