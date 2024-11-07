package com.example.sb.Service;

import com.example.sb.Model.*;
import com.example.sb.Repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GobanService {
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private GobanRepository gobanRepository;
    @Autowired
    private LobbyRepository lobbyRepository;
    @Autowired
    private TeamRepository teamRepository;

    //public List<Integer> getPlayerTurnlist(Integer lobbyId) {
    //    Optional<Lobby> lobbyOptional = lobbyRepository.findById(lobbyId);

    //    if (lobbyOptional.isPresent()) {
    //        Lobby lobby = lobbyOptional.get();

    //        Goban board = lobby.getBoard();
    //        return board.getPlayerIdTurnList();
    //    }
    //    return null;
    //}

    // TODO get board

    public String getBoardState(Integer lobbyId) {
        Optional<Lobby> lobbyOptional = lobbyRepository.findById(lobbyId);
        if (lobbyOptional.isPresent()) {
            Lobby lobby = lobbyOptional.get();
            Goban board = lobby.getGoban();

            return board.getBoardState();
        }
        return null;
    }
    public ResponseEntity<String> endGame(Integer lobbyId) {
        Optional<Lobby> lobbyOptional = lobbyRepository.findById(lobbyId);
        if (lobbyOptional.isPresent()) {
            Lobby lobby = lobbyOptional.get();
            Team team1 = lobby.getTeam1();
            // player number is not based on order, it's just a variable name
            Player player1 = team1.getPlayer1();
            Player player2 = team1.getPlayer2();
            Team team2 = lobby.getTeam2();
            Player player3 = team2.getPlayer1();
            Player player4 = team2.getPlayer2();
            Goban board = lobby.getGoban();

            String teamWinner;
            if (team1.getTeamScore() > team2.getTeamScore()) {
                teamWinner = team1.getTeamName() + "("+ player1.getUsername() + "-|-" + player2.getUsername() + ") win's the game.";
            }
            else {
                teamWinner = team2.getTeamName() + "("+ player1.getUsername() + "-|-" + player2.getUsername() + ") win's the game.";
            }
            String finaleScores = "Final scores are, " + team1.getTeamName() + ": " + team1.getTeamScore() +
                    " and " + team2.getTeamName() + ": " + team2.getTeamScore();

            lobby.setGoban(null);
            lobby.setIsGameInitialized(false);

            team1.setTeamScore(0.0);
            team1.setStoneCount(41);

            team2.setTeamScore(0.0);
            team2.setStoneCount(41);

            player1.setStartTime(null);
            player1.setEndTime(null);
            player1.setIsTurn(false);
            player1.setIsReady(false);

            player2.setStartTime(null);
            player2.setEndTime(null);
            player2.setIsTurn(false);
            player2.setIsReady(false);

            player3.setStartTime(null);
            player3.setEndTime(null);
            player3.setIsTurn(false);
            player3.setIsReady(false);

            player4.setStartTime(null);
            player4.setEndTime(null);
            player4.setIsTurn(false);
            player4.setIsReady(false);

            gobanRepository.delete(board);
            lobbyRepository.save(lobby);
            playerRepository.save(player1);
            playerRepository.save(player2);
            playerRepository.save(player3);
            playerRepository.save(player4);
            teamRepository.save(team1);
            teamRepository.save(team2);

            return ResponseEntity.ok("Game over,\n " + teamWinner + "\n" + finaleScores);
        }
        return ResponseEntity.ok("Wrong lobby id");
    }
}
