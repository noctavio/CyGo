package com.example.sb.Service;

import com.example.sb.Entity.Leaderboard;
import com.example.sb.Entity.TheProfile;
import com.example.sb.Entity.User;
import com.example.sb.Repository.TheProfileRepository;
import com.example.sb.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TheProfileService {
    @Autowired
    private TheProfileRepository theProfileRepository;
    @Autowired
    private UserRepository userRepository;

    public List<TheProfile> getAllProfiles() {
        return theProfileRepository.findAll();
    }

    public TheProfile getProfileByUsername(String username) {
        return theProfileRepository.findByUsername(username);
    }

    public TheProfile createProfile(TheProfile profile) {

        return theProfileRepository.save(profile);
    }

    public void updateProfile(TheProfile profile) {
        theProfileRepository.save(profile);
    }

    public void updateProfileTable() {
        List<User> users = userRepository.findAll(); // Fetch all users

        for (User user : users) {
            // Fetch the profile by current username from theProfileRepository
            TheProfile existingProfile = theProfileRepository.findByUsername(user.getUsername());

            if (existingProfile == null) {
                // If no profile exists, create a new profile
                TheProfile newProfile = new TheProfile();
                newProfile.setUsername(user.getUsername());  // This can be changed in UserController
                newProfile.setProfilepicture("-/-");
                newProfile.setClubname("-/-");
                newProfile.setClubpicture("-/-");
                newProfile.setRank("30 kyu");
                newProfile.setWins(0);
                newProfile.setLoss(0);
                newProfile.setGamesplayed();

                theProfileRepository.save(newProfile);  // Save new profile
            }
            else {
                existingProfile.setUsername(user.getUsername());
                theProfileRepository.save(existingProfile);
            }
        }
    }
}
