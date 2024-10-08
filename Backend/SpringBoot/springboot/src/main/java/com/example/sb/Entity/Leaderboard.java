package com.example.sb.Entity;

import jakarta.persistence.*;

import lombok.*;
import org.springframework.data.annotation.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "LEADERBOARD")
public class Leaderboard {

    @jakarta.persistence.Id
    @Id()
    @Column(name = "ID")
    @GeneratedValue
    private int id;

    @Column(name = "USERNAME")
    private String username;
    @Column(name = "RANK")
    private String rank;
    @Column(name = "CLUBNAME")
    private String clubname;
    @Column(name = "GAMES")
    private Integer gamesplayed;
    @Column(name = "WINS")
    private Integer wins;
    @Column(name = "LOSS")
    private Integer loss;

    public void setGamesplayed() {
        this.gamesplayed = (wins != null ? wins : 0) + (loss != null ? loss : 0);
    }
}