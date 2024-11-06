package com.example.sb.Service;

import com.example.sb.Model.*;
import com.example.sb.Repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class StoneService {
    @Autowired
    private StoneRepository stoneRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private GobanRepository gobanRepository;
    @Autowired
    private TeamRepository teamRepository;

    public ResponseEntity<String> placeAStone(Integer userId, Integer x, Integer y) {
        TheProfile profile = userService.findProfileById(userId);
        Player currentPlayer = playerRepository.findByProfile(profile);
        Goban goban = currentPlayer.getTeam().getLobby().getGoban();
        Team currentTeam = currentPlayer.getTeam();

        if (currentPlayer.getIsTurn()) {
            if (x == 22 && y == 22) {
                // TODO clear matrix for testing
                return ResponseEntity.ok("Matrix clear method here temp");
            }
            goban.loadBoardState();
            Stone[][] board = goban.getBoard();

            Stone stone = new Stone(goban, x, y);
            if (currentPlayer.getTeam().getIsBlack()) {
                if (board[x][y].getStoneType().equals("X")) {
                    stone.setStoneType("B");
                }
            }
            else {
                if (board[x][y].getStoneType().equals("X")) {
                    stone.setStoneType("W");
                }
            }

            Integer stones = currentTeam.getStoneCount();
            currentTeam.setStoneCount(stones - 1);
            board[x][y] = stone;
            //stone.setIsCaptured(); TODO IMPLEMENT CAPTURE IF SURROUNDED !!!
            goban.saveBoardState();
            String nextPlayerName = switchToNextPlayer(currentPlayer,goban.getPlayerIdTurnList());
            teamRepository.save(currentTeam);
            stoneRepository.save(stone);
            gobanRepository.save(goban);
            return ResponseEntity.ok(currentPlayer.getUsername() + " placed a stone, it is now " + nextPlayerName + "'s turn.");
        }
        return ResponseEntity.ok("It is not " + currentPlayer.getUsername() + "'s turn, cannot place a stone." );
    }

    public ResponseEntity<String> pass(Integer userId) {
        TheProfile profile = userService.findProfileById(userId);
        Player currentPlayer = playerRepository.findByProfile(profile);

        if (currentPlayer.getIsTurn()) {
            Goban goban = currentPlayer.getTeam().getLobby().getGoban();
            String nextPlayerName = switchToNextPlayer(currentPlayer, goban.getPlayerIdTurnList());

            return ResponseEntity.ok(currentPlayer.getUsername() + " passed, it is now " + nextPlayerName + "'s turn." );
        }

        return ResponseEntity.ok("It is not " + currentPlayer.getUsername() + "'s turn, cannot pass." );
    }

    public String switchToNextPlayer(Player currentPlayer, List<Integer> turnSequence) {
        // Find the current player's ID and index in the turn sequence
        Integer currentTurnId = currentPlayer.getProfile().getUser().getUser_id();
        int currentIndex = turnSequence.indexOf(currentTurnId);

        // Determine the next index in a circular manner
        int nextIndex = (currentIndex + 1) % turnSequence.size();
        Integer nextTurnId = turnSequence.get(nextIndex);

        // Retrieve the next player based on the ID
        TheProfile nextProfile = userService.findProfileById(nextTurnId);
        Player nextPlayer = playerRepository.findByProfile(nextProfile);

        // Update turn states
        nextPlayer.setIsTurn(true);
        currentPlayer.setIsTurn(false);

        // Save updated player states if needed (optional, depending on persistence setup)
        playerRepository.save(currentPlayer);
        playerRepository.save(nextPlayer);
        return nextPlayer.getUsername();
    }
}
