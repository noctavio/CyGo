package com.example.sb.Controller;

import com.example.sb.Model.TheProfile;
import com.example.sb.Service.TheProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok("Player usernames and IDs have been transferred to the profiles table.");
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
}

