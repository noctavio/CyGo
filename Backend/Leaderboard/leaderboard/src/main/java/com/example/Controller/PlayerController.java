package com.example.Controller;

import com.example.Entity.Player;

import com.example.Service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @PutMapping("/stats/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable int id, @RequestBody Player user) {
        // TODO make sure changing one stat doesn't make the rest go null
        Player existingUser = playerService.getUserById(id);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }
        existingUser.setUsername(user.getUsername());

        Player updatedUser = playerService.updatePlayer(existingUser);

        return ResponseEntity.ok(updatedUser);
    }


    @DeleteMapping("stats/{id}")
    public ResponseEntity<Player> deleteSomething(@PathVariable int id, @RequestBody Player player) {
        //delete request, how to use...
        return null;
    }

    // TODO must add delete for this controller, check demo-2 requirements
    // TODO figure out how to merge
    // TODO rewrite screen sketches for tables

    // TODO display top 100 and some stats :)
}
