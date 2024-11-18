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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    public ResponseEntity<String> updateSetting(@PathVariable Integer settingsId, @RequestBody Settings settingJSON) {
        // Fetch the existing setting by username
        Settings existingSetting = settingsService.getSettingsById(settingsId);

        // If the setting doesn't exist, return an error
        if (existingSetting == null) {
            return ResponseEntity.badRequest().body("Setting not found for the specified username.");
        }

        User existingUser = userService.getByUsername(username);

        if (existingUser == null) {
            return ResponseEntity.badRequest().body("User not found.");
        }

        if (settingJSON.getUser().getUsername() != null) {
            existingUser.setUsername(settingJSON.getUser().getUsername());
        }

        if (settingJSON.getUser().getPassword() != null) {
            String newPassword = settingJSON.getUser().getPassword();
            //updates pass if it changed
            if (!newPassword.isEmpty()) {
                PasswordEncoder encoder = new BCryptPasswordEncoder();
                existingUser.setPassword(encoder.encode(newPassword));
            }
        }

        // Update other fields if provided in the settings
        if (settingJSON.getPiece_color() != 0) { // Assuming 0 is not a valid PIECECOLOR
            existingSetting.setPiece_color(settingJSON.getPiece_color());
        }

        if (settingJSON.getBoard_color() != 0) { // Assuming 0 is not a valid BOARDCOLOR
            existingSetting.setBoard_color(settingJSON.getBoard_color());
        }

        existingSetting.setUser(existingUser);
        settingsService.updateSettings(existingSetting);
        return ResponseEntity.ok("The setting has been updated accordingly.");
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteSetting(@PathVariable String username) {
        User user = userService.getByUsername(username);
        TheProfile theProfile = theProfileService.getProfileByUser(user);
        Settings settings = settingsService.getSettingsByUsername(username);

        settingsService.deleteSettings(settings.getSettings_id());
        userService.deleteByID(user.getUser_id());
        theProfileService.getProfileByID(theProfile.getProfile_id());
        return ResponseEntity.ok("deleted");
    }
}
