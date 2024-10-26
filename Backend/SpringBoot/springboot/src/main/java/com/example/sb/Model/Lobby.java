package com.example.sb.Model;
<<<<<<< HEAD:Backend/SpringBoot/springboot/src/main/java/com/example/sb/Model/Lobby.java

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
=======
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
>>>>>>> 9a90bf0 (Figuring it out..., I want to complete pre-game before websocket as it is reliant on it heavily):Backend/SpringBoot/springboot/src/main/java/com/example/sb/Entity/Lobby.java
    }
}
