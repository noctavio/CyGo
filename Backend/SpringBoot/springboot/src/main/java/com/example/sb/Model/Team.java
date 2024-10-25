package com.example.sb.Model;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Team {
    private int id; //TODO probably necessary to be unique and primary key.
    private boolean isBlack;
    private String name;
    private Player player1;
    private Player player2;
    private int teamScore;
}
