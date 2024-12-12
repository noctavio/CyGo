package com.example.sb.Controller;



import com.example.sb.Model.User;
import com.example.sb.Repository.FriendsRepository;
import com.example.sb.Service.FriendsService;
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
@RequestMapping("/friends") // Base URL for the controller
@Tag(name = "Club Controller", description = "Endpoints for managing clubs and their members")
public class FriendsController {

    @Autowired
    private FriendsService friendsService; // Service to manage club operations
    @Autowired
    private FriendsRepository friendsRepository;

    /**
     * Retrieve a list of all clubs.
     *
     * @return a list of all clubs.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get friends by ID", description = "Retrieve friends of a specific user by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the club details"),
            @ApiResponse(responseCode = "404", description = "Club not found")
    })
    public List<User> getAllFriends(@PathVariable Integer id) {
        return friendsService.getAllFriends(id); // Fetch club by ID
    }

    /**
     * Retrieve details of a specific club by its ID.
     *
     * @param user_id the ID of the club.
     * @return the club with the specified ID.
     */
    @GetMapping("/{id}/{user_id}")
    @Operation(summary = "Get friend by their ID", description = "Retrieve details of a specific friend by their ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the club details"),
            @ApiResponse(responseCode = "404", description = "Club not found")
    })
    public User getFriendById(@PathVariable Integer user_id, @PathVariable String friendName) {
        return friendsService.getAFriend(user_id, friendName); // Fetch club by ID
    }




    /**
     * Add a member to a specific friends list.
     *
     * @param user_id         the ID of the friends list.
     * @param friendName the name of the friend to be added.
     * @return a success message.
     */
    @PostMapping("/{user_id}/{friendName}")
    @Operation(summary = "Add friend to list", description = "Add a new friend to the specified friend list.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Member added successfully"),
            @ApiResponse(responseCode = "404", description = "Club not found")
    })
    public ResponseEntity addMember(@PathVariable Integer user_id, @PathVariable String friendName) {

        return friendsService.addFriends(user_id, friendName); // Add a friend to the specified user
    }

    /**
     * Remove a member from a specific club.
     *
     * @param id the ID of the club.
     * @param friendName the name of the member to be removed.
     * @return a success or error message.
     */
    @DeleteMapping("/delete/{id}/{friendName}")
    @Operation(summary = "Remove friend from friend list", description = "Remove a friend from the specified friend list.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "friend removed successfully"),
            @ApiResponse(responseCode = "400", description = "friend not found in the club")
    })
    public ResponseEntity<String> removeMember(@PathVariable Integer id, @PathVariable String friendName) {
        if (friendsService.removeFriend(id, friendName)) {
            return ResponseEntity.ok("Friend removed successfully.");
        } else {
            return ResponseEntity.badRequest().body("Member not found in the club.");
        }
    }

}