package com.example.sb.Repository;

import com.example.sb.Entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    Profile findByUSERNAME(String username);

    void deleteByUSERNAME(String username);
}
