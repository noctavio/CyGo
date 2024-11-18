package com.example.sb.Controller;

import com.example.sb.Constants.RankConstants;
import com.example.sb.Model.TheProfile;
import com.example.sb.Service.TheProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
@RestController
public class TheProfileController {

    @Autowired
    private TheProfileService TheProfileService;

    /**
     * Returns a list of the top 10 players sorted by rank (highest to lowest)
     * @return list
     */

    @PostMapping("/profiles/refresh")
    public ResponseEntity<String> createProfilesFromUsers() {
        TheProfileService.updateProfileTable();
        return ResponseEntity.ok("Player username's and ID's have been transferred to the profiles table.");
    }

    @GetMapping("/leaderboard")
    public List<TheProfile> getLeaderboard() {
        return TheProfileService.getTop10Players();
    }

    @GetMapping("/profiles")
    public List<TheProfile> getAllProfiles() {
        return TheProfileService.getAllProfiles();
    }

    @GetMapping(path = "/profiles/{userId}")
    public TheProfile getProfileById(@PathVariable Integer userId) {
        return TheProfileService.getProfileByID(userId);
    }

    @PutMapping("/profiles/update/{userId}")
    public ResponseEntity<String> updateProfile(@PathVariable Integer userId, @RequestBody TheProfile profileJSON) {
        TheProfile existingUser = TheProfileService.getProfileByID(userId);

        if (existingUser == null) {
            return ResponseEntity.badRequest().body("Profile was not found");
        }

        if ((profileJSON.getWins() != null && profileJSON.getWins() < 0) || (profileJSON.getLoss() != null && profileJSON.getLoss() < 0)) {

            return ResponseEntity.badRequest().body("Wins and losses must be non-negative.");
        }

        if (profileJSON.getRank() != null) {
            if (Arrays.asList(RankConstants.RANKS).contains(profileJSON.getRank())) {
                existingUser.setRank(profileJSON.getRank());
            }
            else {
                return ResponseEntity.badRequest().body("Invalid rank input... please review Go ranks -> (30 kyu - 1 kyu) and (1 dan - 9 dan)");
            }
        }

        if (profileJSON.getClub() != null) {
            existingUser.setClub(profileJSON.getClub());
        }

        if (profileJSON.getProfilepicture() != null) {
            existingUser.setProfilepicture(profileJSON.getProfilepicture());
        }

        if (profileJSON.getWins() != null) {
            existingUser.setWins(profileJSON.getWins());
        }

        if (profileJSON.getLoss() != null) {
            existingUser.setLoss(profileJSON.getLoss());
        }

        existingUser.setGamesplayed();
        TheProfileService.updateProfile(existingUser);

        return ResponseEntity.ok("The user has been updated accordingly.");
    }
}

