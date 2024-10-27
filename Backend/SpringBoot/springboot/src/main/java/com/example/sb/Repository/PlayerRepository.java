package com.example.sb.Repository;

import com.example.sb.Entity.Lobby;
import com.example.sb.Entity.Player;
import com.example.sb.Entity.TheProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Integer> {
    Player findByProfile(TheProfile profile);
}

