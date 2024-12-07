package com.example.sb.Controller;

import com.example.sb.Service.GobanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/goban")
@RestController
public class GobanController {
    @Autowired
    private GobanService gobanService;

    @Operation(summary = "Place a stone on the board", description = "Allows a user to place a stone on the board at a specified position.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stone placed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid position or user action"),
            @ApiResponse(responseCode = "404", description = "User or board not found")
    })
    @PostMapping("/{userId}/place/{x}/{y}")
    public ResponseEntity<String> placeAStone(
            @Parameter(description = "ID of the user placing the stone") @PathVariable Integer userId,
            @Parameter(description = "X-coordinate of the position") @PathVariable Integer x,
            @Parameter(description = "Y-coordinate of the position") @PathVariable Integer y) {
        return gobanService.placeAStone(userId, x, y);
    }

    @Operation(summary = "Get the current board state", description = "Retrieves the current state of the game board for the specified lobby.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board state retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Lobby not found")
    })
    @GetMapping("/{lobbyId}/board")
    public String getBoardState(
            @Parameter(description = "ID of the lobby to retrieve the board state for") @PathVariable Integer lobbyId) {
        return gobanService.getBoardState(lobbyId);
    }

    @Operation(summary = "Pass a turn", description = "Allows a user to pass their turn in the game.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Turn passed successfully"),
            @ApiResponse(responseCode = "404", description = "User or game not found")
    })
    @PutMapping("/{userId}/pass")
    public ResponseEntity<String> pass(
            @Parameter(description = "ID of the user passing their turn") @PathVariable Integer userId) {
        return gobanService.pass(userId);
    }

    @Operation(summary = "Allows player to abandon the game", description = "Ends the game for the lobby and removes player from the lobby/game.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Player abandoned successfully and game has closed"),
            @ApiResponse(responseCode = "404", description = "Player not found")
    })
    @DeleteMapping("/{userId}/abandon")
    public ResponseEntity<String> abandonGame(
            @Parameter(description = "ID of the player abandoning") @PathVariable Integer userId) {
        return gobanService.abandonGame(userId);
    }
}

