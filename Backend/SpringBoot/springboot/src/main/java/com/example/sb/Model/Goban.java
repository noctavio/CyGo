package com.example.sb.Model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "Gobans")
@NoArgsConstructor
@Getter
@Setter
public class Goban {
    @Id()
    @GeneratedValue
    private Integer board_id;
    @Column(name = "board_state", length = 1000)
    private String boardState;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "player_turn_order", joinColumns = @JoinColumn(name = "board_id")) // Join to Players table
    @Column(name = "player_id")
    private List<Integer> playerIdTurnList;
    @Transient
    private Stone[][] board;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "lobby_id", referencedColumnName = "lobby_id")
    private Lobby lobby;

    private static final int[] DIR_X = {-1, 1, 0, 0};
    private static final int[] DIR_Y = {0, 0, -1, 1};

    public Goban(Lobby lobby)  {
        this.lobby = lobby;
        this.board = new Stone[9][9];
        this.playerIdTurnList = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.board[i][j] = new Stone(this, i, j);
            }
        }
        saveBoardState();
    }

    // Check if a stone is captured
    public boolean checkIfCaptured(Stone[][] board, int x, int y) {
        String stone = board[x][y].getStoneType();
        if (stone.equals("X")) {
            return false;  // No stone at the position
        }

        // Perform a DFS/BFS to find the group of stones
        boolean[][] visited = new boolean[board.length][board[0].length];
        Set<String> group = new HashSet<>();
        Set<String> liberties = new HashSet<>();
        exploreGroup(board, x, y, stone, visited, group, liberties);

        // If there are no liberties, the group is captured
        return liberties.isEmpty();
    }

    // Helper method to explore a group of connected stones and its liberties
    private void exploreGroup(Stone[][] board, int x, int y, String stone, boolean[][] visited, Set<String> group, Set<String> liberties) {
        if (x < 0 || x >= board.length || y < 0 || y >= board[0].length || visited[x][y] || !board[x][y].getStoneType().equals(stone)) {
            return;  // Out of bounds, already visited, or not the same stone
        }

        visited[x][y] = true;
        group.add(x + "," + y);

        // Check all adjacent positions for liberties or more stones in the group
        for (int i = 0; i < 4; i++) {
            int newX = x + DIR_X[i];
            int newY = y + DIR_Y[i];

            if (newX >= 0 && newX < board.length && newY >= 0 && newY < board[0].length) {
                if (board[newX][newY].getStoneType().equals("X")) {
                    liberties.add(newX + "," + newY);  // Add liberty (empty space)
                } else if (board[newX][newY].getStoneType().equals(stone)) {
                    exploreGroup(board, newX, newY, stone, visited, group, liberties);  // Recursively explore the group
                }
            }
        }
    }

    // Capture the group of stones
    public void captureGroup(Stone[][] board, Set<String> group) {
        for (String position : group) {
            String[] pos = position.split(",");
            int x = Integer.parseInt(pos[0]);
            int y = Integer.parseInt(pos[1]);
            board[x][y].setStoneType("X");  // Remove the stone //TODO implement capture type, Bc/Wc
        }
    }

    // Method to convert board to JSON and store it
    public void saveBoardState() {
        //ObjectMapper mapper = new ObjectMapper();
        //this.boardState = mapper.writeValueAsString(this.board);
        StringBuilder boardString = new StringBuilder();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                boardString.append(this.board[i][j].getStoneType()).append(" ");
            }
            boardString.append("\n");  // New line after each row
        }

        this.boardState = boardString.toString();
    }

    public void loadBoardState() {
        // Split the boardState string by new lines to get each row
        this.board = new Stone[9][9];
        String[] rows = this.boardState.trim().split("\n");

        for (int i = 0; i < 9; i++) {
            // Split each row by spaces to get each stone type
            String[] stoneTypes = rows[i].trim().split(" ");

            for (int j = 0; j < 9; j++) {
                // Create a new Stone object and set the board reference and coordinates
                Stone stone = new Stone(this, i, j);

                // Set stone attributes based on the parsed stoneType value
                String type = stoneTypes[j];
                switch (type) {
                    case "X":
                        stone.setStoneType("X");
                        stone.setIsCaptured(false);
                        break;
                    case "B":
                        stone.setStoneType("B");
                        stone.setIsCaptured(false);
                        break;
                    case "W":
                        stone.setStoneType("W");
                        stone.setIsCaptured(false);
                        break;
                    case "Bc":
                        stone.setStoneType("Bc");
                        stone.setIsCaptured(true);
                        break;
                    case "Wc":
                        stone.setStoneType("Wc");
                        stone.setIsCaptured(true);
                        break;
                    default:
                        throw new IllegalArgumentException("Unexpected stone type: " + type);
                }

                // Assign the stone to the board matrix
                this.board[i][j] = stone;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goban goban = (Goban) o;
        return Objects.equals(board_id, goban.board_id) && Objects.equals(boardState, goban.boardState) && Objects.equals(playerIdTurnList, goban.playerIdTurnList) && Objects.deepEquals(board, goban.board) && Objects.equals(lobby, goban.lobby);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board_id, boardState, playerIdTurnList, Arrays.deepHashCode(board), lobby);
    }
}
