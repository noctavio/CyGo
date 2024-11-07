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

    @GetMapping("/{lobbyId}/board")
    public String getBoardState(@PathVariable Integer lobbyId) {
        return gobanService.getBoardState(lobbyId);
    }

    @DeleteMapping("/{lobbyId}/end")
    public ResponseEntity<String> endGame(@PathVariable Integer lobbyId) {
        System.out.println("is there anybody out there");
        return gobanService.endGame(lobbyId); // TODO this is temporary and for testing not to be used manually, game end is automatic!
    }
}
