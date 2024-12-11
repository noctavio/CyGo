package com.example.sb.Repository;


import com.example.sb.Model.Friends;
import com.example.sb.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendsRepository extends JpaRepository<Friends, Integer> {
    Optional<Friends> findByUser(User user);
    void deleteById(Integer id);
}
