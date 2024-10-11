package com.example.sb.Repository;

import com.example.sb.Entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, String> {
    Profile findById(int id);

    Profile deleteById(int id);
}
