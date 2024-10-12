package com.example.sb.Controller;

import com.example.sb.Entity.Leaderboard;

import com.example.sb.Service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/leaderboard")
@RestController
public class LeaderboardController {
    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping
    public List<Leaderboard> getLeaderboard() {
        return leaderboardService.getTop10Players();
    }

    /**
     * This Controller transfers new users to the leaderboard database to be considered for top 10.
     * IT MUST BE CALLED BEFORE getLeaderboard!
     * @return String
     */
    @PostMapping("/refresh")
    public ResponseEntity<String> createPlayersFromProfiles() {
        leaderboardService.updateLeaderboardTable();
        return ResponseEntity.ok("Player data has been transferred to the leaderboard table.");
    }
}


