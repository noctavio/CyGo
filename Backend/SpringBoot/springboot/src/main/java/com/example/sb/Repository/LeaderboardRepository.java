package com.example.sb.Repository;

import com.example.sb.Entity.Leaderboard;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LeaderboardRepository extends JpaRepository<Leaderboard, Integer> {

    Leaderboard findByUsername(String username);

    @Transactional
    void deleteByUsername(String username);
}
