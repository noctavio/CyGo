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
public class Settings {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id; // Primary key with auto-increment

    @Getter
    @Setter
    @Column(name = "PIECECOLOR")
    private int pieceColor;

    @Getter
    @Setter
    @Column(name = "BOARDCOLOR")
    private int boardColor;

    @Getter
    @Setter
    @Column(name = "USERNAME")
    private String username;
}

