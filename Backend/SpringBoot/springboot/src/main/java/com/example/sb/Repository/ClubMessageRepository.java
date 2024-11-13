package com.example.sb.Repository;

import com.example.sb.Model.ClubMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubMessageRepository extends JpaRepository<ClubMessage, Integer> {
}
