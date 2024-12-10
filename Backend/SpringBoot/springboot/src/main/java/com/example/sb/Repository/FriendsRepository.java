package com.example.sb.Repository;

import com.example.sb.Model.Club;
import com.example.sb.Model.Friends;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendsRepository extends JpaRepository<Friends, Integer> {
    Optional<Friends> findById(Integer id);
    void deleteById(Integer id);
}
