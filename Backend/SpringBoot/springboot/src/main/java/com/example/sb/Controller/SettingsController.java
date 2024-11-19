package com.example.sb.Controller;

import ch.qos.logback.core.joran.event.SaxEventRecorder;
import com.example.sb.Model.Settings;
import com.example.sb.Model.TheProfile;
import com.example.sb.Model.User;
import com.example.sb.Repository.TheProfileRepository;
import com.example.sb.Repository.UserRepository;
import com.example.sb.Service.SettingsService;
import com.example.sb.Service.TheProfileService;
import com.example.sb.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/settings")
@Tag(name = "SettingsController", description = "Controller for managing user settings")
public class SettingsController {

    @Autowired
    private UserService userService;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private TheProfileService theProfileService;

    @Autowired
    private TheProfileRepository theProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "Get all settings", description = "Fetch all user settings from the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all settings",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Settings.class)))
    })
    @GetMapping
    public List<Settings> getAllSettings() {
        return settingsService.getAllSettings();
    }

    @Operation(summary = "Get setting by ID", description = "Fetch a specific setting by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the setting",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Settings.class))),
            @ApiResponse(responseCode = "404", description = "Setting not found")
    })
    @GetMapping("/{id}")
    public Settings getSettingById(@PathVariable Integer id) {
        return settingsService.getSettingsById(id);
    }

    @Operation(summary = "Update user settings", description = "Update user settings, including username, password, profile picture, and other fields.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the setting",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input or user not found")
    })
    @PutMapping("/update/{userId}")
    public ResponseEntity<String> updateSetting(
            @PathVariable Integer userId,
            @RequestBody(description = "Updated settings data", required = true,
                    content = @Content(schema = @Schema(implementation = Settings.class)))
            Settings settingJSON) {

        Optional<User> userOptional = userService.getByUserID(userId);

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            TheProfile existingProfile = theProfileService.getProfileByUser(existingUser);

            Settings existingSetting = existingProfile.getSettings();

            if (settingJSON.getProfile().getUser().getUsername() != null) {
                existingUser.setUsername(settingJSON.getProfile().getUser().getUsername());
            }

            if (settingJSON.getProfile().getUser().getPassword() != null) {
                String newPassword = settingJSON.getProfile().getUser().getPassword();
                if (!newPassword.isEmpty()) {
                    PasswordEncoder encoder = new BCryptPasswordEncoder();
                    existingUser.setPassword(encoder.encode(newPassword));
                }
            }

            if (settingJSON.getProfile().getProfilePicture() != null) {
                existingProfile.setProfilePicture(settingJSON.getProfile().getProfilePicture());
            }

            if (settingJSON.getPieceColor() != 0) {
                existingSetting.setPieceColor(settingJSON.getPieceColor());
            }

            if (settingJSON.getBoardColor() != 0) {
                existingSetting.setBoardColor(settingJSON.getBoardColor());
            }

            settingsService.updateSettings(existingSetting);
            theProfileRepository.save(existingProfile);
            userRepository.save(existingUser);
            return ResponseEntity.ok("The setting has been updated accordingly.");
        }
        return ResponseEntity.badRequest().body("User not found.");
    }
}
