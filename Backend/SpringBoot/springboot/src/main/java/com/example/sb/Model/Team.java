package com.example.sb.Model;
import jakarta.persistence.*;
import lombok.*;

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
    private boolean isBlack;
    private String teamName;
    private Integer teamScore;
    private Integer playerCount;

    // Two distinct player references instead of a list
    @OneToOne(cascade = CascadeType.ALL) // or use @OneToOne depending on your player-team relationship
    @JoinColumn(name = "player1_id", referencedColumnName = "player_id")
    private Player player1;

    @OneToOne(cascade = CascadeType.ALL) //
    @JoinColumn(name = "player2_id", referencedColumnName = "player_id")
    private Player player2;

    public Team(String teamName, boolean isBlack) {
        this.teamName = teamName;
        this.isBlack = isBlack;
        setPlayerCount();
        setTeamScore();
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

    public void setTeamScore() {
        teamScore = (player1 != null && player1.getIndividualScore() != null ? player1.getIndividualScore() : 0) +
                (player2 != null && player2.getIndividualScore() != null ? player2.getIndividualScore() : 0);
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
