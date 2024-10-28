package com.example.sb.Controller;

import com.example.sb.Model.Player;
import com.example.sb.Service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/lobby/players")
@RestController
public class PlayerController {
    @Autowired
    private PlayerService playerService;

    // TODO true scope needs to be realized, this only adds thing, we need a separate one to update?
    //@PostMapping("/refresh")
    //public ResponseEntity<String> refreshPlayers() {
    //    playerService.updatePlayerTable();
    //    return ResponseEntity.ok("-> Player(s) should have updated.");
    //}
    @GetMapping
    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updatePlayer(@PathVariable Integer id, @RequestBody Player playerJSON) {
        return playerService.updatePlayer(id, playerJSON);
    }

    /**
     * Adds one player or multiple to a targets mute list, by giving JSON data of literal username, "enemies", or "all" commands
     * @param id Player who is muting others
     * @param target JSON containing the list of players you want to mute.
     * @return String message
     */
    @PutMapping("{id}/mute/{target}")
    public ResponseEntity<String> addToMuteList(@PathVariable Integer id, @PathVariable String target) {
        return playerService.mute(id, target);
    }

    @DeleteMapping("{id}/unmute/{target}")
    public ResponseEntity<String> removeFromMuteList(@PathVariable Integer id, @PathVariable String target) {
        return playerService.unmute(id, target);
    }
}
