package com.example.sb.Model;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import lombok.*;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Team {

    @jakarta.persistence.Id
    @Id()
    @Column(name = "ID")
    @GeneratedValue
    private int id; //TODO probably necessary to be unique and primary key.
    @Column
    private boolean isBlack;
    @Column
    private String teamName;
    @Column
    private List<Player> teamList;
    @Column
    private int teamScore;

    public Team(List<Player> team) {
        this.teamList = new ArrayList<>(team);
    }
}
