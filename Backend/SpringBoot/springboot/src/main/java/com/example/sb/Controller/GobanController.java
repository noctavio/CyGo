package com.example.sb.Controller;

import com.example.sb.Model.Player;
import com.example.sb.Service.GobanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/goban")
@RestController
public class GobanController {
    @Autowired
    private GobanService gobanService;

    //@GetMapping("/{lobbyId}/playerTurnList")
    //public List<Integer> playerTurnList(@PathVariable Integer lobbyId) {
    //    return gobanService.getPlayerTurnlist(lobbyId);
    //}

    @GetMapping("/{lobbyId}/board")
    public String getBoardState(@PathVariable Integer lobbyId) {
        return gobanService.getBoardState(lobbyId);
    }
}
