package com.example.sb.Service;

import com.example.sb.Entity.Club;
import com.example.sb.Entity.TheProfile;
import com.example.sb.Entity.User;
import com.example.sb.Repository.ClubRepository;
import com.example.sb.Repository.TheProfileRepository;
import com.example.sb.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClubService {
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private UserRepository userRepository;


    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }

    public Club getClubByID(Integer id) {
        // Retrieve the user by username
        Optional<Club> club = clubRepository.findById(id); // This should return the User object

        // If user is not found, handle it appropriately
        if (club == null) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        // Retrieve the profile using the User object
        return clubRepository.findByName(club.getclubname());
    }

    public void updateProfile(Club club) {
        clubRepository.save(club);
    }
}


