package com.example.sb.Controller;

import com.example.sb.Entity.Leaderboard;

import com.example.sb.Service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/createFromUsers")
    public String createPlayersFromUsers() {
        playerService.createPlayersFromUsers();
        return "Player username's and ID's have been transferred to the leaderboard table.";
    }

    // TODO(DISCLAIMER) You cannot edit the username using the leaderboard controller.
    @PutMapping("/stats/{id}")
    public ResponseEntity<Leaderboard> updatePlayer(@PathVariable int id, @RequestBody Leaderboard player) {
        Leaderboard existingUser = playerService.getUserById(id);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }
        if (player.getGamesplayed() != null) {
            existingUser.setGamesplayed(player.getWins() + player.getLoss());
        }
        if (player.getRating() != null) {
            existingUser.setRating(player.getRating());
        }
        if(player.getClubname() != null) {
            existingUser.setClubname(player.getClubname());
        }
        if(player.getWins() != null) {
            existingUser.setWins(player.getWins());
        }
        if(player.getLoss() != null) {
            existingUser.setLoss(player.getLoss());
        }

        Leaderboard updatedUser = playerService.updatePlayer(existingUser);

        return ResponseEntity.ok(updatedUser);
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


