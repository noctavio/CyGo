package com.example.sb.Service;

import com.example.sb.Entity.Leaderboard;
import com.example.sb.Entity.TheProfile;
import com.example.sb.Repository.LeaderboardRepository;
import com.example.sb.Repository.TheProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {
    @Autowired
    private LeaderboardRepository leaderboardRepository;
    @Autowired
    private TheProfileRepository theProfileRepository;
    @Autowired
    private TheProfileService theProfileService;

    public List<Leaderboard> getTop10Players() {
        // Fetch all players from database
        List<Leaderboard> players = leaderboardRepository.findAll();

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

    /**
     * This updates the leaderboard table, and profile table(all tables above it in hierarchy should be refreshed)
     * this is in case a user accesses the leaderboard in case a new player registers.
     * It pulls the data from Registered_Users, Updates Profiles, pulls the data from Profiles, and finally Updates the leaderboard.
     */
    public void updateLeaderboardTable() {
        theProfileService.updateProfileTable();

        List<TheProfile> userProfiles = theProfileRepository.findAll(); // Fetch all users
        List<Leaderboard> leaderboardUsers = leaderboardRepository.findAll();

        for (TheProfile profiles : userProfiles) {
            Leaderboard existingPlayer = leaderboardRepository.findByUsername(profiles.getUsername());

            if (leaderboardRepository.findByUsername(profiles.getUsername()) == null) {
                Leaderboard newPlayer = new Leaderboard();
                newPlayer.setUsername(profiles.getUsername());
                newPlayer.setClubname(profiles.getClubname());
                newPlayer.setRank(profiles.getRank());
                newPlayer.setWins(profiles.getWins());
                newPlayer.setLoss(profiles.getLoss());
                newPlayer.setGamesplayed(profiles.getGames());

                leaderboardRepository.save(newPlayer); // Save the Player
            }
            else {
                existingPlayer.setUsername(profiles.getUsername());
                existingPlayer.setClubname(profiles.getClubname());
                existingPlayer.setRank(profiles.getRank());
                existingPlayer.setWins(profiles.getWins());
                existingPlayer.setLoss(profiles.getLoss());
                existingPlayer.setGamesplayed(profiles.getGames());

                leaderboardRepository.save(existingPlayer);
            }
        }
    }

    //public Leaderboard getByUsername(String username) {
    //    return leaderboardRepository.findByUsername(username);
    //}

    //public void updatePlayer(Leaderboard player) {
    //    leaderboardRepository.save(player);
    //}
}
