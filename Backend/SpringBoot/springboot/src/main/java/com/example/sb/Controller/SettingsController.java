package com.example.sb.Controller;

import com.example.sb.Model.Settings;
import com.example.sb.Model.User;
import com.example.sb.Service.SettingsService;
import com.example.sb.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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


    @GetMapping
    public List<Settings> getAllSettings() {
        return settingsService.getAllSettings();
    }

    @GetMapping("/{id}")
    public Settings getSettingById(@PathVariable Integer id) {
        return settingsService.getSettingsById(id);
    }

    @PostMapping
    public ResponseEntity<String> createSetting(@RequestBody Settings setting) {
        settingsService.createSettings(setting); // Create a new setting
        return ResponseEntity.ok("Setting created successfully.");
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

        if (settingJSON.getUsername() != null) {
            existingSetting.setUsername(settingJSON.getUsername());
        }

        // Save the updated settings
        settingsService.updateSettings(existingSetting);

        return ResponseEntity.ok("The setting has been updated accordingly.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSetting(@PathVariable Integer id) {
        if (settingsService.deleteSettings(id)) {
            return ResponseEntity.ok("Setting deleted successfully.");
        } else {
            return ResponseEntity.badRequest().body("Setting not found.");
        }
    }
}
