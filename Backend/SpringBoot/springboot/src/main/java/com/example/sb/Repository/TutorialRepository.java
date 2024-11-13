package com.example.sb.Repository;

import com.example.sb.Model.Tutorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorialRepository extends JpaRepository<Tutorial, Integer> {

    // Custom query method to find tutorials by gameId and moveNumber
    Tutorial findByGameIdAndMoveNumber(int gameId, int moveNumber);

}
