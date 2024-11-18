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
    private Integer game_id;
    @Column(nullable = false)
    private Integer player_color; // 0 for Black, 1 for White
    @Column(nullable = false)
    private Integer x_position;
    @Column(nullable = false)
    private Integer y_position;
    @Column(nullable = false)
    private Integer move_number;

    public Tutorial(Integer gameId, Integer playerColor, Integer xPosition, Integer yPosition, Integer moveNumber) {
        this.game_id = gameId;
        this.player_color = playerColor;
        this.x_position = xPosition;
        this.y_position = yPosition;
        this.move_number = moveNumber;
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
                ", gameId=" + game_id +
                ", moveNumber=" + move_number +
                ", playerColor=" + player_color +
                ", xPosition=" + x_position +
                ", yPosition=" + y_position +
                '}';
    }
}
