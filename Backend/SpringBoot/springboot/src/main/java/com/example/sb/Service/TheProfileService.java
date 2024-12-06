package com.example.sb.Service;

import com.example.sb.Constants.RankConstants;
import com.example.sb.Model.Friends;
import com.example.sb.Model.Settings;
import com.example.sb.Model.TheProfile;
import com.example.sb.Model.User;
import com.example.sb.Repository.FriendsRepository;
import com.example.sb.Repository.SettingsRepository;
import com.example.sb.Repository.TheProfileRepository;
import com.example.sb.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import java.util.TreeMap;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TheProfileService {
    @Autowired
    private TheProfileRepository theProfileRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SettingsRepository settingsRepository;
    @Autowired
    private FriendsRepository friendsRepository;

    private static final TreeMap<Integer, String> rankMapping = new TreeMap<>();

    static {
        int startingElo = 1000; // Elo for 30 kyu

        // Populate kyu ranks (30kyu to 1kyu)
        for (int kyu = 30; kyu >= 1; kyu--) {
            rankMapping.put(startingElo, kyu + "kyu");
            startingElo += 100; // Increment Elo by 100 for each kyu rank
        }

        // Populate dan ranks (1dan to 10dan)
        for (int dan = 1; dan <= 10; dan++) {
            rankMapping.put(startingElo, dan + "dan");
            startingElo += 100; // Increment Elo by 100 for each dan rank
        }
    }

    public List<TheProfile> getTop10Players() {
        // Fetch all players from database
        List<TheProfile> players = theProfileRepository.findAll();

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

    public List<TheProfile> getAllProfiles() {
        return theProfileRepository.findAll();
    }

    public TheProfile getProfileByUser(User user) {
        if (user == null) {
            throw new ResourceNotFoundException("User not found...");
        }
        return theProfileRepository.findByUser(Optional.of(user));
    }

    public TheProfile getProfileByID(Integer userId) {
        // Retrieve the user by username
        Optional<User> user = userRepository.findById(userId); // This should return the User object

        // If user is not found, handle it appropriately
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        // Retrieve the profile using the User object
        return theProfileRepository.findByUser(user);
    }

    public void updateProfileTable() {
        List<User> users = userRepository.findAll(); // Fetch all users

        for (User user : users) {
            // Fetch the profile by current user from theProfileRepository
            TheProfile existingProfile = theProfileRepository.findByUser(Optional.ofNullable(user));

            if (existingProfile == null) {
                // If no profile exists, create a new profile
                TheProfile newProfile = new TheProfile(user);
                Settings newSettings = new Settings(newProfile);
                Friends newFriends = new Friends(user);


                newProfile.setSettings(newSettings);
                theProfileRepository.save(newProfile);
                settingsRepository.save(newSettings);
                friendsRepository.save(newFriends);

            }
            else {
                existingProfile.setUser(user);
                theProfileRepository.save(existingProfile);
            }
        }
    }

    public void updateAfterGame(TheProfile profile, boolean isWin, int averageOpponentTeamElo) {
        int k = 30;
        double expectedScore = 1.0 / (1 + Math.pow(10, (averageOpponentTeamElo - profile.getElo()) / 400.0));
        int eloChange = (int) Math.round(k * ((isWin ? 1 : 0) - expectedScore));

        // Update Elo
        profile.setElo(profile.getElo() + eloChange);

        // Track wins/losses
        if (isWin) {
            profile.setWins(profile.getWins() + 1);
        }
        else {
            profile.setWins(profile.getLoss() + 1);
        }

        // Adjust rank
        adjustRank(profile);
    }

    // Adjust rank based on current Elo
    private void adjustRank(TheProfile profile) {
        for (var entry : rankMapping.entrySet()) {
            if (profile.getElo() >= entry.getKey()) {
                profile.setRank(entry.getValue());
            }
        }
    }
}