package com.example.sb.Repository;

import com.example.sb.Entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(String username);

    @Transactional
    void deleteByUsername(String username);
}