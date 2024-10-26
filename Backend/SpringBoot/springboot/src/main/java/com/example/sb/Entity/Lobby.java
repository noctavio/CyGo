package com.example.sb.Model;
import jakarta.persistence.*;
import lombok.*;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class Lobby {

    @jakarta.persistence.Id
    @Id()
    @Column(name = "ID")
    @GeneratedValue
    private int id; //TODO probably necessary to be unique and primary key.
    @Column
    private String hostName;
    @Column
    private int gameTime;
    @Column
    private boolean isFriendly;
    @Column
    private final String boardSize;
    @Column
    private Team team1;
    @Column
    private Team team2;

    public Lobby() {
        this.boardSize ="9x9";
    }
}
