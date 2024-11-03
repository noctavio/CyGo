package com.example.sb.Controller;

import com.example.sb.Model.Lobby;
import com.example.sb.Model.Team;
import com.example.sb.Service.GobanService;
import com.example.sb.Service.LobbyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/lobby")
@RestController
public class LobbyController {
    @Autowired
    private LobbyService lobbyService;

    @Autowired
    private GobanService gobanService;

    @PostMapping("/{hostId}/initialize/game")
    public ResponseEntity<String> initializeGame(@PathVariable Integer hostId) throws JsonProcessingException {
        return gobanService.initializeGame(hostId);
    }

    @PostMapping("/{userId}/create")
    public ResponseEntity<String> createFriendlyLobby(@PathVariable Integer userId) {
        return lobbyService.createFriendlyLobby(userId);
    }

    @GetMapping
    public List<Lobby> getAllLobbies() {
        return lobbyService.getAllLobbies();
    }

    // TODO implement maybe...
    //@PutMapping("/invite/players/{userId}")
    //public ResponseEntity<String> invitePlayers(@PathVariable Integer userId, @RequestBody Lobby lobbyJSON) {
    //
    //   return ResponseEntity.ok("-> method incomplete.");
    //}

    @PutMapping("/{hostUserId}/updateConfig")
    public ResponseEntity<String> updateConfig(@PathVariable Integer hostUserId, @RequestBody Lobby lobbyJSON) {
        return lobbyService.updateConfig(hostUserId, lobbyJSON);
    }

    //TODO when i add the 2nd player it gives a 'duplicate key' error but it saves anyway, ignore the error it works!(could not fix)
    @PutMapping("/{userId}/join/{lobbyId}")
    public ResponseEntity<String> joinLobby(@PathVariable Integer userId ,@PathVariable Integer lobbyId) {
        return lobbyService.joinLobby(userId, lobbyId);
    }

    @DeleteMapping("/{userId}/leave")
    public ResponseEntity<String> leaveLobby(@PathVariable Integer userId) {
        return lobbyService.leaveLobby(userId);
    }

    @DeleteMapping("/{hostUserId}/kick/{userId}")
    public ResponseEntity<String> kickPlayerFromLobby(@PathVariable Integer hostUserId, @PathVariable Integer userId) {
        return lobbyService.kickPlayer(hostUserId, userId);
    }

    @DeleteMapping("/{hostUserId}/killLobby")
    public ResponseEntity<String> killLobby(@PathVariable Integer hostUserId) {
        return lobbyService.deleteLobby(hostUserId);
    }
}
