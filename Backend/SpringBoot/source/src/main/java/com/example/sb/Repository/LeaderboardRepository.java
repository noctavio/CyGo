package com.example.sb.Repository;

import com.example.sb.Entity.Leaderboard;

import org.springframework.data.jpa.repository.JpaRepository;


public interface LeaderboardRepository extends JpaRepository<Leaderboard, Long> {

}
