package com.example.sb.Service;

import com.example.sb.Model.*;
import com.example.sb.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class GobanJosekiService {

    @Autowired
    private GobanJosekiRepository gobanJosekiRepository;
    @Autowired
    private JosekiRepository lobbyRepository;
    @Autowired
    private TheProfileRepository theProfileRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JosekiService josekiService;
    @Autowired
    private JosekiRepository josekiRepository;

    private static final int[] DIR_X = {-1, 1, 0, 0};
    private static final int[] DIR_Y = {0, 0, -1, 1};

    public ResponseEntity<String> createBoard(String username) {
        User user = userRepository.findByUsername(username);
        TheProfile theProfile = theProfileRepository.findByUser(Optional.of(user));

        GobanJoseki board = new GobanJoseki(theProfile);
        gobanJosekiRepository.save(board);
        return ResponseEntity.ok("new board made");
    }

    public String getBoardState(String username) {
        User user = userRepository.findByUsername(username);
        TheProfile theProfile = theProfileRepository.findByUser(Optional.of(user));
        GobanJoseki goban = gobanJosekiRepository.findByGobanProfile(theProfile);
        if(josekiService.getAllPlayableMoves(goban.getCurrentMove(), goban) == null){
            return goban.getBoardState();
        }
        List<Joseki> josekiList = josekiService.getAllPlayableMoves(goban.getCurrentMove(), goban);
        goban.loadMatrixFromBoardString();
        StoneJoseki board[][] = goban.getBoard();
        int moveNumber = 1;
        for (Joseki joseki : josekiList){
            String move = String.valueOf(moveNumber);
            board[joseki.getXPosition()][joseki.getYPosition()].setStoneType(move);
            moveNumber += 1;
        }
        StringBuilder boardString = new StringBuilder();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                boardString.append(board[i][j].getStoneType()).append(" ");
            }
            boardString.append("\n");  // New line after each row
        }

        return boardString.toString();
    }


    public ResponseEntity<String> resetBoard(String username) {
        User user = userRepository.findByUsername(username);
        TheProfile theProfile = theProfileRepository.findByUser(Optional.of(user));
        GobanJoseki goban = gobanJosekiRepository.findByGobanProfile(theProfile);
        goban.Reset();
        gobanJosekiRepository.save(goban);
        return ResponseEntity.ok(goban.getBoardState());

    }
    public ResponseEntity<String> undoMove(String username) {
        User user = userRepository.findByUsername(username);
        TheProfile theProfile = theProfileRepository.findByUser(Optional.of(user));
        GobanJoseki goban = gobanJosekiRepository.findByGobanProfile(theProfile);
        if(goban.getCurrentMove() == "0000000000"){
            return ResponseEntity.ok("board has no moves");
        }
        Joseki currentjoseki = josekiService.getCurrentMove(goban.getCurrentMove());
        goban.loadMatrixFromBoardString();
        goban.getBoard()[currentjoseki.getXPosition()][currentjoseki.getYPosition()] = new StoneJoseki(goban, currentjoseki.getXPosition(), currentjoseki.getYPosition());

        goban.setCurrentMove(currentjoseki.getLastMove());
        goban.saveBoardString();
        gobanJosekiRepository.save(goban);
        return ResponseEntity.ok("board is updated");
    }


    public ResponseEntity<String> placeAStone(String username, int x, int y) {
        User user = userService.getByUsername(username);
        TheProfile theProfile = theProfileRepository.findByUser(Optional.of(user));
        GobanJoseki goban = gobanJosekiRepository.findByGobanProfile(theProfile);
        goban.loadMatrixFromBoardString();
        List<Joseki> josekiList = josekiService.getAllPlayableMoves(goban.getCurrentMove(), goban);
        boolean isPLaceable = false;

        if (x < 0 || y < 0) {
            //off the board placement check
            return ResponseEntity.ok("Cannot place a a stone at (" + x + ", " + y + "),");
        }
        if (x > 9 || y > 9) {
            //off the board placement check
            return ResponseEntity.ok("Cannot place a a stone at (" + x + ", " + y + "),");
        }
        for(Joseki joseki: josekiList){
            if(joseki.getXPosition() == x && joseki.getYPosition() == y) {
                isPLaceable = true;
                goban.setCurrentMove(joseki.getMoveNumber());


            }
        }



        if(isPLaceable) {
            if (goban.isBlack()) {
                if (goban.getStoneJoseki(x, y).equals("X")) {
                    if(goban.getBoard()[x][y] != null) {
                        goban.getBoard()[x][y].setStoneType("B");
                        goban.setBlack(false);
                    }
                    else{
                        return ResponseEntity.ok("board is null");
                    }
                } else {
                    return ResponseEntity.ok("Cannot place a a stone at (" + x + ", " + y + "), as it is occupied.");
                }
            }
            else {
                if (goban.getStoneJoseki(x, y).equals("X")) {
                    if(goban.getBoard()[x][y] != null) {
                        goban.getBoard()[x][y].setStoneType("W");
                        goban.setBlack(true);
                    }
                    else{
                        return ResponseEntity.ok("board is null");
                    }
                }
                else {
                    return ResponseEntity.ok("Cannot place a a stone at (" + x + ", " + y + "), as it is occupied.");
                }
            }
        }
        else{
            return ResponseEntity.ok("your move was unplaceable");
        }


        goban.saveBoardString();
        gobanJosekiRepository.save(goban);
        return ResponseEntity.ok(theProfile.getUsername() + " placed a stone");

    }
    public ResponseEntity<String> endGame(String username){
        User user = userRepository.findByUsername(username);
        TheProfile theProfile = theProfileRepository.findByUser(Optional.of(user));
        GobanJoseki goban = gobanJosekiRepository.findByGobanProfile(theProfile);
        gobanJosekiRepository.delete(goban);
        return ResponseEntity.ok(theProfile.getUsername() + "'s Joseki game is deleted");
    }
    public String getRealBoardState(String username) {
        User user = userRepository.findByUsername(username);
        TheProfile theProfile = theProfileRepository.findByUser(Optional.of(user));
        GobanJoseki goban = gobanJosekiRepository.findByGobanProfile(theProfile);
        return goban.getBoardState();
    }
}
