package com.example.sb.Repository;

import com.example.sb.Model.GobanJoseki;
import com.example.sb.Model.Joseki;
import com.example.sb.Model.TheProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JosekiRepository extends JpaRepository<Joseki, Integer> {
    Joseki findByLastMove(String lastMove);
}
