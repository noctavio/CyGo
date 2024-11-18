package com.example.sb.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "settings")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer settings_id; // Primary key
    private Integer piece_color;
    private Integer board_color;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user; // When you set the user for some object(such as Settings), you don't have to set it manually
                       // this user reference already has a username which you can retrieve using user.getUsername();
    public Settings(User user){
        this.user = user;
        this.board_color = 1;
        this.piece_color = 1;
    }
}

