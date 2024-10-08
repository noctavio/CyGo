package com.example.sb.Service;

import com.example.sb.Entity.Leaderboard;
import com.example.sb.Entity.User;
import com.example.sb.Repository.LeaderboardRepository;
import com.example.sb.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {
    @Autowired
    private LeaderboardRepository playerRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Leaderboard> getTop10Players() {
        // Fetch all players from database
        List<Leaderboard> players = playerRepository.findAll();

        // Sort by rating in descending order
        players.sort(Comparator.comparing(Leaderboard::getRating));

        // Limit to top 100 players
        return players.stream().limit(10).collect(Collectors.toList());
    }
    public void createPlayersFromUsers() {
        List<User> users = userRepository.findAll(); // Fetch all users

        for (User user : users) {
            if (!playerRepository.existsById((long) user.getId())) {
                Leaderboard player = new Leaderboard();
                player.setUsername(user.getUsername());
                player.setClubname("-/-");
                player.setRating(1500);
                player.setWins(0);
                player.setLoss(0);
                player.setGamesplayed(0);

                playerRepository.save(player); // Save the Player
            }
        }
    }

    public Leaderboard getUserById(int id) {
        return playerRepository.findById((long) id).orElse(null);
    }
    public Leaderboard updatePlayer(Leaderboard player) {
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
