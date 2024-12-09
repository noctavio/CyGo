package com.example.sb.Service;

import com.example.sb.Model.Club;
import com.example.sb.Repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClubService {
    @Autowired
    private ClubRepository clubRepository;

    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }

    public Club getClubByID(Integer id) {
        Optional<Club> club = clubRepository.findById(id);
        if (club.isPresent()) {
            return club.get();
        } else {
            throw new ResourceNotFoundException("Club not found with id: " + id);
        }
    }

    public void updateClub(Club club) {
        clubRepository.save(club);
    }

    public void addMemberToClub(int clubId, String memberName) {
        Optional<Club> optionalClub = clubRepository.findById(clubId);
        if (optionalClub.isPresent()) {
            Club club = optionalClub.get();
            club.getMembers().add(memberName); // Add the new member
            clubRepository.save(club); // Save the updated club
        } else {
            throw new ResourceNotFoundException("Club not found with id: " + clubId);
        }
    }

    public boolean removeMemberFromClub(int clubId, String memberName) {
        Optional<Club> optionalClub = clubRepository.findById(clubId);
        if (optionalClub.isPresent()) {
            Club club = optionalClub.get();
            return club.getMembers().remove(memberName); // Remove the member and return the result
        } else {
            throw new ResourceNotFoundException("Club not found with id: " + clubId);
        }
    }

    public void updateClubMembers(int clubId, List<String> newMembers) {
        Optional<Club> optionalClub = clubRepository.findById(clubId);
        if (optionalClub.isPresent()) {
            Club club = optionalClub.get();
            club.setMembers(newMembers); // Replace the members list
            clubRepository.save(club); // Save the updated club
        } else {
            throw new ResourceNotFoundException("Club not found with id: " + clubId);
        }
    }

    public List<String> getMembersOfClub(int clubId) {
        Optional<Club> optionalClub = clubRepository.findById(clubId);
        if (optionalClub.isPresent()) {
            return optionalClub.get().getMembers(); // Return the list of members
        } else {
            throw new ResourceNotFoundException("Club not found with id: " + clubId);
        }
    }

    public boolean updateMemberInClub(int clubId, String oldMemberName, String newMemberName) {
        Optional<Club> optionalClub = clubRepository.findById(clubId);
        if (optionalClub.isPresent()) {
            Club club = optionalClub.get();
            if (club.getMembers().remove(oldMemberName)) {
                club.getMembers().add(newMemberName); // Add the new member name
                clubRepository.save(club); // Save the updated club
                return true; // Return true if the member was updated
            }
            return false; // Return false if the old member name was not found
        } else {
            throw new ResourceNotFoundException("Club not found with id: " + clubId);
        }
    }
}