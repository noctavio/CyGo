package com.example.sb.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Lobbies")
public class Lobby {

    @Id()
    @GeneratedValue
    private Integer lobby_id;
    private String hostName;
    private Double gameTime;
    private Boolean isFriendly;
    private Boolean isGameInitialized;
    private final String boardSize = "9x9";

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "team1_id", referencedColumnName = "team_id")
    private Team team1;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "team2_id", referencedColumnName = "team_id")
    private Team team2;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "board_id", referencedColumnName = "board_id")
    private Goban goban;

    //@Column
    //private List<String> invitedPlayers;

    public Lobby(String hostname) {
        this.gameTime = 20.0;
        this.hostName = hostname;
        this.isFriendly = true;
        this.isGameInitialized = false;
        //this.invitedPlayers = new ArrayList<>();
    }

    public List<Player> getPlayersInLobby() {
        List<Player> players = new ArrayList<>();
        // Check if each player is non-null and add to the list
        if (team1.getPlayer1() != null) {
            players.add(team1.getPlayer1());
        }
        if (team1.getPlayer2() != null) {
            players.add(team1.getPlayer2());
        }
        if (team2.getPlayer1() != null) {
            players.add(team2.getPlayer1());
        }
        if (team2.getPlayer2() != null) {
            players.add(team2.getPlayer2());
        }
        return players;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lobby lobby = (Lobby) o;
        return Objects.equals(lobby_id, lobby.lobby_id) && Objects.equals(hostName, lobby.hostName) && Objects.equals(gameTime, lobby.gameTime) && Objects.equals(isFriendly, lobby.isFriendly) && Objects.equals(isGameInitialized, lobby.isGameInitialized) && Objects.equals(boardSize, lobby.boardSize) && Objects.equals(team1, lobby.team1) && Objects.equals(team2, lobby.team2) && Objects.equals(goban, lobby.goban);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lobby_id, hostName, gameTime, isFriendly, isGameInitialized, boardSize, team1, team2, goban);
    }
}
