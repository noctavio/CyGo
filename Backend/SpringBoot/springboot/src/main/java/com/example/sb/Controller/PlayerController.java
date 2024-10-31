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

    /**
     * Returns a list of all players saved across all lobbies(not practical for testing only)
     * @return List of ALL players saved in the repository
     */
    @GetMapping
    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updatePlayer(@PathVariable Integer id, @RequestBody Player playerJSON) {
        return playerService.updatePlayer(id, playerJSON);
    }

    /**
     * Returns a list of muted players by the specified id
     * @param id The id of the player for which we are retrieving the mute list from.
     * @return List of muted players
     */
    @GetMapping("/mutedList/{id}")
    public List<String> getSomePlayersMutedList(@PathVariable Integer id) {
        return playerService.getMutedList(id);
    }

    /**
     * ADDS one or multiple players to a targets mute list, by providing input via mapping
     * @param id Player who is muting others
     * @param target type being muted, "player", "enemies", or "all"
     * @return String message
     */
    @PutMapping("{id}/mute/{target}")
    public ResponseEntity<String> addToMuteList(@PathVariable Integer id, @PathVariable String target) {
        return playerService.mute(id, target);
    }
    /**
     * REMOVES one or multiple players to a targets mute list, by providing input via mapping
     * @param id Player who is muting others
     * @param target type being unmuted, "player", "enemies", or "all"
     * @return String message
     */
    @PutMapping("{id}/unmute/{target}")
    public ResponseEntity<String> removeFromMuteList(@PathVariable Integer id, @PathVariable String target) {
        return playerService.unmute(id, target);
    }
}
