package com.example.sb.Repository;

import com.example.sb.Model.Settings;
import com.example.sb.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SettingsRepository extends JpaRepository<Settings, Integer> {
    // Custom method to find a setting by its username
    Optional<Settings> findByUsername(String username);

    Optional<Settings> findByUser(Optional<User> user);
}