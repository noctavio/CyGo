package com.example.Controller;

import com.example.Entity.Player;

import com.example.Service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PlayerController {
    @Autowired
    private PlayerService playerService;

    @GetMapping("/leaderboard")
    public List<Player> getLeaderboard() {
        return playerService.getTop100Players();
    }

    // TODO must add delete for this controller, check demo-2 requirements
    // TODO figure out how to merge
    // TODO rewrite screen sketches for tables

    // TODO display top 100 and some stats :)
}
