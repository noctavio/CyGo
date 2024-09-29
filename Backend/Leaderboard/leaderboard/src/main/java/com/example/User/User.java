package com.example.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Entity
@Data
@Table(name = "USER")
public class User {

    @jakarta.persistence.Id
    @Id()
    @Column(name = "ID")
    private int id;
    @Column(name = "USERNAME")
    private String username;
    @Column(name = "RATING")
    private int rating;
    @Column(name = "CLUBNAME")
    private String clubName;

    public User(String username, int rating, int id, String clubname) {
        this.username = username;
        this.rating = rating;
        this.id = id;
        this.clubName = clubname;
    }
    public User() {

    }
}