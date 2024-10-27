package com.example.sb.Service;

import com.example.sb.Model.Lobby;
import com.example.sb.Model.Player;
import com.example.sb.Model.Team;
import com.example.sb.Repository.LobbyRepository;
import com.example.sb.Repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private LobbyRepository lobbyRepository;

    public ResponseEntity<String> updatePlayerTable(Integer id, Player playerJSON) {
        Optional<Player> playerOptional = playerRepository.findById(id);
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            // If the player wants to mute additional players
            Lobby lobby = player.getLobby();
            Team team1 = lobby.getTeam1();
            Team team2 = lobby.getTeam2();

            Team playerTeam;
            Team oppositeTeam;

            if (team1.getTeamList().contains(player)) {
                playerTeam = team1;
                oppositeTeam = team2;
            } else if (team2.getTeamList().contains(player)) {
                oppositeTeam = team1;
                playerTeam = team2;
            } else {
                throw new IllegalStateException("Player is not in either team.");
            }
            // TODO fix this works for now, infact make helper methods
            if (!playerJSON.getMuted().isEmpty()) {
                if (playerJSON.getMuted().get(0).equals("enemies")) {
                    for (int i = 0; i < oppositeTeam.getTeamList().size(); i++) {
                        player.mute(oppositeTeam.getTeamList().get(i).getProfile().getUser().getUsername());
                    }
                }
                if (playerJSON.getMuted().get(0).equals("all")) {
                    for (int i = 0; i < oppositeTeam.getTeamList().size(); i++) {
                        player.mute(oppositeTeam.getTeamList().get(i).getProfile().getUser().getUsername());
                    }
                    for (int i = 0; i < playerTeam.getTeamList().size(); i++) {
                        if (!playerTeam.getTeamList().get(i).equals(player)) {
                            player.mute(playerTeam.getTeamList().get(i).getProfile().getUser().getUsername());
                        }
                    }
                }
                else {
                    player.mute(playerJSON.getMuted().get(0));
                }
            }

            // if the player said unmute in some way shape or form update the unmute list
            if (playerJSON.getMuted().get(0).equals("unmute all")) {
                player.setMuted(new ArrayList<>()); // TODO this is temp unmutes ALL players
            }

            playerRepository.save(player);
            return ResponseEntity.ok("Player has been updated!");
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Player not found!");
        }
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }
}
