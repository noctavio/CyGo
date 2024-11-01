package com.example.sb.Service;

import com.example.sb.Model.*;
import com.example.sb.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    @Autowired
    private TeamService teamService;

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

    public void createFriendlyLobby(Integer userId) {
        TheProfile profile = userService.findProfileById(userId);

        // Initializes lobby with host
        Lobby lobby = new Lobby(profile.getUsername());
        Team team1 = new Team(profile.getClubname(), true);
        lobby.setTeam1(team1);

        Team emptyTeam2 = new Team("-/-", false);
        lobby.setTeam2(emptyTeam2);

        Player newPlayer = new Player(profile, team1, lobby);
        teamService.joinTeam(userId, team1.getTeam_id());

        lobbyRepository.save(lobby);
        teamRepository.save(team1);
        teamRepository.save(emptyTeam2);
        playerRepository.save(newPlayer);
    }

    public void createRankedLobby() {
        Lobby lobby = new Lobby();
    }

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
                teamService.joinTeam(userId, team1.getTeam_id());
            }
            else if (team1.getPlayer2() == null) {
                newPlayer = new Player(profile, team1, lobbyToJoin);
                teamService.joinTeam(userId, team1.getTeam_id());
            }
            else if (team2.getPlayer1() == null) {
                newPlayer = new Player(profile, team2, lobbyToJoin);
                teamService.joinTeam(userId, team2.getTeam_id());
            }
            else if (team2.getPlayer2() == null) {
                newPlayer = new Player(profile, team2, lobbyToJoin);
                teamService.joinTeam(userId, team2.getTeam_id());
            }
            else {
                return ResponseEntity.ok("Lobby is full.");
            }

            teamRepository.save(team1);
            teamRepository.save(team2);
            playerRepository.save(newPlayer);
            lobbyRepository.save(lobbyToJoin);

            return ResponseEntity.ok("Player joined the lobby!");
        }
        else {
            // Handles lobby is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lobby not found!");
        }
    }
    public ResponseEntity<String> leaveLobby(Integer userId, Integer lobbyId) {
        TheProfile profile = userService.findProfileById(userId);

        Optional<Lobby> lobbyOptional = lobbyRepository.findById(lobbyId);
        if (lobbyOptional.isPresent()) {
            // Retrieve the Lobby instance
            Player deserter = playerRepository.findByProfile(profile);
            if (deserter != null) {
                playerRepository.delete(deserter);
                return ResponseEntity.ok(profile.getUsername() + " left the lobby!");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Deserter not found!");
        }
        else {
            // Handles lobby is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lobby not found!");
        }
    }

    public ResponseEntity<String> deleteLobbyById(Integer lobbyId) {
        Optional<Lobby> lobbyOptional = lobbyRepository.findById(lobbyId);
        if (lobbyOptional.isPresent()) {
            Lobby lobbyToDelete = lobbyOptional.get();
            lobbyRepository.delete(lobbyToDelete);
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lobby not found or hostId is wrong!");
        }
        return ResponseEntity.ok("Lobby deleted!");
    }

}
