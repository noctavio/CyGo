package com.example.sb.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "Messages")
@NoArgsConstructor
@Getter
@Setter
public class
ClubMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String userName;

    @Column
    private String clubName;

    @Column
    private String content;

    public ClubMessage(String clubName, String userName, String content) {
        this.clubName = clubName;
        this.userName = userName;
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClubMessage message = (ClubMessage) o;
        return Objects.equals(id, message.id) && Objects.equals(userName, message.userName) && Objects.equals(content, message.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, content);
    }
}