package com.example.sb.Controller;

import com.example.sb.Model.Team;
import com.example.sb.Service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/lobby/teams")
@RestController
public class TeamController {
    @Autowired
    private TeamService teamService;

    @GetMapping("/{teamId}/timer")
    public ResponseEntity<Long> getTimer(@PathVariable Integer teamId) {
        Long timer = teamService.getTimer(teamId);
        return ResponseEntity.ok(timer);
    }

    @PostMapping("/{userId}/join/{teamId}")
    public ResponseEntity<String> joinTeam(@PathVariable Integer userId, @PathVariable Integer teamId) {
        return teamService.joinTeam(userId, teamId);
    }

    @GetMapping("/{lobbyId}")
    public List<Team> getTeamsFromLobby(@PathVariable Integer lobbyId) {
        return teamService.getTeams(lobbyId);
    }

    @PutMapping("/{userId}/updateTeamName")
    public ResponseEntity<String> updateTeamName(@PathVariable Integer userId, @RequestBody Team teamJSON) {
        return teamService.updateTeamName(userId, teamJSON);
    }

    @PutMapping("/{userId}/updateTeamScore/{score}")
    public ResponseEntity<String> updateTeamScore(@PathVariable Integer userId, @PathVariable Integer score) {
        return teamService.updateTeamScore(userId, score);
    }

    @DeleteMapping("/{userId}/leave")
    public ResponseEntity<String> leaveTeam(@PathVariable Integer userId) {
        return teamService.leaveTeam(userId);
    }
}
