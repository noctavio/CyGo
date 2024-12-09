package com.example.sb.Controller;

import com.example.sb.Model.GobanJoseki;
import com.example.sb.Service.GobanJosekiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/goban")
@RestController
public class GobanJosekiController {
    @Autowired
    private GobanJosekiService gobanService;

    @Operation(summary = "Place a stone on the board", description = "Allows a user to place a stone on the board at a specified position.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid position or user action"),
            @ApiResponse(responseCode = "404", description = "User or board not found")
    })
    @PostMapping("/{username}")
    public ResponseEntity<String> CreateBoard(
            @Parameter(description = "creates a board for the specified user") @PathVariable String username) {
        return gobanService.createBoard(username);
    }

    @Operation(summary = "Place a stone on the board", description = "Allows a user to place a stone on the board at a specified position.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stone placed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid position or user action"),
            @ApiResponse(responseCode = "404", description = "User or board not found")
    })
    @PostMapping("/{username}/place/{x}/{y}")
    public ResponseEntity<String> placeAStone(
            @Parameter(description = "ID of the user placing the stone") @PathVariable String username,
            @Parameter(description = "X-coordinate of the position") @PathVariable Integer x,
            @Parameter(description = "Y-coordinate of the position") @PathVariable Integer y) {
        return gobanService.placeAStone(username, x, y);
    }
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stone placed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid position or user action"),
            @ApiResponse(responseCode = "404", description = "User or board not found")
    })
    @PostMapping("/{username}/undo/{x}/{y}")
    public ResponseEntity<String> undo(
            @Parameter(description = "username of the user removing the stone") @PathVariable String username,
            @Parameter(description = "X-coordinate of the position") @PathVariable Integer x,
            @Parameter(description = "Y-coordinate of the position") @PathVariable Integer y) {
        return gobanService.undoMove(username, x, y);
    }
    @Operation(summary = "Reset the current board state", description = "Retrieves the current state of the game board for the specified lobby.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board state retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Lobby not found")
    })
    @GetMapping("/reset/board/{username}")
    public String ResetBoardState(
            @Parameter(description = "username of user who sets the board") @PathVariable String username) {
        gobanService.resetBoard(username);
        return gobanService.getBoardState(username);
    }

    @Operation(summary = "Get the current board state", description = "Retrieves the current state of the game board for the specified lobby.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board state retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Lobby not found")
    })
    @GetMapping("/{username}/board")
    public String getBoardState(
            @Parameter(description = "ID of the lobby to retrieve the board state for") @PathVariable String username) {
        return gobanService.getBoardState(username);
    }

    @Operation(summary = "End the game", description = "Ends the game for the specified lobby.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game ended successfully"),
            @ApiResponse(responseCode = "404", description = "Lobby not found")
    })
    @DeleteMapping("/{username}/end")
    public ResponseEntity<String> endGame(
        @Parameter(description = "ID of the lobby where the game will end") @PathVariable String username) {
        System.out.println("is there anybody out there");
        return gobanService.endGame(username);
    }
}

