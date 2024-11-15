package com.example.sb.Controller;

import com.example.sb.Model.TheProfile;
import com.example.sb.Service.TheProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(summary = "Refresh profiles from users", description = "Transfers player usernames and IDs from the user table to the profiles table.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profiles successfully updated"),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred while refreshing profiles")
    })
    @PostMapping("/profiles/refresh")
    public ResponseEntity<String> createProfilesFromUsers() {
        TheProfileService.updateProfileTable();
        return ResponseEntity.ok("Player usernames and IDs have been transferred to the profiles table.");
    }

    @Operation(summary = "Get leaderboard", description = "Returns a list of the top 10 players sorted by rank (highest to lowest).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leaderboard retrieved successfully")
    })
    @GetMapping("/leaderboard")
    public List<TheProfile> getLeaderboard() {
        return TheProfileService.getTop10Players();
    }

    @Operation(summary = "Get all profiles", description = "Fetches a list of all player profiles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of profiles retrieved successfully")
    })
    @GetMapping("/profiles")
    public List<TheProfile> getAllProfiles() {
        return TheProfileService.getAllProfiles();
    }

    @Operation(summary = "Get profile by ID", description = "Fetches the profile of a specific user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Profile not found for the given ID")
    })
    @GetMapping(path = "/profiles/{userId}")
    public TheProfile getProfileById(
            @Parameter(description = "ID of the user whose profile is to be retrieved") @PathVariable Integer userId) {
        return TheProfileService.getProfileByID(userId);
    }

    @Operation(summary = "Update profile", description = "Updates the profile of a specific user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Profile not found for the given ID")
    })
    @PutMapping("/profiles/update/{userId}")
    public ResponseEntity<String> updateProfile(
            @Parameter(description = "ID of the user whose profile is to be updated") @PathVariable Integer userId,
            @Parameter(description = "Updated profile details") @RequestBody TheProfile profileJSON) {
        TheProfile existingUserProfile = TheProfileService.getProfileByID(userId);
        return TheProfileService.updateProfile(existingUserProfile, profileJSON);
    }
}
