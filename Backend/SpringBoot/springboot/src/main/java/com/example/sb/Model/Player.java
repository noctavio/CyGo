package com.example.sb.Model;

import java.util.ArrayList;
import java.util.Date;
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
    private Boolean castBlackVote;
    private Boolean isTurn;

    //@Temporal(TemporalType.TIMESTAMP)
    //@Column(name = "startedTurn")
    //private Date startTime;

    //@Temporal(TemporalType.TIMESTAMP)
    //@Column(name = "endedTurn")
    //private Date endTime;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "profile_id", referencedColumnName = "profile_id")
    private TheProfile profile;

    @ManyToOne()
    @JsonIgnore
    @JoinColumn(name = "team_id", referencedColumnName = "team_id")
    private Team team;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "muted_list", joinColumns = @JoinColumn(name = "player_id")) // Join to Players table
    @Column(name = "muted_type")
    private List<String> muted;

    public Player(TheProfile profile, Team team) {
        this.profile = profile;
        this.team = team;
        this.castBlackVote = false;
        this.isReady = false;  // Players are not ready when they join
        this.isTurn = false;
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
        return Objects.equals(player_id, player.player_id) && Objects.equals(isReady, player.isReady) && Objects.equals(castBlackVote, player.castBlackVote) && Objects.equals(isTurn, player.isTurn) && Objects.equals(profile, player.profile) && Objects.equals(team, player.team) && Objects.equals(muted, player.muted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player_id, isReady, castBlackVote, isTurn, profile, team, muted);
    }
}
