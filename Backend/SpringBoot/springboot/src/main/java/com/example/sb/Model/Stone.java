package com.example.sb.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Stones")
@NoArgsConstructor
@Getter
@Setter
public class Stone {

    @Id()
    @GeneratedValue
    private Integer stone_id;
    private String stoneType;
    private Boolean isCaptured;
    //private Boolean isEye; //TODO idk maybe
    private Integer x,y;

    @ManyToOne
    @JoinColumn(name = "board_id", referencedColumnName = "board_id")
    private Goban board;

    public Stone(Goban board, Integer x, Integer y) {
        this.board = board;
        this.stoneType = "X";
        this.isCaptured = false;
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stone stone = (Stone) o;
        return Objects.equals(stone_id, stone.stone_id) && Objects.equals(stoneType, stone.stoneType) && Objects.equals(isCaptured, stone.isCaptured) && Objects.equals(x, stone.x) && Objects.equals(y, stone.y) && Objects.equals(board, stone.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stone_id, stoneType, isCaptured, x, y, board);
    }
}
