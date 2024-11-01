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
        lobbyService.createFriendlyLobby(userId);
        return ResponseEntity.ok("-> Friendly Lobby should have been created.");
    }

    @PostMapping("/createRanked")
    public ResponseEntity<String> createRankedLobby() {
        lobbyService.createRankedLobby();
        return ResponseEntity.ok("-> Ranked Lobby should have been created.");
    }

    @GetMapping
    public List<Lobby> getAllLobbies() {
        return lobbyService.getAllLobbies();
    }

    @GetMapping("/teams/{lobbyId}")
    public List<Team> getTeamsFromLobby(@PathVariable Integer lobbyId) {
        return lobbyService.getTeams(lobbyId);
    }

    // TODO implement
    @PutMapping("/invite/players/{id}")
    public ResponseEntity<String> invitePlayers(@PathVariable Integer id, @RequestBody Lobby lobbyJSON) {

       return ResponseEntity.ok("-> method incomplete.");
    }
    //TODO when i add the 2nd player it gives a massive error perhaps ask TA. should work for now
    @PutMapping("{userId}/join/{lobbyId}")
    public ResponseEntity<String> joinLobby(@PathVariable Integer userId ,@PathVariable Integer lobbyId) {
        return lobbyService.joinLobby(userId, lobbyId);
    }

    // TODO implement
    @DeleteMapping("{userId}/leave/{lobbyId}")
    public ResponseEntity<String> leaveLobby(@PathVariable Integer lobbyId ,@PathVariable Integer userId) {
        return lobbyService.leaveLobby(userId, lobbyId);
    }

    @DeleteMapping("/kill/{lobbyId}")
    public ResponseEntity<String> killLobby(@PathVariable Integer lobbyId) {
        return lobbyService.deleteLobbyById(lobbyId);
    }

}
