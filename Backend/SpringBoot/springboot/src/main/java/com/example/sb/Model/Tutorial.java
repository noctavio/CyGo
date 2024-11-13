package com.example.sb.Model;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@Table(name = "tutorial")
@AllArgsConstructor
@NoArgsConstructor
public class Tutorial {

    @jakarta.persistence.Id
    @Column(name = "move_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int moveId; // Primary key

    @Column(name = "game_id", nullable = false)
    private int gameId;

    @Column(name = "player_color", nullable = false)
    private int playerColor; // 0 for Black, 1 for White

    @Column(name = "x_position", nullable = false)
    private int xPosition;

    @Column(name = "y_position", nullable = false)
    private int yPosition;

    @Column(name = "move_number", nullable = false)
    private int moveNumber;

    public Tutorial(Integer gameId, Integer playerColor, Integer xPosition, Integer yPosition, Integer moveNumber) {
        this.gameId = gameId;
        this.playerColor = playerColor;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.moveNumber = moveNumber;
    }
    // Log entity state before saving
    @PrePersist
    public void logEntityState() {
        System.out.println("Saving Tutorial: " + this);
    }
    @Override
    public String toString() {
        return "Tutorial{" +
                "moveId=" + moveId +
                ", gameId=" + gameId +
                ", moveNumber=" + moveNumber +
                ", playerColor=" + playerColor +
                ", xPosition=" + xPosition +
                ", yPosition=" + yPosition +
                '}';
    }
}
