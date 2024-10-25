package com.example.sb.Model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Player {

    private int playerId; //TODO probably necessary to be unique and primary key.
    private String username;
    private Boolean isReady;
    private String rank;
    private String clubname;
    private List<String> mutedPlayers;
    private int individualScore;

    public Player(int playerId) {
        this.playerId = playerId;
        this.isReady = false;  // Players are not ready when they join
    }

    public void muteAPlayer(String muteTarget) {
        mutedPlayers.add(muteTarget);
    }

    public void muteAllEnemies(List<String> enemyTeam) {
        this.mutedPlayers.addAll(enemyTeam); // Add all at once
    }

    public void muteAll(List<String> all) {
        this.mutedPlayers.addAll(all);
    }
}
