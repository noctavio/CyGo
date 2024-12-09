package com.example.sb.Repository;


import com.example.sb.Model.FriendMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendsMessageRepository extends JpaRepository<FriendMessage, Integer> {
}
