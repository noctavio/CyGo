package com.example.sb.Repository;

import com.example.sb.Model.ClubMessage;
import com.example.sb.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubMessageRepository extends JpaRepository<ClubMessage, Long>{

}
