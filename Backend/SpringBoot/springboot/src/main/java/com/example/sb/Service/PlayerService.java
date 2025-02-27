package com.example.sb.Service;

import com.example.sb.Model.Lobby;
import com.example.sb.Model.Player;
import com.example.sb.Model.Team;
import com.example.sb.Model.TheProfile;
import com.example.sb.Repository.LobbyRepository;
import com.example.sb.Repository.PlayerRepository;
import com.example.sb.Repository.TeamRepository;
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
    private TeamRepository teamRepository;
    @Autowired
    private UserService userService;

    public List<Player> getAllPlayersFromTeam(Integer teamId) {
        Optional<Team> targetTeam = teamRepository.findById(teamId);
        if (targetTeam.isPresent()) {
            Team team = targetTeam.get();
            List<Player> players = new ArrayList<>();
            if (team.getPlayer1() != null) {
                players.add(team.getPlayer1());
            }
            if (team.getPlayer2() != null) {
                players.add(team.getPlayer2());
            }

            return players;
        }
        return null;
    }

    public List<String> getMutedList(Integer userId) {
        Player player = userService.findPlayerById(userId);
        return player.getMuted();
    }

    public ResponseEntity<String> mute(Integer userId, String target) {
        Player playerUpdatingMuteList = userService.findPlayerById(userId);
        List<String> currentMuted = playerUpdatingMuteList.getMuted();

        Lobby lobby = playerUpdatingMuteList.getTeam().getLobby();
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

        Lobby lobby = playerUpdatingMuteList.getTeam().getLobby();
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
        if (player == null) {
            return ResponseEntity.ok("Player not found");
        }
        if (player.getTeam() != null) {
            player.setIsReady(!player.getIsReady());

            playerRepository.save(player);
            String statusMessage = player.getIsReady() ? "Ready" : "Not Ready";
            return ResponseEntity.ok( player.getUsername() + " is now: " + statusMessage);
        }
        return ResponseEntity.ok("Player must be in a team to set ready status!");
    }

    public ResponseEntity<String> toggleBlackVote(Integer userId) {
        Player player = userService.findPlayerById(userId);
        if (player == null) {
            return ResponseEntity.ok("Player not found");
        }
        if (player.getTeam() != null) {
            player.setCastBlackVote(!player.getCastBlackVote());
            playerRepository.save(player);

            Lobby lobby = player.getTeam().getLobby();
            int blackVoteCount = 0;
            for (Player currentPlayer: lobby.getPlayersInLobby()) {
                if (currentPlayer.getCastBlackVote()) {
                    blackVoteCount++;
                }
            }

            String switchedMessage = "";
            if (blackVoteCount > 2) {
                Team team1 = lobby.getTeam1();
                Team team2 = lobby.getTeam2();
                // Swaps the colors of both teams
                team1.setIsBlack(!team1.getIsBlack());
                team2.setIsBlack(!team2.getIsBlack());
                teamRepository.save(team1);
                teamRepository.save(team2);

                // Resets all votes in case they want to swap again.
                for (Player currentPlayer: lobby.getPlayersInLobby()) {
                    currentPlayer.setCastBlackVote(false);
                    playerRepository.save(currentPlayer);
                }
                switchedMessage = "COLORS HAVE NOW SWAPPED, and votes are reset!";
            }

            String statusMessage = player.getCastBlackVote() ? "in favor of switching teams colors" : "against switching team colors";
            return ResponseEntity.ok("[ALERT] " + player.getUsername() + " voted "  + statusMessage + " " + switchedMessage);
        }
        return ResponseEntity.ok(player.getUsername() + " must be in a team to cast black vote!");
    }
}
