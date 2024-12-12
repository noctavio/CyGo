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

    public List<User> getAllFriends(Integer user_id) {
        Optional<User> optionalUser = userRepository.findById(user_id);
        Optional<Friends> friends;
        List<String> friendsList;
        if(optionalUser.isEmpty()){
            return null;
        }else{
            friends = friendsRepository.findByUser(optionalUser.get());
        }
        if (friends.isPresent()) {
            friendsList = friends.get().getFriends();
        }
        else{
            return null;
        }
        List<User> userList = new ArrayList<>();
        for (String friendName : friendsList) {
            if (!friendName.equals(null)) {
                User user = userRepository.findByUsername(friendName);
                userList.add(user);
            }
        }
        return userList;
    }

    public User getAFriend(Integer user_id, String username ){
        Optional<User> optionalUser = userRepository.findById(user_id);
        Optional<Friends> friends;
        List<String> friendsList;
        if(optionalUser.isPresent()) {
            friends = friendsRepository.findByUser(optionalUser.get());
        }
        else{
            return null;
        }
        if(friends.isPresent()) {
            friendsList = friends.get().getFriends();
        }
        else{
            return null;
        }
        User user = null;
        for (String friendName : friendsList) {
            user = userRepository.findByUsername(username);
            if (friendName != null && friendName.equals(user.getUsername())) {
                return user;
            }
        }
        return user;
    }

    public ResponseEntity addFriends(Integer user_id, String friendName) {
        Optional<User> optionalUser = userRepository.findById(user_id);
        Optional<Friends> optionalFriends;
        if(optionalUser.isEmpty()){
            return ResponseEntity.ok("user not found");
        }
        else{

            optionalFriends = friendsRepository.findByUser(optionalUser.get());
        }
        if (optionalFriends.isPresent()) {
            Friends friends = optionalFriends.get();
            friends.getFriends().add(friendName);// Add the new member
            friendsRepository.save(friends); // Save the updated club
        } else {
            throw new ResourceNotFoundException("user's friends not found with id: " + user_id);
        }
        return ResponseEntity.ok("friend added.");
    }

    public boolean removeFriend(int user_id, String friendsName) {
        Optional<User> optionalUser = userRepository.findById(user_id);
        Optional<Friends> optionalFriends;
        if(optionalUser.isPresent()){
            optionalFriends = friendsRepository.findByUser(optionalUser.get());
        }
        else{
            return false;
        }

        if (optionalFriends.isPresent()) {
            Friends friends = optionalFriends.get();
            friends.getFriends().remove(friendsName);
            friendsRepository.save(friends);
            return true; // Remove the member and return the result
        } else {
            throw new ResourceNotFoundException("user not found with id: " + user_id);
        }
    }
}



