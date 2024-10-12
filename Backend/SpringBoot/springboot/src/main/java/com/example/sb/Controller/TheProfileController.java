package com.example.sb.Controller;

import com.example.sb.Constants.RankConstants;
import com.example.sb.Entity.TheProfile;
import com.example.sb.Service.TheProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
@RequestMapping("/profiles")
@RestController
public class TheProfileController {

    @Autowired
    private TheProfileService TheProfileService;

    @GetMapping()
    public List<TheProfile> getAllProfiles() {
        return TheProfileService.getAllProfiles();
    }

    @GetMapping(path = "/{username}")
    public TheProfile getProfileByUsername(@PathVariable String username) {
        return TheProfileService.getProfileByUsername(username);
    }

    @PostMapping(path = "/create")
    public TheProfile createProfile(@RequestBody TheProfile profile) {
        return TheProfileService.createProfile(profile);
    }
    @PostMapping("/refresh")
    public ResponseEntity<String> createProfilesFromUsers() {
        TheProfileService.updateProfileTable();
        return ResponseEntity.ok("Player username's and ID's have been transferred to the leaderboard table.");
    }

    @PutMapping("/update/{username}")
    public ResponseEntity<String> updateProfile(@PathVariable String username, @RequestBody TheProfile profileJSON) {
        TheProfile existingUser = TheProfileService.getProfileByUsername(username);

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

        if (profileJSON.getClubpicture() != null) {
            existingUser.setClubpicture(profileJSON.getClubpicture());
        }

        if (profileJSON.getProfilepicture() != null) {
            existingUser.setProfilepicture(profileJSON.getProfilepicture());
        }

        if (profileJSON.getClubname() != null) {
            existingUser.setClubname(profileJSON.getClubname());
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
    // TODO READ -> this controller does not require a delete method since you are only updating/overwriting data here, only deletes in club!
    //@DeleteMapping("/{username}")
    //public ResponseEntity<String> deleteSomethingByUsername(@PathVariable String username) {
    //    return ResponseEntity.ok("HEY YOU");
    //}
}

