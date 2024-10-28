package com.example.sb.Service;

import com.example.sb.Model.Lobby;
import com.example.sb.Model.Player;
import com.example.sb.Model.Team;
import com.example.sb.Model.TheProfile;
import com.example.sb.Repository.LobbyRepository;
import com.example.sb.Repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private LobbyRepository lobbyRepository;
    @Autowired
    private UserService userService;

    public ResponseEntity<String> updatePlayer(Integer id, Player playerJSON) {
        Optional<Player> playerOptional = playerRepository.findById(id);
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
        }
        return ResponseEntity.ok("method not complete!");
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public ResponseEntity<String> mute(Integer id, String target) {
        Player playerUpdatingMuteList = userService.findPlayerById(id);
        List<String> currentMuted = playerUpdatingMuteList.getMuted();

        Lobby lobby = playerUpdatingMuteList.getLobby();
        List<Player> playersInLobby = lobby.getPlayersInLobby();

        if (target.equals("all")) {
            for (Player currentPlayer : playersInLobby) {
                if (currentPlayer == playerUpdatingMuteList) {
                    continue;
                }
                if (currentMuted.contains(currentPlayer.getUsername())) {
                    continue;
                }
                playerUpdatingMuteList.mute(currentPlayer.getUsername());
            }
        }
        else if (target.equals("enemies")) {
            for (Player currentPlayer : playersInLobby) {
                if (currentPlayer.getTeam().equals(playerUpdatingMuteList.getTeam())) {
                    continue;
                }
                if (currentMuted.contains(currentPlayer.getUsername())) {
                    continue;
                }
                playerUpdatingMuteList.mute(currentPlayer.getUsername());
            }
        }
        else {
            if (playerUpdatingMuteList.getUsername().equals(target)) {
                return ResponseEntity.ok("You can't mute yourself...");
            }
            playerUpdatingMuteList.mute(target);
        }

        playerRepository.save(playerUpdatingMuteList);
        return ResponseEntity.ok("Player(s) have been muted");
    }

    public ResponseEntity<String> unmute(Integer id, String target) {
        Player playerUpdatingMuteList = userService.findPlayerById(id);
        List<String> currentMuted = playerUpdatingMuteList.getMuted();

        Lobby lobby = playerUpdatingMuteList.getLobby();
        List<Player> playersInLobby = lobby.getPlayersInLobby();

        if (target.equals("all")) {
            for (Player currentPlayer : playersInLobby) {
                if (currentPlayer == playerUpdatingMuteList) {
                    continue;
                }
                if (currentMuted.contains(currentPlayer.getUsername())) {
                    playerUpdatingMuteList.unmute(currentPlayer.getUsername());
                }

            }
        }
        else if (target.equals("enemies")) {
            for (Player currentPlayer : playersInLobby) {
                if (currentPlayer.getTeam().equals(playerUpdatingMuteList.getTeam())) {
                    continue;
                }
                if (currentMuted.contains(currentPlayer.getUsername())) {
                    playerUpdatingMuteList.unmute(currentPlayer.getUsername());
                }
            }
        }
        else {
            if (playerUpdatingMuteList.getUsername().equals(target)) {
                return ResponseEntity.ok("You can't unmute yourself...");
            }
            playerUpdatingMuteList.unmute(target);
        }

        System.out.println(target);
        playerRepository.save(playerUpdatingMuteList);
        return ResponseEntity.ok("Player(s) have been unmuted");
    }
}
