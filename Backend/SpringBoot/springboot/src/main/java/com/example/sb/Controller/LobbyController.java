package com.example.sb.Controller;

import com.example.sb.Model.Lobby;
import com.example.sb.Model.Team;
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

    @PostMapping("/createFriendly/{userId}")
    public ResponseEntity<String> createFriendlyLobby(@PathVariable Integer userId) {
        return lobbyService.createFriendlyLobby(userId);
    }

    // TODO implement maybe...
    @PostMapping("/createRanked")
    public ResponseEntity<String> createRankedLobby() {
        lobbyService.createRankedLobby();
        return ResponseEntity.ok("-> method incomplete.");
    }

    @GetMapping
    public List<Lobby> getAllLobbies() {
        return lobbyService.getAllLobbies();
    }

    @GetMapping("/teams/{lobbyId}")
    public List<Team> getTeamsFromLobby(@PathVariable Integer lobbyId) {
        return lobbyService.getTeams(lobbyId);
    }

    // TODO implement maybe...
    @PutMapping("/invite/players/{userId}")
    public ResponseEntity<String> invitePlayers(@PathVariable Integer userId, @RequestBody Lobby lobbyJSON) {

       return ResponseEntity.ok("-> method incomplete.");
    }

    //TODO when i add the 2nd player it gives a duplicate perhaps ask TA. should work for now
    @PutMapping("/{userId}/join/{lobbyId}")
    public ResponseEntity<String> joinLobby(@PathVariable Integer userId ,@PathVariable Integer lobbyId) {
        return lobbyService.joinLobby(userId, lobbyId);
    }

    @PutMapping("/{userId}/leave")
    public ResponseEntity<String> leaveLobby(@PathVariable Integer userId) {
        return lobbyService.leaveLobby(userId);
    }

    @PutMapping("/{userId}/updateHostOrTime")
    public ResponseEntity<String> updateGameHostOrGameTime(@PathVariable Integer userId, @RequestBody Lobby lobbyJSON) {
        return lobbyService.updateHostOrGameTime(userId, lobbyJSON);
    }

    @DeleteMapping("/{userId}/killLobby")
    public ResponseEntity<String> killLobby(@PathVariable Integer userId) {
        return lobbyService.deleteLobby(userId);
    }

}
