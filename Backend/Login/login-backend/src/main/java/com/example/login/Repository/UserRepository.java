package com.example.login.Repository;

import com.example.login.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByusername(String username);
}