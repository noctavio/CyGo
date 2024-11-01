package com.example.sb.Service;

import com.example.sb.Model.TheProfile;
import com.example.sb.Model.User;
import com.example.sb.Repository.TheProfileRepository;
import com.example.sb.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TheProfileService {
    @Autowired
    private TheProfileRepository theProfileRepository;
    @Autowired
    private UserRepository userRepository;

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

    public void updateProfile(TheProfile profile) {
        theProfileRepository.save(profile);
    }

    public void updateProfileTable() {
        List<User> users = userRepository.findAll(); // Fetch all users

        for (User user : users) {
            // Fetch the profile by current username from theProfileRepository
            TheProfile existingProfile = theProfileRepository.findByUser(Optional.ofNullable(user));

            // TODO dynamically update table to delete(nvm already done in hard delete??)
            if (existingProfile == null) {
                // If no profile exists, create a new profile
                TheProfile newProfile = new TheProfile(user);
                theProfileRepository.save(newProfile);  // Save new profile
            }
            else {
                existingProfile.setUser(user);
                theProfileRepository.save(existingProfile);
            }
        }
    }
}
