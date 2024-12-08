package com.example.sb.Service;

import com.example.sb.Model.*;
import com.example.sb.Repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    @Autowired
    private GobanRepository gobanRepository;
    //@Autowired
    //private TimerService timerService;

    public List<Lobby> getAllLobbies() {
        return lobbyRepository.findAll();
    }

    public List<Player> getAllPlayersInLobby(Integer lobbyId) {
        Optional<Lobby> lobbyOptional = lobbyRepository.findById(lobbyId);
        if (lobbyOptional.isPresent()) {
            Lobby lobby = lobbyOptional.get();

            return lobby.getPlayersInLobby();
        }
        return null;
    }

    public ResponseEntity<String> createFriendlyLobby(Integer userId) {
        Team team1;
        TheProfile profile = userService.findProfileById(userId);

        // Initializes lobby with host
        Lobby lobby = new Lobby(profile.getUsername());
        if (profile.getClub() == null) {
            team1 = new Team(lobby, "-/-", true);
        }
        else {
            team1 = new Team(lobby, profile.getClub().getClubName(), true);
        }
        lobby.setTeam1(team1);

        Team emptyTeam2 = new Team(lobby,"-/-", false);
        lobby.setTeam2(emptyTeam2);

        Player hostPlayer = new Player(profile, team1);
        team1.setPlayer1(hostPlayer);
        team1.setPlayerCount();

        lobbyRepository.save(lobby);
        teamRepository.save(team1);
        teamRepository.save(emptyTeam2);
        playerRepository.save(hostPlayer);

        return ResponseEntity.ok("Friendly lobby created with, host: " + profile.getUsername());
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
                newPlayer = new Player(profile, team1);
                team1.setPlayer1(newPlayer);
            }
            else if (team1.getPlayer2() == null) {
                newPlayer = new Player(profile, team1);
                team1.setPlayer2(newPlayer);
            }
            else if (team2.getPlayer1() == null) {
                newPlayer = new Player(profile, team2);
                team2.setPlayer1(newPlayer);
            }
            else if (team2.getPlayer2() == null) {
                newPlayer = new Player(profile, team2);
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
        Player leavingPlayer = playerRepository.findByProfile(profile);
        Team teamBeingLeft = leavingPlayer.getTeam();
        Lobby lobby = teamBeingLeft.getLobby();

        if (lobby != null) {
            if (lobby.getIsGameInitialized()) {
                return ResponseEntity.ok("Cannot leave lobby, must abandon game instead which in turn will remove you from the lobby.");
            }
            if (lobby.getPlayersInLobby().size() > 1 && leavingPlayer.getUsername().equals(lobby.getHostName())) {
                return ResponseEntity.ok("Cannot leave as host while players are in the lobby, transfer host or kick players.");
            }
            else if (leavingPlayer.getUsername().equals(lobby.getHostName()) && lobby.getPlayersInLobby().size() == 1) {
                lobbyRepository.delete(lobby);
                return ResponseEntity.ok("Lobby deleted!");
            }
            //TODO I couldn't get cascade to work here manually set to null to delete.
            if (teamBeingLeft.getPlayer1() != null && teamBeingLeft.getPlayer1().equals(leavingPlayer)) {
                teamBeingLeft.setPlayer1(null);
            }
            else if (teamBeingLeft.getPlayer2() != null && teamBeingLeft.getPlayer2().equals(leavingPlayer)) {
                teamBeingLeft.setPlayer2(null);
            }
            teamRepository.save(teamBeingLeft);
            playerRepository.delete(leavingPlayer);
            return ResponseEntity.ok(profile.getUsername() + " left the lobby!");
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lobby not found!");
        }
    }

    public ResponseEntity<String> updateConfig(Integer hostUserId, Lobby lobbyJSON) {
        TheProfile potentiallyHost = userService.findProfileById(hostUserId);

        Player player = playerRepository.findByProfile(potentiallyHost);
        Lobby lobby = player.getTeam().getLobby();
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

    public ResponseEntity<String> kickPlayer(Integer hostUserId, Integer userId) {

        TheProfile profile = userService.findProfileById(userId);
        TheProfile potentiallyHost = userService.findProfileById(hostUserId);

        Player playerBeingKicked = playerRepository.findByProfile(profile);
        Team targetTeam = playerBeingKicked.getTeam();
        Lobby lobby = targetTeam.getLobby();

        if (lobby != null) {
            if (lobby.getHostName().equals(potentiallyHost.getUsername())) {

                if (targetTeam.getPlayer1().equals(playerBeingKicked)) {
                    targetTeam.setPlayer1(null);
                }
                else if (targetTeam.getPlayer2().equals(playerBeingKicked)) {
                    targetTeam.setPlayer2(null);
                }
                teamRepository.save(targetTeam);
                playerRepository.delete(playerBeingKicked);
                return ResponseEntity.ok(profile.getUsername() + " was kicked from the lobby!");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot kick player as you are not host.");
        }
        else {
            // Handles lobby is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lobby not found!");
        }
    }

    public ResponseEntity<String> initializeGame(Integer hostId) {
        TheProfile profile = userService.findProfileById(hostId);
        Player potentiallyHost = playerRepository.findByProfile(profile);
        Lobby lobby = potentiallyHost.getTeam().getLobby();

        if (lobby.getIsGameInitialized()) {
            System.out.println(lobby.getGoban().getPlayerIdTurnList());
            return ResponseEntity.ok("Game is already initialized");
        }

        if (potentiallyHost.getUsername().equals(lobby.getHostName())) {
            Team team1 = lobby.getTeam1();
            Team team2 = lobby.getTeam2();
            Player team1PlayerStarter = team1.getPlayer1();
            Player team2PlayerStarter = team2.getPlayer1();

            if (team1.getPlayerCount() == 2 && team2.getPlayerCount() == 2) {
                if (team1.getPlayer1().getIsReady() && team1.getPlayer2().getIsReady() && team2.getPlayer1().getIsReady() && team2.getPlayer2().getIsReady()) {
                    Goban goban = new Goban(lobby);
                    List<Integer> playerTurnList = goban.getPlayerIdTurnList();

                    // Sets up the specific ordering for players based on which team is black.
                    if (team1.getIsBlack()) {
                        team2.setTeamScore(6.5);
                        team1.setIsTeamTurn(true);
                        team1PlayerStarter.setIsTurn(true);
                        //team1PlayerStarter.setStartTime(new Date());

                        //timerService.startTimerForTeam(team1);
                        playerTurnList.add(team1PlayerStarter.getProfile().getUser().getUser_id());
                        playerTurnList.add(team2PlayerStarter.getProfile().getUser().getUser_id());
                        playerTurnList.add(team1.getPlayer2().getProfile().getUser().getUser_id());
                        playerTurnList.add(team2.getPlayer2().getProfile().getUser().getUser_id());
                    }
                    else if (team2.getIsBlack()) {
                        team1.setTeamScore(6.5);
                        team2.setIsTeamTurn(true);
                        team2PlayerStarter.setIsTurn(true);
                        //team2PlayerStarter.setStartTime(new Date());
                        //timerService.startTimerForTeam(team2);
                        playerTurnList.add(team2PlayerStarter.getProfile().getUser().getUser_id());
                        playerTurnList.add(team1PlayerStarter.getProfile().getUser().getUser_id());
                        playerTurnList.add(team2.getPlayer2().getProfile().getUser().getUser_id());
                        playerTurnList.add(team1.getPlayer2().getProfile().getUser().getUser_id());
                    }
                    lobby.setGoban(goban);
                    lobby.setIsGameInitialized(true);
                    goban.setPlayerIdTurnList(playerTurnList);

                    playerRepository.save(team1PlayerStarter);
                    playerRepository.save(team2PlayerStarter);
                    gobanRepository.save(goban);
                    lobbyRepository.save(lobby);
                    return ResponseEntity.ok("Game starting soon!");
                }
                return ResponseEntity.ok("All players must be ready to start the game.");
            }
            return ResponseEntity.ok("4 players are required to start the game, otherwise someone has not selected a team!");
        }
        return ResponseEntity.ok("Player cannot initialize the game as they are not host!");
    }
}
