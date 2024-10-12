package com.example.sb.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.annotation.Id;

@Entity
@Data
@Table(name = "profile")

@AllArgsConstructor
@NoArgsConstructor
public class TheProfile {

    @Getter
    @Setter
    @jakarta.persistence.Id
    @Id()
    @Column(name = "ID")
    @GeneratedValue
    private int id;
    @Column(name = "USERNAME")
    private String username;
    @Column(name = "PROFILEPICTURE")
    private String profilepicture;
    @Column(name = "CLUBNAME")
    private String clubname;
    @Column(name = "CLUBPICTURE")
    private String clubpicture;
    @Column(name = "WINS")
    private Integer wins;
    @Column(name = "LOSS")
    private Integer loss;
    @Column(name = "GAMES")
    private Integer games;
    @Column(name = "RANK")
    private String rank;

    // This is scary to read but all it does is check if the values are null and if they are it should internally save
    // games played as 0 + 0 to display 0 on the leaderboard.
    public void setGamesplayed() {
        this.games = (wins != null ? wins : 0) + (loss != null ? loss : 0);
    }
}
