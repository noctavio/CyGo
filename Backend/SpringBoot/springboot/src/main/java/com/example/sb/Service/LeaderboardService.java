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
    private LeaderboardRepository leaderboardService;

    @Autowired
    private UserRepository userRepository;

    public List<Leaderboard> getTop10Players() {
        // Fetch all players from database
        List<Leaderboard> players = leaderboardService.findAll();

        // Sort by rank in descending order
        players.sort((player1, player2) -> {
            String rank1 = player1.getRank();
            String rank2 = player2.getRank();

            // Extract the numeric part and the type (dan/kyu)
            String type1 = rank1.split(" ")[1];
            String type2 = rank2.split(" ")[1];
            int value1 = Integer.parseInt(rank1.split(" ")[0]);
            int value2 = Integer.parseInt(rank2.split(" ")[0]);

            // Compare by type first
            if (!type1.equals(type2)) {
                return type1.equals("dan") ? -1 : 1; // dan is higher than kyu
            }
            // For dan, higher values are better (9 dan > 1 dan)
            if (type1.equals("dan")) {
                return Integer.compare(value2, value1); // Higher dan values come first
            }

            // Compare by numeric value if types are the same
            return Integer.compare(value1, value2); // Descending order
        });

        // Limit to top 10 players
        return players.stream().limit(10).collect(Collectors.toList());
    }
    public void createPlayersFromUsers() {
        List<User> users = userRepository.findAll(); // Fetch all users

        for (User user : users) {
            if (!leaderboardService.existsById((long) user.getId())) {
                Leaderboard player = new Leaderboard();
                player.setUsername(user.getUsername());
                player.setClubname("-/-");
                player.setRank("30 kyu");
                player.setWins(0);
                player.setLoss(0);
                player.setGamesplayed(0);

                leaderboardService.save(player); // Save the Player
            }
        }
    }

    public Leaderboard getUserById(int id) {
        return leaderboardService.findById((long) id).orElse(null);
    }
    public Leaderboard updatePlayer(Leaderboard player) {
        return leaderboardService.save(player);
    }
    public boolean deleteUserById(int id) {

        if (leaderboardService.existsById((long) id)) {
            leaderboardService.deleteById((long) id);
            return true;
        }
        return false;
    }
}
