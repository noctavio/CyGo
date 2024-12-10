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
    private Double teamScore;
    private Integer playerCount;
    private Boolean isTeamTurn;
    private Long timeRemaining;

    @Column(name = "lastMoveTimestamp")
    private LocalDateTime lastMoveTimestamp;

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
        this.teamScore = 0.0;
        this.timeRemaining = ((lobby.getGameTime() / 2) * 60 * 1000);
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
        return isBlack == team.isBlack && Objects.equals(team_id, team.team_id) &&
                Objects.equals(teamName, team.teamName) && Objects.equals(teamScore, team.teamScore)
                && Objects.equals(playerCount, team.playerCount) && Objects.equals(player1, team.player1)
                && Objects.equals(player2, team.player2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team_id, isBlack, teamName, teamScore, playerCount, player1, player2);
    }
}
