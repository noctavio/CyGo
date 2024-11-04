package com.example.sb.Service;

import com.example.sb.Model.*;
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
            Team team1 = lobby.getTeam1();
            Team team2 = lobby.getTeam2();
            Player playerStarting1 = team1.getPlayer1();
            Player playerStarting2 = team2.getPlayer1();

            if (team1.getPlayerCount() == 2 && team2.getPlayerCount() == 2) {
                if (team1.getPlayer1().getIsReady() && team1.getPlayer2().getIsReady() && team2.getPlayer1().getIsReady() && team2.getPlayer2().getIsReady()) {
                    Goban goban = new Goban(lobby);

                    if (team1.getIsBlack()) {
                        playerStarting1.setIsTurn(true);
                    }
                    else if(team2.getIsBlack()) {
                        playerStarting2.setIsTurn(true);
                    }

                    playerRepository.save(playerStarting1);
                    playerRepository.save(playerStarting2);
                    gobanRepository.save(goban);
                    return ResponseEntity.ok("Game has been initialized, player order is ->");
                }
                return ResponseEntity.ok("All players must be ready to start the game.");
            }
            return ResponseEntity.ok("4 players are required to start the game, otherwise someone has not selected a team!");
        }
        return ResponseEntity.ok("Player cannot initialize the game as they are not host!");
    }
}
