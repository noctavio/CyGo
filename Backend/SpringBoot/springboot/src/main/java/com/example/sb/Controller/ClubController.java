package com.example.sb.Controller;

import com.example.sb.Model.Club;
import com.example.sb.Repository.ClubRepository;
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
 * Provides endpoints for retrieving, updating, and managing clubs and their memberships.
 */
@RestController
@RequestMapping("/clubs") // Base URL for the controller
@Tag(name = "Club Controller", description = "Endpoints for managing clubs and their members")
public class ClubController {

    @Autowired
    private ClubService clubService; // Service to manage club operations
    @Autowired
    private ClubRepository clubRepository;

    /**
     * Retrieve a list of all clubs.
     *
     * @return a list of all clubs.
     */
    @GetMapping
    @Operation(summary = "Get all clubs", description = "Retrieve a list of all clubs.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of clubs")
    })
    public List<Club> getAllClubs() {
        return clubService.getAllClubs(); // Fetch all clubs
    }

    /**
     * Retrieve details of a specific club by its ID.
     *
     * @param id the ID of the club.
     * @return the club with the specified ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get club by ID", description = "Retrieve details of a specific club by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the club details"),
            @ApiResponse(responseCode = "404", description = "Club not found")
    })
    public Club getClubById(@PathVariable Integer id) {
        return clubService.getClubByID(id); // Fetch club by ID
    }

    /**
     * create a club.
     *
     * @param club a club json
     * @return a success message.
     */
    @PostMapping("/create")
    @Operation(summary = "Add new club", description = "create a new club.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Club added successfully"),
            @ApiResponse(responseCode = "404", description = "Club not created")
    })
    public ResponseEntity<String> addClub(@RequestBody Club club) {
        clubRepository.save(club);// Add a new club
        return ResponseEntity.ok("Club added successfully.");
    }
    /**
     * Add a member to a specific club.
     *
     * @param id the ID of the club.
     * @param memberName the name of the member to be added.
     * @return a success message.
     */
    @PostMapping("/{id}/members")
    @Operation(summary = "Add member to club", description = "Add a new member to the specified club.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Member added successfully"),
            @ApiResponse(responseCode = "404", description = "Club not found")
    })
    public ResponseEntity<String> addMember(@PathVariable Integer id, @RequestBody String memberName) {
        clubService.addMemberToClub(id, memberName); // Add a member to the specified club
        return ResponseEntity.ok("Member added successfully.");
    }

    /**
     * Remove a member from a specific club.
     *
     * @param id the ID of the club.
     * @param memberName the name of the member to be removed.
     * @return a success or error message.
     */
    @DeleteMapping("/{id}/members/{memberName}")
    @Operation(summary = "Remove member from club", description = "Remove a member from the specified club.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Member removed successfully"),
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
     * Update the list of members in a specific club.
     *
     * @param id the ID of the club.
     * @param newMembers the new list of members.
     * @return a success message.
     */
    @PutMapping("/{id}/members")
    @Operation(summary = "Update club members", description = "Update the list of members in the specified club.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Club members updated successfully"),
            @ApiResponse(responseCode = "404", description = "Club not found")
    })
    public ResponseEntity<String> updateMembers(@PathVariable Integer id, @RequestBody List<String> newMembers) {
        clubService.updateClubMembers(id, newMembers); // Update club members
        return ResponseEntity.ok("Club members updated successfully.");
    }

    /**
     * Update a specific member in a club.
     *
     * @param id the ID of the club.
     * @param oldMemberName the name of the member to be updated.
     * @param newMemberName the new name for the member.
     * @return a success or error message.
     */
    @PutMapping("/{id}/members/{oldMemberName}")
    @Operation(summary = "Update member in club", description = "Update the name of a specific member in the club.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Member updated successfully"),
            @ApiResponse(responseCode = "400", description = "Member not found in the club")
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
     * Retrieve the list of members in a specific club.
     *
     * @param id the ID of the club.
     * @return a list of members in the specified club.
     */
    @GetMapping("/{id}/members")
    @Operation(summary = "Get club members", description = "Retrieve the list of members in the specified club.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of members"),
            @ApiResponse(responseCode = "404", description = "Club not found")
    })
    public List<String> getMembers(@PathVariable Integer id) {
        return clubService.getMembersOfClub(id); // Fetch members of the club
    }

    /**
     * Update the details of a specific club.
     *
     * @param id the ID of the club.
     * @param clubJSON the updated club details.
     * @return a success or error message.
     */
    @PutMapping("/update/{id}")
    @Operation(summary = "Update club details", description = "Update the details of a specific club.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The club was updated successfully"),
            @ApiResponse(responseCode = "400", description = "Club not found")
    })
    public ResponseEntity<String> updateClub(@PathVariable Integer id, @RequestBody Club clubJSON) {
        Club existingClub = clubService.getClubByID(id);

        if (existingClub == null) {
            return ResponseEntity.badRequest().body("Club was not found");
        }

        // Update club name if provided
        if (clubJSON.getClubName() != null) {
            existingClub.setClubName(clubJSON.getClubName());
        }

        // Update club picture if provided
        if (clubJSON.getClubPicture() != null) {
            existingClub.setClubPicture(clubJSON.getClubPicture());
        }

        // Save the updated club
        clubService.updateClub(existingClub);

        return ResponseEntity.ok("The club has been updated accordingly.");
    }
}