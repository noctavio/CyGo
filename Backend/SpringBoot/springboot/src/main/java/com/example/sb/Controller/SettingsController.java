package com.example.sb.Controller;

import com.example.sb.Model.Settings;
import com.example.sb.Service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/settings") // Base URL for the controller
public class SettingsController {

    @Autowired
    private SettingsService settingService;

    @GetMapping
    public List<Settings> getAllSettings() {
        return settingService.getAllSettings();
    }

    @GetMapping("/{id}")
    public Settings getSettingById(@PathVariable Integer id) {
        return settingService.getSettingsById(id);
    }

    @PostMapping
    public ResponseEntity<String> createSetting(@RequestBody Settings setting) {
        settingService.createSettings(setting); // Create a new setting
        return ResponseEntity.ok("Setting created successfully.");
    }

    @PutMapping("/update/{username}")
    public ResponseEntity<String> updateSetting(@PathVariable String username, @RequestBody Settings settingJSON) {
        Settings existingSetting = settingService.getSettingsByUsername(username);

        if (existingSetting == null) {
            return ResponseEntity.badRequest().body("Setting not found for the specified username.");
        }

        if (settingJSON.getPieceColor() != 0) { // Assuming 0 is not a valid PIECECOLOR
            existingSetting.setPieceColor(settingJSON.getPieceColor());
        }

        if (settingJSON.getBoardColor() != 0) { // Assuming 0 is not a valid BOARDCOLOR
            existingSetting.setBoardColor(settingJSON.getBoardColor());
        }

        if (settingJSON.getUsername() != null) {
            existingSetting.setUsername(settingJSON.getUsername());
        }

        settingService.updateSettings(existingSetting);

        return ResponseEntity.ok("The setting has been updated accordingly.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSetting(@PathVariable Integer id) {
        if (settingService.deleteSettings(id)) {
            return ResponseEntity.ok("Setting deleted successfully.");
        } else {
            return ResponseEntity.badRequest().body("Setting not found.");
        }
    }
}
