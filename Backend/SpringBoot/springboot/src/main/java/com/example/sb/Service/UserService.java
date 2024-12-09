package com.example.sb.Service;

import com.example.sb.Model.Player;
import com.example.sb.Model.TheProfile;
import com.example.sb.Model.User;
import com.example.sb.Repository.PlayerRepository;
import com.example.sb.Repository.TheProfileRepository;
import com.example.sb.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TheProfileService theProfileService;
    @Autowired
    private TheProfileRepository profileRepository;
    @Autowired
    private PlayerRepository playerRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ResponseEntity<String> registerUser(User userJSON) {
        // Validate username input
        if (userJSON.getUsername() == null || userJSON.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Username cannot be null or empty.");
        }

        // Check if username already exists
        if (getByUsername(userJSON.getUsername()) != null) {
            return ResponseEntity.badRequest().body("Username is already in use! Please choose a different username.");
        }

        // Validate password input
        if (userJSON.getPassword() == null || userJSON.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Password cannot be null or empty.");
        }

        // Encode the password and create a new user object
        String encodedPassword = passwordEncoder.encode(userJSON.getPassword());
        User userObject = new User(userJSON, encodedPassword);

        // Save the user and update the profile table
        userRepository.save(userObject);
        theProfileService.updateProfileTable();
        return ResponseEntity.ok("User successfully registered!");
    }

    public String authenticateUser(String username, String password, boolean isLoginAttempt) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "Invalid credentials";
        }

        if (user.getAdminNameReset() || user.getAdminPassReset()) {
            if (user.getAdminNameReset() && user.getAdminPassReset()) {
                return "Username and Password were reset by an admin, please update both.";
            }
            else if (user.getAdminNameReset()) {
                return "Username was reset by an admin, please update username.";
            }
            else {
                return "Password was reset by an admin, please update password.";
            }
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return "Invalid credentials";
        }

        if (user.getIsLoggedIn()) {
            return "User is already logged in...";
        }
        if (user.getLiftBanTimestamp() != null && user.getBanTimeStamp() != null) {
            if (isLoginAttempt && LocalDateTime.now().isBefore(user.getLiftBanTimestamp())) {
                user.setBanTimeStamp(LocalDateTime.now());
                userRepository.save(user);
                return "You are still banned";
            }
        }
        user.setLiftBanTimestamp(null);
        user.setBanTimeStamp(null);

        user.setIsLoggedIn(isLoginAttempt);
        userRepository.save(user);
        return "User authentication successful.";
    }

    public ResponseEntity<String> banUser(Integer potentialAdminId, String username, Integer banLengthMinutes) {
        User targetUser = getByUsername(username);
        if (targetUser != null) {
            if (verifyIfAdmin(potentialAdminId)) {
                if (!targetUser.getIsLoggedIn()) {
                    // Convert banLengthMinutes (Integer) to milliseconds (Long)
                    long banLength = (long) banLengthMinutes * 60 * 1000;

                    // Set the ban timestamp to the current time and calculate lift time
                    targetUser.setBanLength(banLength);
                    targetUser.setBanTimeStamp(LocalDateTime.now());
                    userRepository.save(targetUser);;
                    targetUser.setLiftBanTimestamp(targetUser.getBanTimeStamp().plus(banLength, ChronoUnit.MILLIS));

                    // Save the updated user details
                    userRepository.save(targetUser);

                    return ResponseEntity.ok("User banned for " + banLengthMinutes + " minutes!");
                }
                return ResponseEntity.ok("Target user is currently logged in, target must be logged out to proceed.");
            }
            return ResponseEntity.ok("User does not have admin privileges.");
        }
        return ResponseEntity.ok("User does not exist...");
    }

    public ResponseEntity<String> toggleAdmin(Integer userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            String msg = "";
            User user = userOptional.get();
            if (user.getIsAdmin()) {
                user.setIsAdmin(false);
                msg = " administrator privileges have been revoked.";
            }
            else {
                user.setIsAdmin(true);
                msg = " has been given administrator privileges.";
            }
            userRepository.save(user);
            return ResponseEntity.ok(user.getUsername() + msg);
        }
        return ResponseEntity.ok("User not found.");
    }

    public ResponseEntity<String> logoutUser(Integer userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!user.getIsLoggedIn()) {
                return ResponseEntity.ok(user.getUsername() + " is not logged in, cannot log out!");
            }
            user.setIsLoggedIn(false);
            userRepository.save(user);
            return ResponseEntity.ok(user.getUsername() + " has signed out.");
        }
        return ResponseEntity.ok("User not found.");
    }

    public List<User> getAllLoggedIn() {
        List<User> loggedInList = new ArrayList<>();
        List<User> users = userRepository.findAll();
        for (User currentuser : users) {
            if (currentuser.getIsLoggedIn()) {
                loggedInList.add(currentuser);
            }
        }
        return loggedInList;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getByUserID(Integer userId) {
        return userRepository.findById(userId);
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public ResponseEntity<String> deleteByID(Integer potentialAdminId, Integer userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            if (verifyIfAdmin(potentialAdminId)) {
                User user = userOptional.get();
                if (!user.getIsLoggedIn()) {
                    TheProfile profileToDelete = findProfileById(userId);

                    profileRepository.delete(profileToDelete);
                    userRepository.deleteById(userId);
                    return ResponseEntity.ok("User deleted");
                }
                return ResponseEntity.ok("Target user is currently logged in, target must be logged out to proceed with deletion.");
            }
            return ResponseEntity.ok("User does not have admin privileges.");
        }
        return ResponseEntity.ok("User does not exist...");
    }

    public ResponseEntity<String> userResetDetails(String username, String password, User userJSON) {
        // Validate input
        if (username == null || username.isEmpty()) {
            return ResponseEntity.badRequest().body("Username cannot be null or empty.");
        }
        if (password == null || password.isEmpty()) {
            return ResponseEntity.badRequest().body("Password cannot be null or empty.");
        }

        User user = getByUsername(username);
        if (user == null) {
            return ResponseEntity.badRequest().body("User does not exist.");
        }

        // Check if the user is logged in
        if (user.getIsLoggedIn()) {
            return ResponseEntity.badRequest().body(
                    "Target user is currently logged in. Cannot reset here. Use the login screen or settings screen."
            );
        }

        // Authenticate the user
        if (!authenticateUser(username, password, false).equals("User authentication successful.")) {
            return ResponseEntity.badRequest().body("Old username/password does not match.");
        }

        boolean updated = false;

        // Update username if provided and valid
        if (userJSON.getUsername() != null && !userJSON.getUsername().isEmpty()) {
            user.setUsername(userJSON.getUsername());
            user.setAdminNameReset(false);
            updated = true;
        }

        // Update password if provided and valid
        if (userJSON.getPassword() != null && !userJSON.getPassword().isEmpty()) {
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setPassword(encoder.encode(userJSON.getPassword()));
            user.setAdminPassReset(false);
            updated = true;
        }

        if (!updated) {
            return ResponseEntity.badRequest().body("You must provide a valid new username or password.");
        }

        userRepository.save(user);
        return ResponseEntity.ok("User credentials updated successfully.");
    }

    public ResponseEntity<String> adminResetUserDetails(Integer potentialAdminId, Integer userId, User userJSON) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            if (verifyIfAdmin(potentialAdminId)) {
                User user = userOptional.get();
                if (!user.getIsLoggedIn()) {
                    if (userJSON.getAdminNameReset()) {
                        user.setAdminNameReset(true);
                    }
                    if (userJSON.getAdminPassReset()) {
                        user.setAdminPassReset(true);
                    }

                    userRepository.save(user);
                    return ResponseEntity.ok("User deleted");
                }
                return ResponseEntity.ok("Target user is currently logged in, target must be logged out to proceed with deletion.");
            }
            return ResponseEntity.ok("User does not have admin privileges.");
        }
        return ResponseEntity.ok("User does not exist...");
    }

    public TheProfile findProfileById(Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // Retrieve the Profile associated with the User
        TheProfile profile = profileRepository.findByUser(user);
        if (profile == null) {
            throw new RuntimeException("Profile not found for specified user ");
        }
        return profile;
    }

    public Player findPlayerById(Integer userId) {
        TheProfile profile = findProfileById(userId);

        Player player = playerRepository.findByProfile(profile);
        if (player == null) {
            throw new RuntimeException("Player not found for specified profile ");
        }
        return player;
    }

    public Boolean verifyIfAdmin (Integer potentialAdminId) {
        Optional<User> userOptional = userRepository.findById(potentialAdminId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user.getIsAdmin();
        }
        return false;
    }
}