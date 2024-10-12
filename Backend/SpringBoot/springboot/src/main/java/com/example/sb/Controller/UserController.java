package com.example.sb.Controller;

import com.example.sb.Entity.User;
import com.example.sb.Service.LeaderboardService;
import com.example.sb.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/users")
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        // Authenticate user
        if (userService.authenticateUser(username, password)) {
            return ResponseEntity.ok("Login successful");
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PutMapping("/update/{username}")
    public ResponseEntity<String> updateUser(@PathVariable String username, @RequestBody User user) {
        // TODO change this so you can change user/password separately without the other going null
        User existingUser = userService.getByUsername(username);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        if (user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());
        }

        if (user.getPassword() != null) {
            String newPassword = user.getPassword();
            //updates pass if it changed
            if (!newPassword.isEmpty()) {
                PasswordEncoder encoder = new BCryptPasswordEncoder();
                existingUser.setPassword(encoder.encode(newPassword));
            }
        }
        userService.updateUser(existingUser);
        return ResponseEntity.ok("User has been updated accordingly.");
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getByUsername(@PathVariable String username) {
        User user = userService.getByUsername(username);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    /**
     * This deletes a user's account and will also remove them from all other tables.
     * @param username username input
     * @return a status message
     */
    @DeleteMapping("/hardDelete/{username}")
    public ResponseEntity<String> deleteByUsername(@PathVariable String username) {
        boolean isDeletedFromUsers = userService.deleteByUsername(username);

        if (isDeletedFromUsers) {
            return ResponseEntity.ok("User deleted successfully.");
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }
}
