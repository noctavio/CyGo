package com.example.sb.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "settings")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Settings {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id; // Primary key with auto-increment


    @Column(name = "PIECECOLOR")
    private int pieceColor;


    @Column(name = "BOARDCOLOR")
    private int boardColor;


    @Column(name = "USERNAME")
    private String username;

    @Lob
    @OneToOne
    private User user;

    public Settings(User user, String username){
        this.user = user;
        this.boardColor = 1;
        this.pieceColor = 1;
        this.username = username;
    }
}

