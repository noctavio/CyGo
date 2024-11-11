package com.example.sb.Service;

import com.example.sb.Model.Settings;
import com.example.sb.Repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SettingsService {

    @Autowired
    private SettingsRepository settingRepository;

    public List<Settings> getAllSettings() {
        return settingRepository.findAll();
    }

    public Settings getSettingsById(Integer id) {
        Optional<Settings> setting = settingRepository.findById(id);
        return setting.orElseThrow(() -> new ResourceNotFoundException("Setting not found with ID: " + id));
    }

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

    public boolean updateSettingsByUsername(String username, Settings newSettingData) {
        Optional<Settings> optionalSetting = settingRepository.findByUsername(username);
        if (optionalSetting.isPresent()) {
            Settings existingSetting = optionalSetting.get();

            if (newSettingData.getPieceColor() != 0) { // Assuming 0 is not a valid PIECECOLOR
                existingSetting.setPieceColor(newSettingData.getPieceColor());
            }

            if (newSettingData.getBoardColor() != 0) { // Assuming 0 is not a valid BOARDCOLOR
                existingSetting.setBoardColor(newSettingData.getBoardColor());
            }

            if (newSettingData.getUsername() != null) {
                existingSetting.setUsername(newSettingData.getUsername());
            }

            settingRepository.save(existingSetting);
            return true;
        } else {
            throw new ResourceNotFoundException("Setting not found with username: " + username);
        }
    }
}
