package com.example.sb.Model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

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
    private Boolean isLoggedIn;
    private Boolean isAdmin;

    public User (User userJSON, String encodedPassword) {
        this.username = userJSON.getUsername();
        this.password = encodedPassword;
        this.isLoggedIn = false;
        this.isAdmin = userJSON.getIsAdmin();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(user_id, user.user_id) && Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(isLoggedIn, user.isLoggedIn) && Objects.equals(isAdmin, user.isAdmin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_id, username, password, isLoggedIn, isAdmin);
    }
}