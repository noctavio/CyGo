package com.example.sb.Controller;

import com.example.sb.Entity.Leaderboard;
import com.example.sb.Model.Player;
import com.example.sb.Repository.UserRepository;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;

@RequestMapping("/lobby")
@RestController
public class LobbyController {

    private Map<Integer, Player> playersInLobby = new HashMap<>();  // Player ID as key, Player object as value
    private UserRepository userRepository;

    @PutMapping("/ready/{playerId}")
    public String updateReadyStatus(@PathVariable Integer playerId, @RequestBody Map<String, Boolean> request) {
        boolean readyStatus = request.get("isReady");
        playersInLobby.get(playerId).setReady(readyStatus);

        return checkIfAllPlayersReady();
    }

    @PostMapping("/join")
    public String joinLobby(@RequestBody Leaderboard profile) { //TODO this is temporary user stats will be fetched from profile! NOT leaderboard...
        if (playersInLobby.containsKey(profile.getId())) {
            return profile.getUsername() + " is already in the lobby";
        }
        if (playersInLobby.size() < 4) {
            // Create a new Player object when they join
            if (!playersInLobby.containsKey(profile.getId())) {
                Player newPlayer = new Player(profile.getId());

                newPlayer.setUsername(profile.getUsername());
                newPlayer.setRank(profile.getRank());
                newPlayer.setClubname(profile.getClubname());

                playersInLobby.put(profile.getId(), newPlayer);

                return profile.getUsername() + " joined the lobby.";
            }
        }
        return "Lobby is full.";
    }
    @PostMapping("/invite/{username}")
    public String inviteToLobby(@PathVariable String username) {
        // TODO we need store access the database of registered users and check if they exist...
        return "User does not exist";
    }

    @GetMapping("/status")
    private String checkIfAllPlayersReady() {
        boolean leastOneNotReady = false;

        if (playersInLobby.size() == 4) {
            StringBuilder notReadyPlayers = new StringBuilder();

            for (Player player : playersInLobby.values()) {
                if (!player.isReady()){
                    leastOneNotReady = true;
                    notReadyPlayers.append("Player ")
                            .append(player.getUsername())
                            .append(" is not ready.\n");
                }
            }
            if (leastOneNotReady) {
                return notReadyPlayers.toString();
            }
            return "All players are ready. Game starting soon!";
        }
        return "Not enough players in lobby.";
    }

    @GetMapping()
    public Map<Integer, Player> getLobbyStatus() {
        return playersInLobby;
    }

    @DeleteMapping("/leave/{id}")
    public String leaveLobby(@PathVariable Integer id) {
        if (playersInLobby.containsKey(id)) {
            String name = playersInLobby.get(id).getUsername();
            playersInLobby.remove(id);
            return name + " left the lobby.";
        }
        return "Specified player not in the lobby";
    }
}

