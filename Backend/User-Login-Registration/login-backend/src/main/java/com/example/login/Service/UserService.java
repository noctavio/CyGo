package com.example.login.Service;

import com.example.login.Entity.User;
import com.example.login.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User updateUser(User user) {
        return userRepository.save(user);
    }
}
