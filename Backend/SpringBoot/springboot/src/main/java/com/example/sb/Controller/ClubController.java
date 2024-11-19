package com.example.sb.Controller;

import com.example.sb.Model.Club;
import com.example.sb.Service.ClubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing clubs and their members.
 * Provides endpoints for CRUD operations on clubs and their associated members.
 */
@RestController
@RequestMapping("/clubs") // Base URL for the controller
@Tag(name = "Club Controller", description = "Endpoints for managing clubs and their members")
public class ClubController {

    @Autowired
    private ClubService clubService; // Injecting the ClubService dependency

    /**
     * Get all clubs.
     *
     * @return a list of all clubs.
     */
    @GetMapping
    @Operation(summary = "Get all clubs", description = "Retrieve a list of all clubs.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of clubs")
    })
    public List<Club> getAllClubs() {
        return clubService.getAllClubs();
    }

    /**
     * Get a club by its ID.
     *
     * @param id the ID of the club.
     * @return the club with the specified ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a club by ID", description = "Retrieve a specific club by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the club"),
            @ApiResponse(responseCode = "404", description = "Club not found")
    })
    public Club getClubById(@PathVariable Integer id) {
        return clubService.getClubByID(id);
    }

    /**
     * Add a new member to a club.
     *
     * @param id the ID of the club.
     * @param memberName the name of the member to add.
     * @return a success message.
     */
    @PostMapping("/{id}/members")
    @Operation(summary = "Add a member to a club", description = "Add a new member to a specified club.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully added the member"),
            @ApiResponse(responseCode = "400", description = "Invalid input or club not found")
    })
    public ResponseEntity<String> addMember(@PathVariable Integer id, @RequestBody String memberName) {
        clubService.addMemberToClub(id, memberName); // Adding the member to the club
        return ResponseEntity.ok("Member added successfully.");
    }

    /**
     * Remove a member from a club.
     *
     * @param id the ID of the club.
     * @param memberName the name of the member to remove.
     * @return a success or error message.
     */
    @DeleteMapping("/{id}/members/{memberName}")
    @Operation(summary = "Remove a member from a club", description = "Remove a specific member from a specified club.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully removed the member"),
            @ApiResponse(responseCode = "400", description = "Member not found in the club")
    })
    public ResponseEntity<String> removeMember(@PathVariable Integer id, @PathVariable String memberName) {
        if (clubService.removeMemberFromClub(id, memberName)) {
            return ResponseEntity.ok("Member removed successfully.");
        } else {
            return ResponseEntity.badRequest().body("Member not found in the club.");
        }
    }

    /**
     * Replace the entire list of members in a club.
     *
     * @param id the ID of the club.
     * @param newMembers the new list of members.
     * @return a success message.
     */
    @PutMapping("/{id}/members")
    @Operation(summary = "Update club members", description = "Replace the entire list of members for a specified club.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully updated the club members"),
            @ApiResponse(responseCode = "400", description = "Invalid input or club not found")
    })
    public ResponseEntity<String> updateMembers(@PathVariable Integer id, @RequestBody List<String> newMembers) {
        clubService.updateClubMembers(id, newMembers); // Updating the club's members
        return ResponseEntity.ok("Club members updated successfully.");
    }

    /**
     * Update the name of a specific member in a club.
     *
     * @param id the ID of the club.
     * @param oldMemberName the current name of the member.
     * @param newMemberName the new name for the member.
     * @return a success or error message.
     */
    @PutMapping("/{id}/members/{oldMemberName}")
    @Operation(summary = "Update a member's name", description = "Update the name of a specific member in a specified club.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully updated the member"),
            @ApiResponse(responseCode = "400", description = "Member not found")
    })
    public ResponseEntity<String> updateMember(
            @PathVariable Integer id,
            @PathVariable String oldMemberName,
            @RequestBody String newMemberName) {
        if (clubService.updateMemberInClub(id, oldMemberName, newMemberName)) {
            return ResponseEntity.ok("Member updated successfully.");
        } else {
            return ResponseEntity.badRequest().body("Member not found.");
        }
    }

    /**
     * Get the list of members in a club.
     *
     * @param id the ID of the club.
     * @return a list of members in the club.
     */
    @GetMapping("/{id}/members")
    @Operation(summary = "Get members of a club", description = "Retrieve the list of members for a specified club.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of members"),
            @ApiResponse(responseCode = "404", description = "Club not found")
    })
    public List<String> getMembers(@PathVariable Integer id) {
        return clubService.getMembersOfClub(id); // Fetching members of the club
    }

    /**
     * Update a club's details.
     *
     * @param id the ID of the club.
     * @param clubJSON the updated club details.
     * @return a success or error message.
     */
    @PutMapping("/update/{id}")
    @Operation(summary = "Update a club", description = "Update the details of a specific club.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully updated the club"),
            @ApiResponse(responseCode = "400", description = "Club not found or invalid input")
    })
    public ResponseEntity<String> updateClub(@PathVariable Integer id, @RequestBody Club clubJSON) {
        Club existingClub = clubService.getClubByID(id); // Fetching the existing club

        if (existingClub == null) {
            return ResponseEntity.badRequest().body("Club was not found");
        }

        // Updating club details
        if (clubJSON.getClubName() != null) {
            existingClub.setClubName(clubJSON.getClubName());
        }

        if (clubJSON.getClubPicture() != null) {
            existingClub.setClubPicture(clubJSON.getClubPicture());
        }

        clubService.updateClub(existingClub); // Persisting the updated club details

        return ResponseEntity.ok("The club has been updated accordingly.");
    }
}
