package com.example.sb.Service;

import com.example.sb.Entity.*;
import com.example.sb.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
    private UserRepository userRepository;
    @Autowired
    private TheProfileRepository profileRepository;

    public List<Lobby> getAllLobbies() {
        return lobbyRepository.findAll();
    }

    public void createFriendlyLobby(Integer id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // Retrieve the Profile associated with the User
        TheProfile profile = profileRepository.findByUser(user);
        if (profile == null) {
            throw new RuntimeException("Profile not found for specified user ");
        }

        // Create a new Player associated with the existing Profile
        Lobby lobby = new Lobby(profile.getUsername());
        Team team1 = new Team(profile.getClubname());
        Team emptyTeam2 = new Team("-/-");
        Player newPlayer = new Player(profile);

        lobby.setTeam1(team1);
        lobby.setTeam2(emptyTeam2);
        team1.addPlayer(newPlayer);
        newPlayer.setLobby(lobby);
        newPlayer.setTeam(team1);

        lobbyRepository.save(lobby);
        teamRepository.save(team1);
        teamRepository.save(emptyTeam2);
        playerRepository.save(newPlayer);
    }

    public void createRankedLobby() {
        Lobby lobby = new Lobby();
    }

    public ResponseEntity<String> updateLobby(Integer playerId, Integer lobbyId) {
        Optional<User> user = userRepository.findById(playerId);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // Retrieve the Profile associated with the User
        TheProfile profile = profileRepository.findByUser(user);
        if (profile == null) {
            throw new RuntimeException("Profile not found for specified user ");
        }

        Optional<Lobby> lobbyOptional = lobbyRepository.findById(lobbyId);
        if (lobbyOptional.isPresent()) {
            // Retrieve the Lobby instance
            Lobby lobbyToJoin = lobbyOptional.get();
            Player newPlayer = new Player(profile);
            Team team1 = lobbyToJoin.getTeam1();
            Team team2 = lobbyToJoin.getTeam2();

            // if team 1 has an open spot
            if (team1.getTeamList().size() < 2) {
                newPlayer.setLobby(lobbyToJoin);
                lobbyToJoin.getTeam1().addPlayer(newPlayer);
            }

            else if (team2.getTeamList().size() < 2) {
                newPlayer.setLobby(lobbyToJoin);
                lobbyToJoin.getTeam2().addPlayer(newPlayer);
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
    // TODO this only works when the lobby has one player/is the host ONLY.
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
    //public ResponseEntity<String> removeUserFromLobby (Integer userId, Integer lobbyId) {
    //
    //}
}
