package com.example.sb.Repository;

import com.example.sb.Model.Player;
import com.example.sb.Model.TheProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Integer> {
    Player findByProfile(TheProfile profile);
}

