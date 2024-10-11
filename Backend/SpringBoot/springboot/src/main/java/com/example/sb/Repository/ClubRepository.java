package com.example.sb.Repository;

import com.example.sb.Entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRepository extends JpaRepository<Club, Integer> {
    Club findByCLUB(String CLUB);

    void deleteByCLUB(String CLUB);
}
