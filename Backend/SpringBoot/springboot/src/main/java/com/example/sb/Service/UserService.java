package com.example.sb.Service;

import com.example.sb.Entity.User;
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

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registerUser(User user) {
        String secret = user.getPassword();

        user.setPassword(passwordEncoder.encode(secret));

        return userRepository.save(user);
    }
    public Boolean authenticateUser(String username, String password) {
        User user = userRepository.findByusername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return true;
        }
        return false;
    }


    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }
    public boolean deleteUserById(int id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
