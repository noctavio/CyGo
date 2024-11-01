package com.example.sb.Service;

import com.example.sb.Model.Player;
import com.example.sb.Model.TheProfile;
import com.example.sb.Model.User;
import com.example.sb.Repository.PlayerRepository;
import com.example.sb.Repository.TheProfileRepository;
import com.example.sb.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public User registerUser(User user) {
        String secret = user.getPassword();

        user.setPassword(passwordEncoder.encode(secret));

        User savedUser = userRepository.save(user);
        theProfileService.updateProfileTable();
        return savedUser;
    }

    public Boolean authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return true;
        }
        return false;
    }

    public void updateUser(Optional<User> user) {
        // Check if the Optional contains a value
        if (user.isPresent()) {
            userRepository.save(user.get());  // Save the User if present
        } else {
            throw new IllegalArgumentException("User must be present to update."); // Handle the absence of User
        }
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

    /**
     * This should be the method to delete from ALLLLLLLL Repositories, rows should cascade delete so set the query please!
     * @param userId user id
     * @return boolean
     */
    public boolean deleteByID(Integer userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
            return true;
        }
       return false;
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
}
