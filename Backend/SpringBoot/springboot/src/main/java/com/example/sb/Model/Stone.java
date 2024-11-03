package com.example.sb.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Stones")
@NoArgsConstructor
@Getter
@Setter
public class Stone {

    @Id()
    @GeneratedValue
    private Integer stone_id;
    private Boolean isBlack;
    private Boolean isCaptured;
    private Integer x,y;

    @ManyToOne
    @JoinColumn(name = "board_id", referencedColumnName = "board_id")
    private Goban board;

    public Stone(Boolean isBlack, Boolean isCaptured, Integer x, Integer y) {
        this.isBlack = isBlack;
        this.isCaptured = isCaptured;
        this.x = x;
        this.y = y;
    }

}
