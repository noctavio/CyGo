package com.example.sb.Service;

import com.example.sb.Model.Settings;
import com.example.sb.Model.User;
import com.example.sb.Repository.SettingsRepository;
import com.example.sb.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
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
    @Transactional
    public Settings getSettingsByUsername(String username) {
        Optional<Settings> setting = settingRepository.findByUsername(username);
        return setting.orElseThrow(() -> new ResourceNotFoundException("Setting not found with username: " + username));
    }

    public void createSettings(Settings setting) {
        settingRepository.save(setting);
    }

    public void updateSettings(Settings setting) {
        settingRepository.save(setting);
    }

    public boolean deleteSettings(Integer id) {
        if (settingRepository.existsById(id)) {
            settingRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public void updateSettingsfromuser() {
        List<User> users = userRepository.findAll(); // Fetch all users

        for (User user : users) {
            // Fetch the profile by current user from theProfileRepository
            Settings settings = settingRepository.findByUser(Optional.ofNullable(user)).orElse(null);

            if (settings == null) {
                // If no profile exists, create a new profile
                String username = user.getUsername();
                Settings newSettings = new Settings(user, username);
                newSettings.setUsername(username);
                settingRepository.save(newSettings);
                // Save new profile
            }
            else {
                settings.setUser(user);
                settingRepository.save(settings);
            }
        }
    }
}
