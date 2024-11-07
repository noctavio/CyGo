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
     * Finds and returns a list of all players in a specific team.
     * @return List of all players in a team
     */
    @GetMapping("/{teamId}")
    public List<Player> getPlayersFromTeam(@PathVariable Integer teamId) {
        return playerService.getAllPlayersFromTeam(teamId);
    }

    /**
     * Returns a list of muted players by the specified id
     * @param userId The id of the player for which we are retrieving the mute list from.
     * @return List of muted players
     */
    @GetMapping("/mutedList/{userId}")
    public List<String> getSomePlayersMutedList(@PathVariable Integer userId) {
        return playerService.getMutedList(userId);
    }

    /**
     * ADDS one or multiple players to a targets mute list, by providing input via mapping
     * @param userId Player who is muting others
     * @param target type being muted, "player", "enemies", or "all"
     * @return String message
     */
    @PutMapping("{userId}/mute/{target}")
    public ResponseEntity<String> addToMuteList(@PathVariable Integer userId, @PathVariable String target) {
        return playerService.mute(userId, target);
    }

    /**
     * REMOVES one or multiple players to a targets mute list, by providing input via mapping
     * @param userId Player who is muting others
     * @param target type being unmuted, "player", "enemies", or "all"
     * @return String message
     */
    @PutMapping("{userId}/unmute/{target}")
    public ResponseEntity<String> removeFromMuteList(@PathVariable Integer userId, @PathVariable String target) {
        return playerService.unmute(userId, target);
    }

    @PutMapping("/{userId}/toggleReady")
    public ResponseEntity<String> toggleReady(@PathVariable Integer userId) {
        return playerService.toggleReady(userId);
    }

    @PutMapping("/{userId}/toggleBlackVote")
    public ResponseEntity<String> toggleBlackVote(@PathVariable Integer userId) {
        return playerService.toggleBlackVote(userId);
    }
}
