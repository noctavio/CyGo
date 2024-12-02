package com.example.sb.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(name = "move_position", nullable = false) // Maps to move_position column
    private String movePosition;

    @Column(name = "board_size") // Maps to board_size column
    private String boardSize;

    @Column(name = "host_name") // Maps to host_name column
    private String hostName;

    @Column(name = "board_id", unique = true) // Maps to board_id column
    private Integer boardId;

    // Constructors
    public Joseki(String moveNumber, String movePosition) {
        this.moveNumber = moveNumber;
        this.movePosition = movePosition;
        this.boardSize = boardSize;
        this.hostName = hostName;
        this.boardId = boardId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Joseki joseki = (Joseki) o;
        return Objects.equals(josekiId, joseki.josekiId) &&
                Objects.equals(moveNumber, joseki.moveNumber) &&
                Objects.equals(movePosition, joseki.movePosition) &&
                Objects.equals(boardSize, joseki.boardSize) &&
                Objects.equals(hostName, joseki.hostName) &&
                Objects.equals(boardId, joseki.boardId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(josekiId, moveNumber, movePosition, boardSize, hostName, boardId);
    }
}
