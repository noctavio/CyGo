package com.example.sb.Controller;

import com.example.sb.Service.GobanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/goban")
@RestController
public class GobanController {
    @Autowired
    private GobanService gobanService;

    @PostMapping("/{userId}/place/{x}/{y}")
    public ResponseEntity<String> placeAStone(@PathVariable Integer userId, @PathVariable Integer x, @PathVariable Integer y) {
        return gobanService.placeAStone(userId,x,y);
    }

    @GetMapping("/{lobbyId}/board")
    public String getBoardState(@PathVariable Integer lobbyId) {
        return gobanService.getBoardState(lobbyId);
    }

    @PutMapping("/{userId}/pass")
    public ResponseEntity<String> pass(@PathVariable Integer userId) {
        return gobanService.pass(userId);
    }

    @DeleteMapping("/{lobbyId}/end")
    public ResponseEntity<String> endGame(@PathVariable Integer lobbyId) {
        System.out.println("is there anybody out there");
        return gobanService.endGame(lobbyId);
    }


}
