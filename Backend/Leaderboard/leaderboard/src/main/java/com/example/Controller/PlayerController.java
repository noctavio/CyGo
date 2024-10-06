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

    // TODO add a player to the leader board(just generate id and set name)
    // TODO delete a player from the leaderboard(post production this would delete from one database and they would exist on the other)
    // TODO display top 100 and some stats :)

    // TODO check demo-2 requirements
    @PutMapping("/stats/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable int id, @RequestBody Player player) {
        // TODO make sure changing one stat doesn't make the rest go null
        Player existingUser = playerService.getUserById(id);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }
        if (player.getUsername() != null) { // TODO this is temporary you should not be able to change this later...
            existingUser.setUsername(player.getUsername());
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
    // TODO figure out how to merge
    // TODO rewrite screen sketches for tables

