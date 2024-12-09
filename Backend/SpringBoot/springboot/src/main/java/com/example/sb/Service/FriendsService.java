package com.example.sb.Service;

import com.example.sb.Model.Club;
import com.example.sb.Model.Friends;
import com.example.sb.Model.User;
import com.example.sb.Repository.ClubRepository;
import com.example.sb.Repository.FriendsRepository;
import com.example.sb.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FriendsService {
    @Autowired
    private FriendsRepository friendsRepository;
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllFriends(Integer id) {
        Optional<Friends> friends = friendsRepository.findById(id);
        List<String> friendsList = friends.get().getFriends();
        List<User> userList = new ArrayList<>();
        for (String friendName : friendsList) {
            if (!friendName.equals(null)) {
                User user = userRepository.findByUsername(friendName);
                userList.add(user);
            }
        }
        return userList;
    }

    public User getAFriend(Integer id, Integer user_id) {
        Optional<Friends> friends = friendsRepository.findById(id);
        List<String> friendsList = friends.get().getFriends();
        User user = null;
        for (String friendName : friendsList) {
            user = userRepository.findByUsername(friendName);
            if (friendName != null && user.getUser_id() == user_id) {
                return user;
            }
        }
        return user;
    }

    public ResponseEntity addFriends(int friendsId, String friendName) {
        Optional<Friends> optionalFriends = friendsRepository.findById(friendsId);
        if (optionalFriends.isPresent()) {
            Friends friends = optionalFriends.get();
            friends.getFriends().add(friendName);// Add the new member
            friendsRepository.save(friends); // Save the updated club
        } else {
            throw new ResourceNotFoundException("user's friends not found with id: " + friendsId);
        }
        return ResponseEntity.ok("friend added.");
    }

    public boolean removeFriend(int friendId, String friendsName) {
        Optional<Friends> optionalFriends = friendsRepository.findById(friendId);
        if (optionalFriends.isPresent()) {
            Friends friends = optionalFriends.get();
            return friends.getFriends().remove(friendsName); // Remove the member and return the result
        } else {
            throw new ResourceNotFoundException("Club not found with id: " + friendId);
        }
    }
}



