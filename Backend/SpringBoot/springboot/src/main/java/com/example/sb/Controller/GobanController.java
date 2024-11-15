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

    @Operation(summary = "End the game", description = "Ends the game for the specified lobby.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game ended successfully"),
            @ApiResponse(responseCode = "404", description = "Lobby not found")
    })
    @DeleteMapping("/{lobbyId}/end")
    public ResponseEntity<String> endGame(
            @Parameter(description = "ID of the lobby where the game will end") @PathVariable Integer lobbyId) {
        System.out.println("is there anybody out there");
        return gobanService.endGame(lobbyId);
    }
}

