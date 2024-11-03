package com.example.sb.Model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Transient
    private Stone[][] board;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "lobby_id", referencedColumnName = "lobby_id")
    private Lobby lobby;

    public Goban(Lobby lobby) throws JsonProcessingException {
        this.lobby = lobby;
        this.board = new Stone[9][9];
        saveBoardState();
    }

    // Method to convert board to JSON and store it
    public void saveBoardState() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        this.boardState = mapper.writeValueAsString(this.board);
    }

    // Method to load board from JSON
    public void loadBoardState() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        this.board = mapper.readValue(this.boardState, Stone[][].class);
    }
}
