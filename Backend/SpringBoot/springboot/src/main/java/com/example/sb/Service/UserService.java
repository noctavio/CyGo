package com.example.sb.Service;

import com.example.sb.Entity.User;
import com.example.sb.Repository.LeaderboardRepository;
import com.example.sb.Repository.TheProfileRepository;
import com.example.sb.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LeaderboardRepository leaderboardRepository;
    @Autowired
    private TheProfileRepository theProfileRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registerUser(User user) {
        String secret = user.getPassword();

        user.setPassword(passwordEncoder.encode(secret));

        return userRepository.save(user);
    }

    public Boolean authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return true;
        }
        return false;
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * This should be the method to delete from ALLLLLLLL Repositories you should import here and deleteByUsername!
     * @param username username
     * @return boolean
     */
    public boolean deleteByUsername(String username) {
        if (userRepository.findByUsername(username) != null) {
            userRepository.deleteByUsername(username);
            leaderboardRepository.deleteByUsername(username);
            theProfileRepository.deleteByUsername(username);
            return true;
        }
        return false;
    }
}
