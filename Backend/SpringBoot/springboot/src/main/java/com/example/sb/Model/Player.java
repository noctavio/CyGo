package com.example.sb.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "Players")
public class Player {

    @Id
    @GeneratedValue
    private Integer player_id;
    private Boolean isReady;
    private Integer individualScore;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "profile_id", referencedColumnName = "profile_id")
    private TheProfile profile;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "lobby_id", referencedColumnName = "lobby_id")
    private Lobby lobby;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "team_id", referencedColumnName = "team_id")
    private Team team;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "muted_list", joinColumns = @JoinColumn(name = "player_id")) // Join to Players table
    @Column(name = "muted_type")
    private List<String> muted;

    public Player(TheProfile profile, Team team, Lobby lobby) {
        this.individualScore = 0;
        this.profile = profile;
        this.team = team;
        this.lobby = lobby;
        this.isReady = false;  // Players are not ready when they join
        this.muted = new ArrayList<>();
    }

    public String getUsername() {
        return profile != null ? profile.getUsername() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return individualScore == player.individualScore && Objects.equals(player_id, player.player_id)
                && Objects.equals(isReady, player.isReady) && Objects.equals(profile, player.profile)
                && Objects.equals(lobby, player.lobby) && Objects.equals(team, player.team) && Objects.equals(muted, player.muted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player_id, isReady, individualScore, profile, lobby, team, muted);
    }
}
