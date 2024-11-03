package com.example.sb.Repository;

import com.example.sb.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(String username);

    @Override
    Optional<User> findById(Integer id);

}