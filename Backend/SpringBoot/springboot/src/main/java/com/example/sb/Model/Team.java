package com.example.sb.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Teams")
public class Team {

    @Id()
    @GeneratedValue
    private Integer team_id; //TODO probably necessary to be unique and primary key.
    private Boolean isBlack;
    private String teamName;
    private Double teamCaptures;
    private Integer playerCount;
    private Boolean isTeamTurn;
    private Long timeRemaining;
    private Boolean isFinishedCounting;
    private Integer territoryCount;

    @Column(name = "lastMoveTimestamp")
    private LocalDateTime lastMoveTimestamp;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now(); // Timestamp when the move is made

    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now(); // Timestamp when the move is made

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "lobby_id", referencedColumnName = "lobby_id")
    private Lobby lobby;

    // Two distinct player references instead of a list
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "player1_id", referencedColumnName = "player_id")
    private Player player1;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "player2_id", referencedColumnName = "player_id")
    private Player player2;

    public Team(Lobby lobby, String teamName, boolean isBlack) {
        this.isTeamTurn = false;
        this.lobby = lobby;
        this.teamName = teamName;
        this.isBlack = isBlack;
        this.teamCaptures = 0.0;
        this.timeRemaining = ((lobby.getGameTime() / 2) * 60 * 1000);
        this.isFinishedCounting = false;
        this.territoryCount = null;
        setPlayerCount();
    }

    public void setPlayerCount() {
        if (this.player1 == null && this.player2 == null) {
            this.playerCount = 0;
        }
        else if (this.player1 != null && this.player2 != null) {
            this.playerCount = 2;
        }
        else {
            this.playerCount = 1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(team_id, team.team_id) && Objects.equals(isBlack, team.isBlack) && Objects.equals(teamName, team.teamName) && Objects.equals(teamCaptures, team.teamCaptures) && Objects.equals(playerCount, team.playerCount) && Objects.equals(isTeamTurn, team.isTeamTurn) && Objects.equals(timeRemaining, team.timeRemaining) && Objects.equals(isFinishedCounting, team.isFinishedCounting) && Objects.equals(territoryCount, team.territoryCount) && Objects.equals(lastMoveTimestamp, team.lastMoveTimestamp) && Objects.equals(lobby, team.lobby) && Objects.equals(player1, team.player1) && Objects.equals(player2, team.player2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team_id, isBlack, teamName, teamCaptures, playerCount, isTeamTurn, timeRemaining, isFinishedCounting, territoryCount, lastMoveTimestamp, lobby, player1, player2);
    }
}
