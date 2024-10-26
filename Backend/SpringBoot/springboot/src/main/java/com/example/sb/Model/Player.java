package com.example.sb.Model;

import java.util.ArrayList;
import java.util.List;

import com.example.sb.Entity.TheProfile;
import com.example.sb.Entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class Player {

    @jakarta.persistence.Id
    @Id()
    @Column(name = "ID")
    @GeneratedValue
    private int id; //TODO probably necessary to be unique and primary key.

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")  // Foreign key pointing to User table
    private TheProfile profile; // Retrieves username, row id's, rank, clubname, and much more from (profile_table)

    @Column
    private Boolean isReady;
    @Column
    private List<String> mutedPlayers;
    @Column
    private int individualScore;

    public Player(int playerId) {
        this.id = playerId;
        this.isReady = false;  // Players are not ready when they join
        this.mutedPlayers = new ArrayList<>();
    }

    public void muteAPlayer(String muteTarget) {
        mutedPlayers.add(muteTarget);
    }

    public void unmuteAPlayer(String unmuteTarget) {
        mutedPlayers.remove(unmuteTarget);
    }

    public void muteAllEnemies(List<String> enemyTeam) {
        this.mutedPlayers.addAll(enemyTeam); // Add all at once
    }

    public void unMuteAllEnemies(List<String> enemyTeam) {
        this.mutedPlayers.removeAll(enemyTeam); // Add all at once
    }

    public void muteAll(List<String> all) {
        this.mutedPlayers.addAll(all);
    }

    public void unMuteAll(List<String> all) {
        this.mutedPlayers.removeAll(all);
    }
}
