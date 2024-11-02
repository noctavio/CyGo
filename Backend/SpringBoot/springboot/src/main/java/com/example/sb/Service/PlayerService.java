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

    public ResponseEntity<String> updatePlayer(Integer userId, Player playerJSON) {

        return ResponseEntity.ok("method not complete!");
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public List<String> getMutedList(Integer userId) {
        Player player = userService.findPlayerById(userId);
        return player.getMuted();
    }

    public ResponseEntity<String> mute(Integer userId, String target) {
        Player playerUpdatingMuteList = userService.findPlayerById(userId);
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
                playerUpdatingMuteList.getMuted().add(currentPlayer.getUsername());
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
                playerUpdatingMuteList.getMuted().add(currentPlayer.getUsername());
            }
        }
        else {
            if (playerUpdatingMuteList.getUsername().equals(target)) {
                return ResponseEntity.ok("You can't mute yourself...");
            }
            playerUpdatingMuteList.getMuted().add(target);
        }

        playerRepository.save(playerUpdatingMuteList);
        return ResponseEntity.ok(target + " muted");
    }

    public ResponseEntity<String> unmute(Integer userId, String target) {
        Player playerUpdatingMuteList = userService.findPlayerById(userId);
        List<String> currentMuted = playerUpdatingMuteList.getMuted();

        Lobby lobby = playerUpdatingMuteList.getLobby();
        List<Player> playersInLobby = lobby.getPlayersInLobby();

        if (target.equals("all")) {
            for (Player currentPlayer : playersInLobby) {
                if (currentPlayer == playerUpdatingMuteList) {
                    continue;
                }
                if (currentMuted.contains(currentPlayer.getUsername())) {
                    playerUpdatingMuteList.getMuted().remove(currentPlayer.getUsername());
                }
            }
        }
        else if (target.equals("enemies")) {
            for (Player currentPlayer : playersInLobby) {
                if (currentPlayer.getTeam().equals(playerUpdatingMuteList.getTeam())) {
                    continue;
                }
                if (currentMuted.contains(currentPlayer.getUsername())) {
                    playerUpdatingMuteList.getMuted().remove(currentPlayer.getUsername());
                }
            }
        }
        else {
            if (playerUpdatingMuteList.getUsername().equals(target)) {
                return ResponseEntity.ok("You can't unmute yourself...");
            }
            playerUpdatingMuteList.getMuted().remove(target);
        }

        System.out.println(target);
        playerRepository.save(playerUpdatingMuteList);
        return ResponseEntity.ok(target + " unmuted");
    }

    public ResponseEntity<String> toggleReady(Integer userId) {
        Player player = userService.findPlayerById(userId);
        if (player != null) {
            player.setIsReady(!player.getIsReady());

            playerRepository.save(player);
            String statusMessage = player.getIsReady() ? "Ready" : "Not Ready";
            return ResponseEntity.ok("Player is now: " + statusMessage);
        }
        return ResponseEntity.ok("Player not found");
    }

    public ResponseEntity<String> toggleBlackVote(Integer userId) {
        Player player = userService.findPlayerById(userId);
        if (player != null) {
            player.setCastBlackVote(!player.getCastBlackVote());

            playerRepository.save(player);
            String statusMessage = player.getCastBlackVote() ? "in favor of switching teams colors" : "against switching team colors";
            return ResponseEntity.ok("Player voted "  + statusMessage);
        }
        return ResponseEntity.ok("Player not found");
    }
}
