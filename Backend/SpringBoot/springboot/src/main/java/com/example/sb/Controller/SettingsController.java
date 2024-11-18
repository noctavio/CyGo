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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/settings") // Base URL for the controller
public class SettingsController {

    @Autowired
    private UserService userService;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private TheProfileService theProfileService;


    @GetMapping
    public List<Settings> getAllSettings() {
        return settingsService.getAllSettings();
    }

    @GetMapping("/{id}")
    public Settings getSettingById(@PathVariable Integer id) {
        return settingsService.getSettingsById(id);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> createProfilesFromUsers() {
            settingsService.updateSettingsfromuser();
            return ResponseEntity.ok("Player usernames and IDs have been transferred to the profiles table.");

    }
    @PutMapping("/update/{username}")
    public ResponseEntity<String> updateSetting(@PathVariable String username, @RequestBody Settings settingJSON) {
        // Fetch the existing setting by username
        Settings existingSetting = settingsService.getSettingsByUsername(username);

        // If the setting doesn't exist, return an error
        if (existingSetting == null) {
            return ResponseEntity.badRequest().body("Setting not found for the specified username.");
        }

        // Fetch the user by username
        User user = userService.getByUsername(username);

        // If the user doesn't exist, return an error
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found.");
        }

        // Update the user's username if it's provided in the settingJSON
        if (settingJSON.getUsername() != null && !settingJSON.getUsername().equals(user.getUsername())) {
            user.setUsername(settingJSON.getUsername());
            userService.updateUser(Optional.of(user));  // Save the updated user
        }

        // Update other fields if provided in the settings
        if (settingJSON.getPieceColor() != 0) { // Assuming 0 is not a valid PIECECOLOR
            existingSetting.setPieceColor(settingJSON.getPieceColor());
        }

        if (settingJSON.getBoardColor() != 0) { // Assuming 0 is not a valid BOARDCOLOR
            existingSetting.setBoardColor(settingJSON.getBoardColor());
        }


        // Save the updated settings
        settingsService.updateSettings(existingSetting);

        return ResponseEntity.ok("The setting has been updated accordingly.");
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteSetting(@PathVariable String username) {

        User user = userService.getByUsername(username);
        TheProfile theProfile = theProfileService.getProfileByUser(user);
        Settings settings = settingsService.getSettingsByUsername(username);
        settingsService.deleteSettings(settings.getId());
        userService.deleteByID(user.getUser_id());
        theProfileService.getProfileByID(theProfile.getProfile_id());
        return ResponseEntity.ok("deleted");

    }

}
