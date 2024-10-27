package com.example.sb.Entity;

import java.util.ArrayList;
import java.util.List;

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
    private int individualScore;

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
    @CollectionTable(name = "muted_players", joinColumns = @JoinColumn(name = "player_id")) // Join to Players table
    @Column(name = "muted_type") // Column name in the muted_players table
    private List<String> muted; // TODO this might be better as a hashMap :)

    public Player(TheProfile profile) {
        this.profile = profile;
        this.isReady = false;  // Players are not ready when they join
        this.muted = new ArrayList<>();
    }

    public void mute(String muteType) {
        muted.add(muteType);
    }

    public void unmute(String unmuteType) {
        muted.remove(unmuteType);
    }

    public String getUsername() {
        return profile != null ? profile.getUsername() : null;
    }
}
