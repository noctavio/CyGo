package com.example.sb.Repository;

import com.example.sb.Model.TheProfile;
import com.example.sb.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TheProfileRepository extends JpaRepository<TheProfile, Integer> {

    TheProfile findByUser(Optional<User> user);

}
