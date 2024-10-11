package com.example.sb.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<profile, String> {
    profile findById(int id);

    profile deleteById(int id);
}
