package com.example.sb.Model;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Lobby {
    private String hostName;
    private int id; //TODO probably necessary to be unique and primary key.
    private int gameTime;
    private boolean isFriendly;
    private final String format;
    private Team team1;
    private Team team2;

    public Lobby() {
        this.format ="9x9";
    }
}
