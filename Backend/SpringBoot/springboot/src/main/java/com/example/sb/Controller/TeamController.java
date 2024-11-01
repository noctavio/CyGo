package com.example.sb.Controller;

import com.example.sb.Model.Team;
import com.example.sb.Service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/lobby/team")
@RestController
public class TeamController {
    @Autowired
    private TeamService teamService;

    @PostMapping("/{userId}/joinTeam/{teamId}")
    public ResponseEntity<String> joinTeam(@PathVariable Integer userId, @PathVariable Integer teamId) {
        return teamService.joinTeam(userId, teamId);
    }

    @PutMapping("/updateTeamName/{userId}")
    public ResponseEntity<String> updateTeamName(@PathVariable Integer userId, @RequestBody Team teamJSON) {
        return teamService.updateTeamName(userId, teamJSON);
    }

    // TODO this does not work!
    @PutMapping("/{userId}/leaveTeam/{teamId}")
    public ResponseEntity<String> leaveTeam(@PathVariable Integer userId, @PathVariable Integer teamId) {
        return teamService.leaveTeam(userId, teamId);
    }
}
