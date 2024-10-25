package com.example.sb.Repository;

import com.example.sb.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long>{

}
