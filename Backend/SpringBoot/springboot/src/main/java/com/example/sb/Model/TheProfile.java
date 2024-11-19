package com.example.sb.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "profiles")

@NoArgsConstructor
@Getter
@Setter
public class TheProfile {

    @Id()
    @GeneratedValue()
    private Integer profile_id;

    @OneToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "club_id", referencedColumnName = "club_id")
    private Club club;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @JoinColumn(name = "settings_id", referencedColumnName = "settings_id")
    private Settings settings;

    private String profilePicture;
    private Integer wins;
    private Integer loss;
    private Integer games;
    private String rank;

    public TheProfile(User user) {
        this.user = user;
        this.profilePicture = "-/-";
        this.rank = "30 kyu";
        this.wins = 0;
        this.loss = 0;
        setGamesplayed();
    }

    // This is scary to read but all it does is check if the values are null and if they are it should internally save
    // games played as 0 + 0 to display 0 on the leaderboard.
    public void setGamesplayed() {
        this.games = (wins != null ? wins : 0) + (loss != null ? loss : 0);
    }

    public String getUsername() {
        return user != null ? user.getUsername() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TheProfile profile = (TheProfile) o;
        return Objects.equals(profile_id, profile.profile_id) && Objects.equals(user, profile.user) && Objects.equals(profilePicture, profile.profilePicture) && Objects.equals(wins, profile.wins) && Objects.equals(loss, profile.loss) && Objects.equals(games, profile.games) && Objects.equals(rank, profile.rank);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profile_id, user, profilePicture, wins, loss, games, rank);
    }
}