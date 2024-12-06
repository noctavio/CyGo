package com.example.sb.Model;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class StoneJoseki {

    private String stoneType;
    private Boolean isCaptured;
    //private Boolean isEye; //TODO idk maybe
    private Integer x,y;

    private GobanJoseki board;

    public StoneJoseki(GobanJoseki board, Integer x, Integer y) {
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
        StoneJoseki stone = (StoneJoseki) o;
        return Objects.equals(stoneType, stone.stoneType) && Objects.equals(isCaptured, stone.isCaptured) && Objects.equals(x, stone.x) && Objects.equals(y, stone.y) && Objects.equals(board, stone.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stoneType, isCaptured, x, y, board);
    }
}
