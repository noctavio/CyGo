package com.example.sb.Controller;

import com.example.sb.Service.StoneService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/goban/stones")
@RestController
public class StoneController {
    @Autowired
    private StoneService stoneService;

    @PostMapping("/{userId}/place/{x}/{y}")
    public ResponseEntity<String> placeAStone(@PathVariable Integer userId, @PathVariable Integer x, @PathVariable Integer y) {
         return stoneService.placeAStone(userId,x,y);
    }
}
