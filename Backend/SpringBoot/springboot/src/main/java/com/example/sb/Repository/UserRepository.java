package com.example.sb.Repository;

import com.example.sb.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByusername(String username);
}