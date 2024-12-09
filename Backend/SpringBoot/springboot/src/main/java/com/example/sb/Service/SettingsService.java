package com.example.sb.Service;

import com.example.sb.Model.Settings;
import com.example.sb.Model.TheProfile;
import com.example.sb.Model.User;
import com.example.sb.Repository.SettingsRepository;
import com.example.sb.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SettingsService {

    @Autowired
    private SettingsRepository settingRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Settings> getAllSettings() {
        return settingRepository.findAll();
    }

    public Settings getSettingsById(Integer id) {
        Optional<Settings> setting = settingRepository.findById(id);
        return setting.orElseThrow(() -> new ResourceNotFoundException("Setting not found with ID: " + id));
    }

    public void updateSettings(Settings setting) {
        settingRepository.save(setting);
    }

    public ResponseEntity<String> deleteSettings(Integer id) {
        if (settingRepository.existsById(id)) {
            settingRepository.deleteById(id);
            return ResponseEntity.ok("Settings deleted.");
        }
        return ResponseEntity.ok("User does not exist...");
    }

}
