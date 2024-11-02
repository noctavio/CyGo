package com.example.sb.Service;

import com.example.sb.Model.*;
import com.example.sb.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.util.*;

@Service
public class LobbyService {
    @Autowired
    private LobbyRepository lobbyRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UserService userService;

    public List<Lobby> getAllLobbies() {
        return lobbyRepository.findAll();
    }

    public List<Team> getTeams(Integer lobbyId) {
        Optional<Lobby> lobbyOptional = lobbyRepository.findById(lobbyId);
        if (lobbyOptional.isPresent()) {
            Lobby specificLobby = lobbyOptional.get();
            Team team1 = specificLobby.getTeam1();
            Team team2 = specificLobby.getTeam2();
            return Arrays.asList(team1, team2);
        }
        return Collections.emptyList();
    }

    public ResponseEntity<String> createFriendlyLobby(Integer userId) {
        TheProfile profile = userService.findProfileById(userId);

        // Initializes lobby with host
        Lobby lobby = new Lobby(profile.getUsername());
        Team team1 = new Team(profile.getClubname(), true);
        lobby.setTeam1(team1);

        Team emptyTeam2 = new Team("-/-", false);
        lobby.setTeam2(emptyTeam2);

        Player hostPlayer = new Player(profile, team1, lobby);
        team1.setPlayer1(hostPlayer);
        team1.setPlayerCount();

        lobbyRepository.save(lobby);
        teamRepository.save(team1);
        teamRepository.save(emptyTeam2);
        playerRepository.save(hostPlayer);

        return ResponseEntity.ok("Friendly lobby created with, host: " + profile.getUsername());
    }

    @Transactional
    public ResponseEntity<String> joinLobby(Integer userId, Integer lobbyId) {
        TheProfile profile = userService.findProfileById(userId);

        Optional<Lobby> lobbyOptional = lobbyRepository.findById(lobbyId);
        if (lobbyOptional.isPresent()) {
            // Retrieve the Lobby instance
            Lobby lobbyToJoin = lobbyOptional.get();
            Team team1 = lobbyToJoin.getTeam1();
            Team team2 = lobbyToJoin.getTeam2();
            Player newPlayer;

            // if team 1 has an open spot
            if (team1.getPlayer1() == null) {
                newPlayer = new Player(profile, team1, lobbyToJoin);
                team1.setPlayer1(newPlayer);
            }
            else if (team1.getPlayer2() == null) {
                newPlayer = new Player(profile, team1, lobbyToJoin);
                team1.setPlayer2(newPlayer);
            }
            else if (team2.getPlayer1() == null) {
                newPlayer = new Player(profile, team2, lobbyToJoin);
                team2.setPlayer1(newPlayer);
            }
            else if (team2.getPlayer2() == null) {
                newPlayer = new Player(profile, team2, lobbyToJoin);
                team2.setPlayer2(newPlayer);
            }
            else {
                return ResponseEntity.ok("Lobby is full.");
            }

            team1.setPlayerCount();
            team2.setPlayerCount();

            teamRepository.save(team1);
            teamRepository.save(team2);
            playerRepository.save(newPlayer);

            return ResponseEntity.ok(profile.getUsername() + " joined the lobby!");
        }
        else {
            // Handles lobby is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lobby not found!");
        }
    }

    public ResponseEntity<String> leaveLobby(Integer userId) {
        TheProfile profile = userService.findProfileById(userId);
        Player deserter = playerRepository.findByProfile(profile);
        Lobby lobby = deserter.getLobby();

        if (lobby != null) {
            playerRepository.delete(deserter);
            return ResponseEntity.ok(profile.getUsername() + " left the lobby!");
        }
        else {
            // Handles lobby is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lobby not found!");
        }
    }

    public ResponseEntity<String> deleteLobby(Integer userId) {
        TheProfile profile = userService.findProfileById(userId);
        Player potentiallyHost = playerRepository.findByProfile(profile);
        Lobby lobbyToDelete = potentiallyHost.getLobby();

        if (potentiallyHost.getUsername().equals(lobbyToDelete.getHostName())) {

            lobbyRepository.delete(lobbyToDelete);
            return ResponseEntity.ok("Lobby deleted!");
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(potentiallyHost.getUsername() + " is not host, cannot delete lobby.");
        }
    }

    public ResponseEntity<String> updateConfig(Integer userId, Lobby lobbyJSON) {
        TheProfile potentiallyHost = userService.findProfileById(userId);

        Player player = playerRepository.findByProfile(potentiallyHost);
        Lobby lobby = player.getLobby();
        if (lobby.getTeam1() != null) {

            if (potentiallyHost.getUsername().equals(lobby.getHostName())) {
                if (lobbyJSON.getGameTime() != null) {
                    lobby.setGameTime(lobbyJSON.getGameTime());
                }
                if (lobbyJSON.getHostName() != null) {
                    lobby.setHostName(lobbyJSON.getHostName());
                }
                if (lobbyJSON.getIsFriendly() != null) {
                    lobby.setIsFriendly(lobbyJSON.getIsFriendly());
                }
            }
            else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(potentiallyHost.getUsername() + " is not host, cannot update lobby.");
            }
            lobbyRepository.save(lobby);
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Specified lobby not found");
        }
        return ResponseEntity.ok("Lobby updated!");
    }

}
