package com.example.sb.Controller;

import com.example.sb.Model.User;
import com.example.sb.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/users")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Register a new user", description = "Creates a new user account and stores it in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid user input")
    })
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(
            @Parameter(description = "Details of the user to be registered") @RequestBody User userJSON) {
        return userService.registerUser(userJSON);
    }

    @Operation(summary = "Get all users", description = "Fetches the list of all registered users.")
    @ApiResponse(responseCode = "200", description = "List of users retrieved successfully")
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Get all logged-in users", description = "Fetches the list of all currently logged-in users.")
    @ApiResponse(responseCode = "200", description = "List of logged-in users retrieved successfully")
    @GetMapping("/loggedIn")
    public List<User> getAllLoggedIn() {
        return userService.getAllLoggedIn();
    }

    @Operation(summary = "Get user by username", description = "Fetches details of a specific user by their username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{username}")
    public ResponseEntity<User> getByUsername(
            @Parameter(description = "Username of the user to fetch") @PathVariable String username) {
        User user = userService.getByUsername(username);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Log in a user", description = "Authenticates a user based on username and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PutMapping("/login/{username}/{password}")
    public String login(
            @Parameter(description = "Username of the user") @PathVariable String username,
            @Parameter(description = "Password of the user") @PathVariable String password) {
        return userService.authenticateUser(username, password, true);
    }

    @Operation(summary = "Admin privilege toggle", description = "Toggles administrator attribute for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Granted privileges "),
            @ApiResponse(responseCode = "401", description = "Invalid ID")
    })
    @PutMapping("/toggleAdmin/{userId}")
    public ResponseEntity<String> toggleAdmin(
            @Parameter(description = "ID of the user") @PathVariable Integer userId) {
        return userService.toggleAdmin(userId);
    }

    @Operation(summary = "Log out a user", description = "Logs out a specific user based on their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/logout/{userId}")
    public ResponseEntity<String> logout(
            @Parameter(description = "ID of the user to log out") @PathVariable Integer userId) {
        return userService.logoutUser(userId);
    }

    @Operation(summary = "Admin reset credentials", description = "Resets a players username/password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reset successful "),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("{adminId}/resetDetails/{userId}")
    public ResponseEntity<String> adminResetUserDetails(
            @Parameter(description = "ID of the admin.") @PathVariable Integer adminId,
            @Parameter(description = "ID of the user to reset.") @PathVariable Integer userId,
            @Parameter(description = "Specify type of reset") @RequestBody User userJSON) {
        return userService.adminResetUserDetails(adminId, userId, userJSON);
    }

    @Operation(summary = "User update details", description = "Updates details after an admin reset them.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reset successful "),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("updateAfterReset/{oldUsername}/{oldPassword}")
    public ResponseEntity<String> updateDetailsAfterAdminReset(
            @Parameter(description = "Username of account") @PathVariable String oldUsername,
            @Parameter(description = "Password of account") @PathVariable String oldPassword,
            @Parameter(description = "New username/password request body") @RequestBody User newUserJSON) {
        return userService.userResetDetails(oldUsername, oldPassword, newUserJSON);
    }

    @Operation(summary = "Suspend a user account", description = "Bans a user for a specified amount of time")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ban successful "),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("{adminId}/ban/{username}/length/{minuteLength}")
    public ResponseEntity<String> banUser(
            @Parameter(description = "adminId") @PathVariable Integer adminId,
            @Parameter(description = "Target username") @PathVariable String username,
            @Parameter(description = "Password of account") @PathVariable Integer minuteLength){
        return userService.banUser(adminId, username, minuteLength);
    }

    @Operation(summary = "Update a user's details", description = "Updates the details of a specific user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("{adminId}/hardDelete/{id}")
    public ResponseEntity<String> deleteByID(
            @Parameter(description = "ID of the user to delete") @PathVariable Integer id,
            @Parameter(description = "ID of the potential administrator") @PathVariable Integer adminId){
        return userService.deleteByID(adminId, id);
    }
}
