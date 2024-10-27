package com.example.sb.Model;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    private int teamScore;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<Player> teamList;

    public Team(String teamName, boolean isBlack) {
        this.teamList = new ArrayList<>();
        this.teamName = teamName;
        this.isBlack = isBlack;
    }

    public void addPlayer(Player player) {
        teamList.add(player);
    }
    public void removePlayer(Player player) {
        teamList.remove(player);
    }
}
