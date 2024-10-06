package com.example.Service;

import com.example.Entity.Player;
import com.example.Repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

    public List<Player> getTop100Players() {
        // Fetch all players from database
        List<Player> players = playerRepository.findAll();

        // Sort by rating in descending order
        players.sort(Comparator.comparing(Player::getRating).reversed());

        // Limit to top 100 players
        return players.stream().limit(100).collect(Collectors.toList());
    }
    public Player addPlayer(Player player) {
        return playerRepository.save(player);
    }

    public Player getUserById(int id) {
        return playerRepository.findById((long) id).orElse(null);
    }
    public Player updatePlayer(Player player) {
        return playerRepository.save(player);
    }
    public boolean deleteUserById(int id) {

        if (playerRepository.existsById((long) id)) {
            playerRepository.deleteById((long) id);
            return true;
        }
        return false;
    }


}
