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
    private LeaderboardService leaderboardService;

    @GetMapping
    public List<Leaderboard> getLeaderboard() {
        return leaderboardService.getTop10Players();
    }

    /**
     * This Controller transfers new users to the leaderboard database to be considered for top 10.
     * @return String
     */
    @PostMapping("/refresh")
    public String createPlayersFromUsers() {
        leaderboardService.createPlayersFromUsers();
        return "Player username's and ID's have been transferred to the leaderboard table.";
    }

    // TODO(DISCLAIMER) You cannot edit the username using the leaderboard controller.
    @PutMapping("/stats/{id}")
    public ResponseEntity<String> updatePlayer(@PathVariable int id, @RequestBody Leaderboard player) {
        Leaderboard existingUser = leaderboardService.getUserById(id);

        if (existingUser == null) {
            return ResponseEntity.badRequest().body("User was not found");
        }
        if ((player.getWins() != null && player.getWins() < 0) || (player.getLoss() != null && player.getLoss() < 0)) {

            return ResponseEntity.badRequest().body("Wins and losses must be non-negative.");
        }
        if (player.getRank() != null) {
            if (Arrays.asList(RankConstants.RANKS).contains(player.getRank())) {
                existingUser.setRank(player.getRank());
            }
            else {
                return ResponseEntity.badRequest().body("Invalid rank input... please review Go ranks -> (30 kyu - 1 kyu) and (1 dan - 9 dan)");
            }
        }


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
        leaderboardService.updatePlayer(existingUser);

        return ResponseEntity.ok("The user has been updated accordingly.");
    }

    @DeleteMapping("remove/{id}")
    public ResponseEntity<String> deletePlayer(@PathVariable int id) {
        boolean isDeleted = leaderboardService.deleteUserById(id);
        if (isDeleted) {
            return ResponseEntity.ok("User deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }
}


