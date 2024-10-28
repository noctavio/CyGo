package com.example.sb.Controller;

import com.example.sb.Entity.Club;
import com.example.sb.Service.ClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clubs") // Base URL for the controller
public class ClubController {

    @Autowired
    private ClubService clubService;

    @GetMapping
    public List<Club> getAllClubs() {
        return clubService.getAllClubs();
    }

    @GetMapping("/{id}")
    public Club getClubById(@PathVariable Integer id) {
        return clubService.getClubByID(id);
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<String> addMember(@PathVariable Integer id, @RequestBody String memberName) {
        clubService.addMemberToClub(id, memberName); // Add a member to the specified club
        return ResponseEntity.ok("Member added successfully.");
    }

    @DeleteMapping("/{id}/members/{memberName}")
    public ResponseEntity<String> removeMember(@PathVariable Integer id, @PathVariable String memberName) {
        if (clubService.removeMemberFromClub(id, memberName)) {
            return ResponseEntity.ok("Member removed successfully.");
        } else {
            return ResponseEntity.badRequest().body("Member not found in the club.");
        }
    }

    @PutMapping("/{id}/members")
    public ResponseEntity<String> updateMembers(@PathVariable Integer id, @RequestBody List<String> newMembers) {
        clubService.updateClubMembers(id, newMembers);
        return ResponseEntity.ok("Club members updated successfully.");
    }

    @PutMapping("/{id}/members/{oldMemberName}")
    public ResponseEntity<String> updateMember(
            @PathVariable Integer id,
            @PathVariable String oldMemberName,
            @RequestBody String newMemberName) {
        if (clubService.updateMemberInClub(id, oldMemberName, newMemberName)) {
            return ResponseEntity.ok("Member updated successfully.");
        } else {
            return ResponseEntity.badRequest().body("Member not found.");
        }
    }

    @GetMapping("/{id}/members")
    public List<String> getMembers(@PathVariable Integer id) {
        return clubService.getMembersOfClub(id); // Retrieve the members of the specified club
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateClub(@PathVariable Integer id, @RequestBody Club clubJSON) {
        Club existingClub = clubService.getClubByID(id);

        if (existingClub == null) {
            return ResponseEntity.badRequest().body("Club was not found");
        }

        if (clubJSON.getClubName() != null) {
            existingClub.setClubName(clubJSON.getClubName());
        }

        if (clubJSON.getClubPicture() != null) {
            existingClub.setClubPicture(clubJSON.getClubPicture());
        }

        clubService.updateClub(existingClub);

        return ResponseEntity.ok("The club has been updated accordingly.");
    }
}