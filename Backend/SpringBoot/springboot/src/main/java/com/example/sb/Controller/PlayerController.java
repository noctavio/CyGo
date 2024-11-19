package com.example.sb.Controller;

import com.example.sb.Model.Player;
import com.example.sb.Service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/lobby/players")
@RestController
public class PlayerController {
    @Autowired
    private PlayerService playerService;

    @Operation(summary = "Get players from a team", description = "Retrieves a list of all players in a specific team.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Players retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Team not found")
    })
    @GetMapping("/{teamId}")
    public List<Player> getPlayersFromTeam(
            @Parameter(description = "ID of the team to retrieve players from") @PathVariable Integer teamId) {
        return playerService.getAllPlayersFromTeam(teamId);
    }

    @Operation(summary = "Get muted players list", description = "Retrieves the mute list for a specific player.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mute list retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Player not found")
    })
    @GetMapping("/mutedList/{userId}")
    public List<String> getSomePlayersMutedList(
            @Parameter(description = "ID of the player whose mute list is being retrieved") @PathVariable Integer userId) {
        return playerService.getMutedList(userId);
    }

    @Operation(summary = "Add players to mute list", description = "Adds one or multiple players to a user's mute list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Players added to mute list successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid mute target"),
            @ApiResponse(responseCode = "404", description = "Player not found")
    })
    @PutMapping("{userId}/mute/{target}")
    public ResponseEntity<String> addToMuteList(
            @Parameter(description = "ID of the player muting others") @PathVariable Integer userId,
            @Parameter(description = "Type of target being muted: 'player', 'enemies', or 'all'") @PathVariable String target) {
        return playerService.mute(userId, target);
    }

    @Operation(summary = "Remove players from mute list", description = "Removes one or multiple players from a user's mute list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Players removed from mute list successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid mute target"),
            @ApiResponse(responseCode = "404", description = "Player not found")
    })
    @PutMapping("{userId}/unmute/{target}")
    public ResponseEntity<String> removeFromMuteList(
            @Parameter(description = "ID of the player unmuting others") @PathVariable Integer userId,
            @Parameter(description = "Type of target being unmuted: 'player', 'enemies', or 'all'") @PathVariable String target) {
        return playerService.unmute(userId, target);
    }

    @Operation(summary = "Toggle ready status", description = "Toggles the ready status for a specific player.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ready status toggled successfully"),
            @ApiResponse(responseCode = "404", description = "Player not found")
    })
    @PutMapping("/{userId}/toggleReady")
    public ResponseEntity<String> toggleReady(
            @Parameter(description = "ID of the player toggling their ready status") @PathVariable Integer userId) {
        return playerService.toggleReady(userId);
    }

    @Operation(summary = "Toggle black vote status", description = "Toggles the black vote status for a specific player.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Black vote status toggled successfully"),
            @ApiResponse(responseCode = "404", description = "Player not found")
    })
    @PutMapping("/{userId}/toggleBlackVote")
    public ResponseEntity<String> toggleBlackVote(
            @Parameter(description = "ID of the player toggling their black vote status") @PathVariable Integer userId) {
        return playerService.toggleBlackVote(userId);
    }
}
