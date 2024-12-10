package com.example.sb.Controller;

import com.example.sb.Model.Team;
import com.example.sb.Repository.TeamRepository;
import com.example.sb.Service.GobanService;
import com.example.sb.Service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/lobby/teams")
@RestController
public class TeamController {
    @Autowired
    private TeamService teamService;
    @Autowired
    private GobanService gobanService;
    @Autowired
    private TeamRepository teamRepository;

    @Operation(summary = "Get team timer", description = "Retrieves the timer value associated with a specific team.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Timer retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Team not found")
    })
    @GetMapping("/{teamId}/timer")
    public ResponseEntity<Long> getTimer(
            @Parameter(description = "ID of the team to retrieve the timer for") @PathVariable Integer teamId) {
        Long timer = teamService.getTimer(teamId);
        return ResponseEntity.ok(timer);
    }

    @Operation(summary = "Join a team", description = "Allows a user to join a specific team.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User joined the team successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user or team ID"),
            @ApiResponse(responseCode = "404", description = "Team not found")
    })
    @PostMapping("/{userId}/join/{teamId}")
    public ResponseEntity<String> joinTeam(
            @Parameter(description = "ID of the user joining the team") @PathVariable Integer userId,
            @Parameter(description = "ID of the team the user is joining") @PathVariable Integer teamId) {
        return teamService.joinTeam(userId, teamId);
    }

    @Operation(summary = "Get teams from lobby", description = "Retrieves a list of all teams associated with a specific lobby.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of teams retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Lobby not found")
    })
    @GetMapping("/{lobbyId}")
    public List<Team> getTeamsFromLobby(
            @Parameter(description = "ID of the lobby to retrieve teams from") @PathVariable Integer lobbyId) {
        return teamService.getTeams(lobbyId);
    }

    @Operation(summary = "Update team name", description = "Updates the name of a team.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Team name updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid team data"),
            @ApiResponse(responseCode = "404", description = "User or team not found")
    })
    @PutMapping("/{userId}/updateTeamName")
    public ResponseEntity<String> updateTeamName(
            @Parameter(description = "ID of the user updating the team name") @PathVariable Integer userId,
            @Parameter(description = "JSON object containing the new team name") @RequestBody Team teamJSON) {
        return teamService.updateTeamName(userId, teamJSON);
    }

    @Operation(summary = "Time elapsed for team", description = "Ends the game if a teams timer has elapsed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game ended"),
            @ApiResponse(responseCode = "404", description = "Team not found")
    })
    @PutMapping("/{teamId}/timeElapsed")
    public ResponseEntity<String> timeElapsed(
            @Parameter(description = "ID of the target team") @PathVariable Integer teamId) {
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        if (teamOptional.isPresent()) {
            Team team = teamOptional.get();
            gobanService.endGame(team.getLobby().getLobby_id(), false);
            return ResponseEntity.ok("Game ended by timeElapsed of team: " + team.getTeamName());
        }
        return ResponseEntity.ok("No team at specified id: " + teamId);
    }

    @Operation(summary = "Leave a team", description = "Allows a user to leave their current team.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User left the team successfully"),
            @ApiResponse(responseCode = "404", description = "User not associated with any team")
    })
    @DeleteMapping("/{userId}/leave")
    public ResponseEntity<String> leaveTeam(
            @Parameter(description = "ID of the user leaving the team") @PathVariable Integer userId) {
        return teamService.leaveTeam(userId);
    }
}
