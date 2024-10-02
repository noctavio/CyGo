package com.example.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import lombok.*;
import org.springframework.data.annotation.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {


    @jakarta.persistence.Id
    @Id()
    @Column(name = "ID")
    @GeneratedValue
    private int id;
    @Column(name = "USERNAME")
    private String username;
    @Column(name = "RATING")
    private Integer rating;
    @Column(name = "CLUBNAME")
    private String clubName;
    @Column(name = "WINS")
    private Integer wins;
    @Column(name = "LOSS")
    private Integer loss;

}