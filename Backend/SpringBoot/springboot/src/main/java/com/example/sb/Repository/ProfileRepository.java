package com.example.sb.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<profile, String> {
    Profile findById(int id);

    Profile deleteById(int id);
}
