package com.example.sb.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "REGISTERED_USERS")
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id()
    @GeneratedValue
    private Integer user_id;
    @Column(unique = true)
    private String username;
    private String password;
}