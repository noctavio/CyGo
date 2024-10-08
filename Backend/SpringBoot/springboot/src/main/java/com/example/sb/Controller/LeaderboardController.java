package com.example.sb.Controller;

import com.example.sb.Entity.Leaderboard;
import com.example.sb.Constants.RankConstants;

import com.example.sb.Service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RequestMapping("/users/leaderboard")
@RestController
public class LeaderboardController {
    @Autowired
    private LeaderboardService playerService;

    @GetMapping
    public List<Leaderboard> getLeaderboard() {
        return playerService.getTop10Players();
    }

    /**
     * This Controller transfers new users to the leaderboard database to be considered for top 10.
     * @return String
     */
    @PostMapping("/refresh")
    public String createPlayersFromUsers() {
        playerService.createPlayersFromUsers();
        return "Player username's and ID's have been transferred to the leaderboard table.";
    }

    // TODO(DISCLAIMER) You cannot edit the username using the leaderboard controller.
    @PutMapping("/stats/{id}")
    public ResponseEntity<String> updatePlayer(@PathVariable int id, @RequestBody Leaderboard player) {
        Leaderboard existingUser = playerService.getUserById(id);

        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }
        if (player.getWins() < 0 || player.getLoss() < 0) {

            return ResponseEntity.badRequest().body("Wins and losses must be non-negative.");
        }
        if (!Arrays.asList(RankConstants.RANKS).contains(player.getRank())) {
            return ResponseEntity.badRequest().body("Invalid rank input... please review Go ranks -> (30 dan - 1 dan) and (1 kyu - 9 kyu)");
        }

        existingUser.setRank(player.getRank());

        if (player.getClubname() != null) {
            existingUser.setClubname(player.getClubname());
        }
        if (player.getWins() != null) {
            existingUser.setWins(player.getWins());
        }
        if (player.getLoss() != null) {
            existingUser.setLoss(player.getLoss());
        }
        existingUser.setGamesplayed();

        return ResponseEntity.ok("The user has been updated accordingly.");
    }

    @DeleteMapping("remove/{id}")
    public ResponseEntity<String> deletePlayer(@PathVariable int id) {
        boolean isDeleted = playerService.deleteUserById(id);
        if (isDeleted) {
            return ResponseEntity.ok("User deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }
}


