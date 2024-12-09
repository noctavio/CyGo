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
@Table(name = "friendsMessages")
@AllArgsConstructor
@NoArgsConstructor
public class FriendMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id; // Primary key

    @Column(name = "Sender", nullable = false)
    private String sender; // username associated with the sender of a message

    @Column(name = "Recipient", nullable = false)
    private String recipient; // username associated with the recipient of a message

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message; // Message content

    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now(); // Timestamp of the message


    public FriendMessage(String sender, String recipient, String message) {
        this.sender = sender;
        this.message = message;
        this.recipient = recipient;

    }
}
