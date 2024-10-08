package com.example.sb.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.annotation.Id;

@Entity
@Data
@Table(name = "REGISTERED_USERS")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Getter
    @Setter
    @jakarta.persistence.Id
    @Id()
    @Column(name = "ID")
    @GeneratedValue
    private int id;
    @Column(unique = true, name = "USERNAME")
    private String username;
    @Column(name = "PASSWORD")
    private String password;
}