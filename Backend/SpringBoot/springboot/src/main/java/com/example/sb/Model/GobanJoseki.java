package com.example.sb.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "GobanJoseki")
@NoArgsConstructor
@Getter
@Setter
public class GobanJoseki {
    @Id()
    @GeneratedValue
    private Integer board_id;
    @Column(name = "board_state", length = 1000)
    private String boardState;
    @Transient
    private Stone[][] board;


    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "joseki_id", referencedColumnName = "joseki_id")
    @JsonIgnore
    private Joseki joseki;

    public GobanJoseki(Lobby lobby)  {
        this.board = new Stone[9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.board[i][j] = new Stone(this, i, j);
            }
        }
        saveBoardString();
    }

    // Setter for saving the board as a string
    public void saveBoardString() {
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
        GobanJoseki goban = (GobanJoseki) o;
        return Objects.equals(board_id, goban.board_id) && Objects.equals(boardState, goban.boardState) && Objects.equals(playerIdTurnList, goban.playerIdTurnList) && Objects.deepEquals(board, goban.board) && Objects.equals(lobby, goban.lobby);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board_id, boardState, playerIdTurnList, Arrays.deepHashCode(board), lobby);
    }
}
