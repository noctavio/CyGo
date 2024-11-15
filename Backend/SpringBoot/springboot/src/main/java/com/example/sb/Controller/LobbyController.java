package com.example.sb.Controller;

import com.example.sb.Model.Lobby;
import com.example.sb.Model.Player;
import com.example.sb.Service.LobbyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/lobby")
@RestController
public class LobbyController {
    @Autowired
    private LobbyService lobbyService;

    @Operation(summary = "Initialize a game", description = "Initializes a game for the specified host user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game initialized successfully"),
            @ApiResponse(responseCode = "404", description = "Host user not found")
    })
    @PostMapping("/{hostId}/initialize/game")
    public ResponseEntity<String> initializeGame(
            @Parameter(description = "ID of the host user initializing the game") @PathVariable Integer hostId)
            throws JsonProcessingException {
        return lobbyService.initializeGame(hostId);
    }

    @Operation(summary = "Create a friendly lobby", description = "Creates a new friendly lobby for the specified user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friendly lobby created successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/{userId}/create")
    public ResponseEntity<String> createFriendlyLobby(
            @Parameter(description = "ID of the user creating the lobby") @PathVariable Integer userId) {
        return lobbyService.createFriendlyLobby(userId);
    }

    @Operation(summary = "Get all lobbies", description = "Retrieves a list of all active lobbies.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lobbies retrieved successfully")
    })
    @GetMapping
    public List<Lobby> getAllLobbies() {
        return lobbyService.getAllLobbies();
    }

    @Operation(summary = "Get all players in a lobby", description = "Retrieves a list of all players in the specified lobby.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Players retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Lobby not found")
    })
    @GetMapping("all/players/{lobbyId}")
    public List<Player> getAllPlayersInLobby(
            @Parameter(description = "ID of the lobby to retrieve players from") @PathVariable Integer lobbyId) {
        return lobbyService.getAllPlayersInLobby(lobbyId);
    }

    @Operation(summary = "Update lobby configuration", description = "Updates the configuration of a lobby for the specified host user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lobby configuration updated successfully"),
            @ApiResponse(responseCode = "404", description = "Host user or lobby not found")
    })
    @PutMapping("/{hostUserId}/updateConfig")
    public ResponseEntity<String> updateConfig(
            @Parameter(description = "ID of the host user updating the lobby configuration") @PathVariable Integer hostUserId,
            @Parameter(description = "New lobby configuration") @RequestBody Lobby lobbyJSON) {
        return lobbyService.updateConfig(hostUserId, lobbyJSON);
    }

    @Operation(summary = "Join a lobby", description = "Allows a user to join a specified lobby.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User joined the lobby successfully"),
            @ApiResponse(responseCode = "409", description = "Duplicate key error occurred but join succeeded"),
            @ApiResponse(responseCode = "404", description = "User or lobby not found")
    })
    @PutMapping("/{userId}/join/{lobbyId}")
    public ResponseEntity<String> joinLobby(
            @Parameter(description = "ID of the user joining the lobby") @PathVariable Integer userId,
            @Parameter(description = "ID of the lobby to join") @PathVariable Integer lobbyId) {
        return lobbyService.joinLobby(userId, lobbyId);
    }

    @Operation(summary = "Leave a lobby", description = "Allows a user to leave the lobby they are currently in.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User left the lobby successfully"),
            @ApiResponse(responseCode = "404", description = "User or lobby not found")
    })
    @DeleteMapping("/{userId}/leave")
    public ResponseEntity<String> leaveLobby(
            @Parameter(description = "ID of the user leaving the lobby") @PathVariable Integer userId) {
        return lobbyService.leaveLobby(userId);
    }

    @Operation(summary = "Kick a player from a lobby", description = "Allows the host user to kick another user from the lobby.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User kicked from the lobby successfully"),
            @ApiResponse(responseCode = "404", description = "Host user, lobby, or player not found")
    })
    @DeleteMapping("/{hostUserId}/kick/{userId}")
    public ResponseEntity<String> kickPlayerFromLobby(
            @Parameter(description = "ID of the host user kicking the player") @PathVariable Integer hostUserId,
            @Parameter(description = "ID of the player being kicked") @PathVariable Integer userId) {
        return lobbyService.kickPlayer(hostUserId, userId);
    }
}
