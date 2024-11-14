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

    private void setupBoard() {
        int boardWidth = boardLayout.getWidth();
        int stoneSize = boardWidth / BOARD_SIZE;

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

                final int x = row;
                final int y = col;
                stoneButton.setOnClickListener(v -> placeStone(x, y, stoneButton));
                boardLayout.addView(stoneButton);
            }
        }
    }

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

    private void showHint() {
        String hintMessage = generateHintMessage();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hint")
                .setMessage(hintMessage)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

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

    private boolean isInBounds(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

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