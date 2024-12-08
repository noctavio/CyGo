package com.example.sb.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private Lobby lobby;

    public Goban(Lobby lobby)  {
        this.lobby = lobby;
        this.board = new Stone[9][9];
        this.playerIdTurnList = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.board[i][j] = new Stone(this, i, j);
            }
        }
        saveBoardString();
    }

    // Setter for saving the board as a string
    public void saveBoardString() {
        StringBuilder boardString = new StringBuilder();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                boardString.append(this.board[i][j].getStoneType()).append(" ");
            }
            boardString.append("\n");
        }

        this.boardState = boardString.toString();
    }

    // Setter for creating a board object based off the saved string data
    public void loadMatrixFromBoardString() {
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
