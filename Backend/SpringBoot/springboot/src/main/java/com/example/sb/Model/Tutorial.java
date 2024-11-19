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

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer move_id; // Primary key
    @Column(nullable = false)
    private Integer gameId;
    @Column(nullable = false)
    private Integer playerColor; // 0 for Black, 1 for White
    @Column(nullable = false)
    private Integer x_position;
    @Column(nullable = false)
    private Integer y_position;
    @Column(nullable = false)
    private Integer moveNumber;

    public Tutorial(Integer gameId, Integer playerColor, Integer xPosition, Integer yPosition, Integer moveNumber) {
        this.gameId = gameId;
        this.playerColor = playerColor;
        this.x_position = xPosition;
        this.y_position = yPosition;
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
                "moveId=" + move_id +
                ", gameId=" + gameId +
                ", moveNumber=" + moveNumber +
                ", playerColor=" + playerColor +
                ", xPosition=" + x_position +
                ", yPosition=" + y_position +
                '}';
    }
}
