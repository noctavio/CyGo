package com.example.sb.Model;

import lombok.Getter;
import lombok.Setter;
import java.util.Objects;

@Getter
@Setter
public class Stone {

    private String stoneType;
    private Integer x,y;

    private Goban board;

    public Stone(Goban board, Integer x, Integer y) {
        this.board = board;
        this.stoneType = "X";
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stone stone = (Stone) o;
        return Objects.equals(stoneType, stone.stoneType) && Objects.equals(x, stone.x) && Objects.equals(y, stone.y) && Objects.equals(board, stone.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stoneType, x, y, board);
    }
}
