package com.example.sb.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "settings")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer settings_id; // Primary key
    private Integer pieceColor;
    private Integer boardColor;

    @OneToOne()
    @JoinColumn(name = "profile_id", referencedColumnName = "profile_id")
    private TheProfile profile;

    public Settings(TheProfile profile) {
        this.profile = profile;
        this.boardColor = 1;
        this.pieceColor = 1;
    }
}

