package com.example.sb.Service;

import com.example.sb.Model.Lobby;
import com.example.sb.Model.Player;
import com.example.sb.Model.Team;
import com.example.sb.Model.TheProfile;
import com.example.sb.Repository.LobbyRepository;
import com.example.sb.Repository.PlayerRepository;
import com.example.sb.Repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TeamService {
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private LobbyRepository lobbyRepository;

    public Long getTimer(Integer teamId) {
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        if (teamOptional.isPresent()) {
            Team team = teamOptional.get();
            return team.getTimeRemaining();
        }
        return -1L;
    }

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
                player.setTeam(targetTeam);
            }

            else if (targetTeam.getPlayer2() == null) {
                targetTeam.setPlayer2(player);
                player.setTeam(targetTeam);
            }

            else {
                return ResponseEntity.ok(targetTeam.getTeamName() + " is full, cannot join team.");
            }

            targetTeam.setPlayerCount();
            teamRepository.save(targetTeam);
            playerRepository.save(player);
            return ResponseEntity.ok(profile.getUsername() + " joined team: " + targetTeam.getTeamName());
        }
        return ResponseEntity.ok("Specified team does not exist.");
    }

    public ResponseEntity<String> leaveTeam(Integer userId) {
        TheProfile profile = userService.findProfileById(userId);
        Player player = playerRepository.findByProfile(profile);

        Team targetTeam = player.getTeam();

        if (targetTeam != null) {
            if (targetTeam.getPlayer1() != null && targetTeam.getPlayer1().equals(player)) {
                targetTeam.setPlayer1(null);
                player.setTeam(null);
            }

            else if (targetTeam.getPlayer2() != null && targetTeam.getPlayer2().equals(player)) {
                targetTeam.setPlayer2(null);
                player.setTeam(null);
            }

            else {
                return ResponseEntity.ok("Player is not in the a team.");
            }

            player.setIsReady(false);
            targetTeam.setPlayerCount();
            if (targetTeam.getPlayer1() == null && targetTeam.getPlayer2() == null) {
                targetTeam.setTeamName("-/-");
            }

            teamRepository.save(targetTeam);
            playerRepository.save(player);
            return ResponseEntity.ok(profile.getUsername() + " left team: " + targetTeam.getTeamName());
        }
        return ResponseEntity.ok("Specified team does not exist.");
    }

    public List<Team> getTeams(Integer lobbyId) {
        Optional<Lobby> lobbyOptional = lobbyRepository.findById(lobbyId);
        if (lobbyOptional.isPresent()) {
            Lobby specificLobby = lobbyOptional.get();
            Team team1 = specificLobby.getTeam1();
            Team team2 = specificLobby.getTeam2();
            return Arrays.asList(team1, team2);
        }
        return Collections.emptyList();
    }
}
