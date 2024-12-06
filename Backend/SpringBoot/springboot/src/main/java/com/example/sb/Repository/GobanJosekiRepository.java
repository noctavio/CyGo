package com.example.sb.Repository;

import com.example.sb.Model.GobanJoseki;
import com.example.sb.Model.Player;
import com.example.sb.Model.TheProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GobanJosekiRepository extends JpaRepository<GobanJoseki, Integer> {
    GobanJoseki findByGobanProfile(TheProfile profile);
}
