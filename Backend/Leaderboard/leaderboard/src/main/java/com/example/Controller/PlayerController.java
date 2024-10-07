package com.example.Controller;

import com.example.Entity.Player;

import com.example.Service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/users/leaderboard")
@RestController
public class PlayerController {
    @Autowired
    private PlayerService playerService;

    @GetMapping
    public List<Player> getLeaderboard() {
        return playerService.getTop100Players();
    }
    @PostMapping("/add")
    public Player addPlayer(@RequestBody Player player) {
        return playerService.addPlayer(player);
    }

    // TODO(DISCLAIMER) You cannot edit the username using the leaderboard controller.
    @PutMapping("/stats/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable int id, @RequestBody Player player) {
        Player existingUser = playerService.getUserById(id);
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

        Player updatedUser = playerService.updatePlayer(existingUser);

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


