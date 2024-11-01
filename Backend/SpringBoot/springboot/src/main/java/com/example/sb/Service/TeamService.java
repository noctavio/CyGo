package com.example.sb.Service;

import com.example.sb.Model.Player;
import com.example.sb.Model.Team;
import com.example.sb.Model.TheProfile;
import com.example.sb.Repository.PlayerRepository;
import com.example.sb.Repository.TeamRepository;
import com.example.sb.Repository.TheProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TeamService {
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PlayerRepository playerRepository;

    public ResponseEntity<String> updateTeamName(Integer userId, Team teamJSON) {
        TheProfile profile = userService.findProfileById(userId);
        Player player = playerRepository.findByProfile(profile);
        Team targetTeam = player.getTeam();
        targetTeam.setTeamName(teamJSON.getTeamName());

        teamRepository.save(targetTeam);
        return ResponseEntity.ok("Team named changed to: " + teamJSON.getTeamName());
    }

    public ResponseEntity<String> joinTeam(Integer userId, Integer teamId) {
        Optional<Team> team = teamRepository.findById(teamId);

        if (team.isPresent()) {
            Team targetTeam = team.get();
            TheProfile profile = userService.findProfileById(userId);
            Player player = playerRepository.findByProfile(profile);
            if (targetTeam.getPlayer1() == null) {
                targetTeam.setPlayer1(player);
            }

            else if (targetTeam.getPlayer2() == null) {
                targetTeam.setPlayer2(player);
            }

            else {
                return ResponseEntity.ok(targetTeam.getTeamName() + " is full, cannot join team.");
            }

            targetTeam.setPlayerCount();
            teamRepository.save(targetTeam);
            return ResponseEntity.ok(profile.getUsername() + " joined team: " + targetTeam.getTeamName());
        }
        return ResponseEntity.ok("Specified team does not exist.");
    }

    public ResponseEntity<String> leaveTeam(Integer userId, Integer teamId) {
        Optional<Team> team = teamRepository.findById(teamId);

        if (team.isPresent()) {
            Team targetTeam = team.get();
            TheProfile profile = userService.findProfileById(userId);
            Player player = playerRepository.findByProfile(profile);

            if (targetTeam.getPlayer1().equals(player)) {
                targetTeam.setPlayer1(null);
            }

            else if (targetTeam.getPlayer2().equals(player)) {
                targetTeam.setPlayer2(null);
            }

            else {
                return ResponseEntity.ok("Player is not in the specified team.");
            }

            player.setIsReady(false);
            targetTeam.setPlayerCount();
            if (targetTeam.getPlayer1() == null && targetTeam.getPlayer2() == null) {
                targetTeam.setTeamName("-/-");
            }

            teamRepository.save(targetTeam);
            return ResponseEntity.ok(profile.getUsername() + " left team: " + targetTeam.getTeamName());
        }
        return ResponseEntity.ok("Specified team does not exist.");
    }

}
