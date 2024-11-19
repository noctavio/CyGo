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
    @Autowired
    private TheProfileRepository theProfileRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Settings> getAllSettings() {
        return settingsService.getAllSettings();
    }

    @GetMapping("/{id}")
    public Settings getSettingById(@PathVariable Integer id) {
        return settingsService.getSettingsById(id);
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<String> updateSetting(@PathVariable Integer userId, @RequestBody Settings settingJSON) {
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
                // updates pass if it changed and is not an empty string!
                if (!newPassword.isEmpty()) {
                    PasswordEncoder encoder = new BCryptPasswordEncoder();
                    existingUser.setPassword(encoder.encode(newPassword));
                }
            }

            if (settingJSON.getProfile().getProfilePicture() != null) {
                existingProfile.setProfilePicture(settingJSON.getProfile().getProfilePicture());
            }

            // Update other fields if provided in the settings
            if (settingJSON.getPieceColor() != 0) { // Assuming 0 is not a valid PIECECOLOR
                existingSetting.setPieceColor(settingJSON.getPieceColor());
            }

            if (settingJSON.getBoardColor() != 0) { // Assuming 0 is not a valid BOARDCOLOR
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
