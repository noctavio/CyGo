package com.example.sb.Controller;

import com.example.sb.Model.User;
import com.example.sb.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/users")
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/loggedIn")
    public List<User> getAllLoggedIn() {
        return userService.getAllLoggedIn();
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getByUsername(@PathVariable String username) {
        User user = userService.getByUsername(username);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PutMapping("/login/{username}/{password}")
    public ResponseEntity<String> login(@PathVariable String username, @PathVariable String password) {
        return userService.authenticateUser(username, password);
    }
    @PutMapping("/logout/{userId}")
    public ResponseEntity<String> logout(@PathVariable Integer userId) {
        return userService.logoutUser(userId);
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable Integer userId, @RequestBody User user) {
        // TODO change this so you can change user/password separately without the other going null
        Optional<User> existingUserOptional = userService.getByUserID(userId);

        // Check if the user exists
        if (existingUserOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User existingUser = existingUserOptional.get(); // Extract the User from Optional

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
        userService.updateUser(Optional.of(existingUser));
        return ResponseEntity.ok("User has been updated accordingly.");
    }

    /**
     * This deletes a user's account and will also remove them from all other tables.
     * @param id ID input
     * @return a status message
     */
    @DeleteMapping("/hardDelete/{id}")
    public ResponseEntity<String> deleteByUsername(@PathVariable Integer id) {
        boolean isDeletedFromUsers = userService.deleteByID(id);

        if (isDeletedFromUsers) {
            return ResponseEntity.ok("User deleted successfully.");
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }
}
