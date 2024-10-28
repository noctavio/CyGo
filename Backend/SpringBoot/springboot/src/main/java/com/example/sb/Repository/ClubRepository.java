package com.example.sb.Repository;

import com.example.sb.Entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Integer> {
    Optional<Club> findById(Integer id);

    void deleteById(Integer id);
}
