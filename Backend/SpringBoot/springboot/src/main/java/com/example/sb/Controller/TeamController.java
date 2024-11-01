package com.example.sb.Controller;

import com.example.sb.Model.Team;
import com.example.sb.Service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/lobby/teams")
@RestController
public class TeamController {
    @Autowired
    private TeamService teamService;

    @PostMapping("/{userId}/join/{teamId}")
    public ResponseEntity<String> joinTeam(@PathVariable Integer userId, @PathVariable Integer teamId) {
        return teamService.joinTeam(userId, teamId);
    }

    @PutMapping("/{userId}/updateTeamName")
    public ResponseEntity<String> updateTeamName(@PathVariable Integer userId, @RequestBody Team teamJSON) {
        return teamService.updateTeamName(userId, teamJSON);
    }

    @PutMapping("/{userId}/leave")
    public ResponseEntity<String> leaveTeam(@PathVariable Integer userId) {
        return teamService.leaveTeam(userId);
    }
}
