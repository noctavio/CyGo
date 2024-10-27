package com.example.sb.Controller;

import com.example.sb.Entity.Lobby;
import com.example.sb.Entity.Player;
import com.example.sb.Entity.User;
import com.example.sb.Service.LobbyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/lobby")
@RestController
public class LobbyController {
    @Autowired
    private LobbyService lobbyService;

    @GetMapping
    public List<Lobby> getAllLobbies() {
        return lobbyService.getAllLobbies();
    }

    @PostMapping("/createFriendly/{userId}")
    public ResponseEntity<String> createFriendlyLobby(@PathVariable Integer userId) {

        lobbyService.createFriendlyLobby(userId);
        return ResponseEntity.ok("-> Friendly Lobby should have been created.");
    }

    //TODO inviting is kind of hard to implement
    //@PutMapping("/invite/players/{id}")
    //public ResponseEntity<String> invitePlayers(@PathVariable Integer id, @RequestBody Lobby lobbyJSON) {
    //    lobbyService.updateLobby(lobbyJSON.getInvitedPlayers());
    //    return ResponseEntity.ok("-> Lobby should be updated.");
    //}

    @PutMapping("{userId}/join/{lobbyId}")
    public ResponseEntity<String> joinLobby(@PathVariable Integer userId ,@PathVariable Integer lobbyId) {
        return lobbyService.updateLobby(userId, lobbyId);
    }
    //@DeleteMapping("{userId}/leave/{lobbyId}")
    //public ResponseEntity<String> leaveLobby(@PathVariable Integer lobbyId ,@PathVariable Integer userId) {
    //    return lobbyService.removeUserFromLobby(userId, lobbyId);
    //}

    @PostMapping("/createRanked")
    public ResponseEntity<String> createRankedLobby() {
        lobbyService.createRankedLobby();
        return ResponseEntity.ok("-> Ranked Lobby should have been created.");
    }

    @DeleteMapping("/kill/{lobbyId}")
    public ResponseEntity<String> killLobby(@PathVariable Integer lobbyId) {
        return lobbyService.deleteLobbyById(lobbyId);
    }
    // TODO create custom invite for ranked lobby, you can only invite 1 person(and they have to be on your team)
}
