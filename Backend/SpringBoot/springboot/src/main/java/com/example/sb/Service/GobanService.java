package com.example.sb.Service;

import com.example.sb.Model.Goban;
import com.example.sb.Model.Lobby;
import com.example.sb.Model.Player;
import com.example.sb.Model.TheProfile;
import com.example.sb.Repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GobanService {
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private GobanRepository gobanRepository;

    public ResponseEntity<String> initializeGame(Integer hostId) throws JsonProcessingException {
        TheProfile profile = userService.findProfileById(hostId);
        Player potentiallyHost = playerRepository.findByProfile(profile);
        Lobby lobby = potentiallyHost.getTeam().getLobby();

        if (potentiallyHost.getUsername().equals(lobby.getHostName())) {
            Goban goban = new Goban(lobby);
            System.out.println(goban.getBoardState());
            gobanRepository.save(goban);
            return ResponseEntity.ok("Game has been initialized, player order is ->");
        }
        return ResponseEntity.ok("Player cannot initialize the game as they are not host!");
    }
}
