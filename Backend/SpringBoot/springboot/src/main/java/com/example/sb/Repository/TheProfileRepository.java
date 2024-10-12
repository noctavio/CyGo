package com.example.sb.Repository;

import com.example.sb.Entity.TheProfile;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheProfileRepository extends JpaRepository<TheProfile, Integer> {
    TheProfile findByUsername(String username);

    TheProfile findById(int id); // different then the auto assigned one.

    @Transactional
    void deleteByUsername(String username);
}
