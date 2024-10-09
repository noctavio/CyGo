package com.profile.User.profiles;

import org.springframework.data.jpa.repository.JpaRepository;

public interface profileRepository extends JpaRepository<profile, String> {
    profile findById(int id);

    profile deleteById(int id);
}
