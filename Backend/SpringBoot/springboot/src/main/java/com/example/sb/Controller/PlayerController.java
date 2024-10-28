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

    @GetMapping
    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    @GetMapping("/mutedList/{id}")
    public List<String> getSomePlayersMutedList(@PathVariable Integer id) {
        return playerService.getMutedList(id);
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
