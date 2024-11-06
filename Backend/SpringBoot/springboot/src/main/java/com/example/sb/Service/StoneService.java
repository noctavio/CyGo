package com.example.sb.Service;

import com.example.sb.Model.*;
import com.example.sb.Repository.GobanRepository;
import com.example.sb.Repository.PlayerRepository;
import com.example.sb.Repository.StoneRepository;
import com.example.sb.Repository.TeamRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

        if (Objects.equals(currentPlayer.getProfile().getUser().getUser_id(), goban.getPlayerIdTurnList().get(0))) {
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
            teamRepository.save(currentTeam);
            stoneRepository.save(stone);
            gobanRepository.save(goban);
            playerRepository.save(currentPlayer);
        }

        return ResponseEntity.ok("method not complete");
    }
}
