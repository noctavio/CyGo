package com.example.sb.Model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Team {
    private int id; //TODO probably necessary to be unique and primary key.
    private boolean isBlack;
    private String name;
    private List<Player> team;
    private int teamScore;
    private int teamCount;

    public Team(List<Player> team) {
        this.team = new ArrayList<>(team);
    }
}
