package com.example.sb.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
@Setter
@Getter
@Entity
@Table(name = "clubMessages")
@AllArgsConstructor
@NoArgsConstructor
public class ClubMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id; // Primary key

    @Column(name = "club", nullable = false)
    private String club; // Club name associated with the message

    @Column(name = "username", nullable = false)
    private String username; // Username of the sender

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message; // Message content

    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now(); // Timestamp of the message


    public ClubMessage(String clubName, String username, String message) {
        this.club = clubName;
        this.message = message;
        this.username = username;

    }
}
