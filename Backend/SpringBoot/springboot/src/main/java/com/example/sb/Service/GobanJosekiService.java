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
        return ResponseEntity.ok("new board made");
    }

    public String getBoardState(String username) {
        User user = userRepository.findByUsername(username);
        TheProfile theProfile = theProfileRepository.findByUser(Optional.of(user));

        GobanJoseki board = gobanJosekiRepository.findByGobanProfile(theProfile);
        return board.getBoardState();
    }


    public ResponseEntity<String> resetBoard(String username) {
        User user = userRepository.findByUsername(username);
        TheProfile theProfile = theProfileRepository.findByUser(Optional.of(user));
        GobanJoseki goban = gobanJosekiRepository.findByGobanProfile(theProfile);
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
               goban.getStone(i,j).setStoneType("X");
            }
        }
        return ResponseEntity.ok("board is reset");

    }
    public ResponseEntity<String> undoMove(String username, int x, int y) {
        User user = userRepository.findByUsername(username);
        TheProfile theProfile = theProfileRepository.findByUser(Optional.of(user));
        GobanJoseki goban = gobanJosekiRepository.findByGobanProfile(theProfile);
        Joseki currentjoseki = josekiService.getCurrentMove(goban.getCurrentMove());
        goban.setStone(x, y,new StoneJoseki(goban, x, y));

        goban.setCurrentMove(currentjoseki.getMoveNumber());
        return ResponseEntity.ok("board is reset");
    }


    public ResponseEntity<String> placeAStone(String username, int x, int y) {
        User user = userService.getByUsername(username);
        TheProfile theProfile = theProfileRepository.findByUser(Optional.of(user));
        GobanJoseki goban = gobanJosekiRepository.findByGobanProfile(theProfile);
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
            if(joseki.getXPosition() == x && joseki.getYPosition() == y){
                isPLaceable = true;

            }
        }

        StoneJoseki stone = new StoneJoseki(goban, x, y);
        if(isPLaceable) {
            if (goban.isBlack()) {
                if (goban.getStoneJoseki(x, y).equals("X")) {
                    stone.setStoneType("B");
                    goban.setBlack(false);
                } else if (goban.getStoneJoseki(x, y).equals("X")) {
                    return ResponseEntity.ok("Cannot place a a stone at (" + x + ", " + y + "), as it is occupied.");
                }
            }
            else {
                if (goban.getStoneJoseki(x, y).equals("X")) {
                    stone.setStoneType("W");
                    goban.setBlack(true);
                } else if (!goban.getStoneJoseki(x, y).equals("X")) {
                    return ResponseEntity.ok("Cannot place a a stone at (" + x + ", " + y + "), as it is occupied.");
                }
            }
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
}
