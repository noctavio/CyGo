package com.example.sb.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
@Entity
@Table(name = "Lobbies")
public class Lobby {

    @Id()
    @GeneratedValue
    private int lobby_id;
    private String hostName;
    private Integer gameTime;
    private boolean isFriendly;
    private final String boardSize = "9x9";

    @OneToOne(cascade = CascadeType.ALL) // Use CascadeType if needed
    @JoinColumn(name = "team1_id", referencedColumnName = "team_id") // Foreign key referencing Team
    private Team team1;

    @OneToOne(cascade = CascadeType.ALL) // Use CascadeType if needed
    @JoinColumn(name = "team2_id", referencedColumnName = "team_id")
    private Team team2;

    //@Column
    //private List<String> invitedPlayers;

    public Lobby(String hostname) {
        this.gameTime = 40;
        this.hostName = hostname;
        this.isFriendly = true;
        //this.invitedPlayers = new ArrayList<>();
    }
    public Lobby() {
        this.isFriendly = false;
    }
}
