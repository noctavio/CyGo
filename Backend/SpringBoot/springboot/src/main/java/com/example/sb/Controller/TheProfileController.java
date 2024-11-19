package com.example.sb.Controller;

import com.example.sb.Model.TheProfile;
import com.example.sb.Service.TheProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Refresh player profiles", description = "Refreshes the profiles table by transferring player usernames and IDs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Player usernames and IDs have been transferred to the profiles table"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/profiles/refresh")
    public ResponseEntity<String> createProfilesFromUsers() {
        TheProfileService.updateProfileTable();
        return ResponseEntity.ok("Player usernames and IDs have been transferred to the profiles table.");
    }

    @Operation(summary = "Get top 10 players", description = "Returns a list of the top 10 players sorted by rank (highest to lowest)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved leaderboard"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/leaderboard")
    public List<TheProfile> getLeaderboard() {
        return TheProfileService.getTop10Players();
    }

    @Operation(summary = "Get all profiles", description = "Returns a list of all player profiles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved profiles"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/profiles")
    public List<TheProfile> getAllProfiles() {
        return TheProfileService.getAllProfiles();
    }

    @Operation(summary = "Get a profile by user ID", description = "Returns the profile of a player by their user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved profile"),
            @ApiResponse(responseCode = "404", description = "Profile not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(path = "/profiles/{userId}")
    public TheProfile getProfileById(@PathVariable Integer userId) {
        return TheProfileService.getProfileByID(userId);
    }
}
