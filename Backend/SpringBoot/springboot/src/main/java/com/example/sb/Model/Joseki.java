package com.example.sb.Model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "joseki") // Matches the table name in the database
public class Joseki {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Matches AUTO_INCREMENT
    @Column(name = "joseki_id") // Maps to the primary key column
    private Integer josekiId;

    @Column(name = "move_number", nullable = false) // Maps to move_number column
    private String moveNumber;

    @Column(name = "x_position", nullable = false) // Maps to move_position column
    private int xPosition;

    @Column(name = "y_position", nullable = false) // Maps to move_position column
    private int yPosition;

    @Column(name = "board_size") // Maps to board_size column
    private String boardSize;



    @Column(name = "board_id", unique = true) // Maps to board_id column
    private Integer boardId;
    @Column (name = "last_move")
    private String lastMove;

    // Constructors
    public Joseki(String moveNumber, int x, int y, GobanJoseki goban) {
        this.moveNumber = moveNumber;
        this.xPosition = x;
        this.yPosition = y;
        this.boardSize = "9x9";
        this.boardId = goban.getBoard_id();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Joseki joseki = (Joseki) o;
        return Objects.equals(josekiId, joseki.josekiId) &&
                Objects.equals(moveNumber, joseki.moveNumber) &&
                Objects.equals(xPosition, joseki.xPosition) &&
                Objects.equals(yPosition, joseki.yPosition) &&
                Objects.equals(boardSize, joseki.boardSize) &&
                Objects.equals(boardId, joseki.boardId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(josekiId, moveNumber, xPosition, yPosition, boardSize, boardId);
    }
}
