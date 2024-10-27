package com.example.sb.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "profiles")

@NoArgsConstructor
@Getter
@Setter
public class TheProfile {

    @Id()
    @GeneratedValue
    private Integer profile_id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    private String profilepicture;
    private String clubname;
    private String clubpicture;
    private Integer wins;
    private Integer loss;
    private Integer games;
    private String rank;

    // This is scary to read but all it does is check if the values are null and if they are it should internally save
    // games played as 0 + 0 to display 0 on the leaderboard.
    public void setGamesplayed() {
        this.games = (wins != null ? wins : 0) + (loss != null ? loss : 0);
    }

    public String getUsername() {
        return user != null ? user.getUsername() : null;
    }
}
