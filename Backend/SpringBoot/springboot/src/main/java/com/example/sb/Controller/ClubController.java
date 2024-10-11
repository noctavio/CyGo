package com.example.sb.Controller;

import com.example.sb.Entity.Club;
import com.example.sb.Repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/Club")
@RestController
public class ClubController {

    @Autowired
    private ClubRepository clubRepository;

    // Create a new Club
    @PostMapping
    public Club createClub(@RequestBody Club club) {
        return clubRepository.save(club);
    }

    // Get all Clubs
    @GetMapping
    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }

    // Get Club by CLUB
    @GetMapping("/{CLUB}")
    public ResponseEntity<Club> getClubByClubname(@PathVariable String CLUB) {
        Club club = clubRepository.findByCLUB(CLUB);
        return club != null ? ResponseEntity.ok(club) : ResponseEntity.notFound().build();
    }

    // Update an existing Club by clubName
    @PutMapping("/update/{CLUB}")
    public ResponseEntity<Club> updateClub(@PathVariable String CLUB, @RequestBody Club updatedClub) {
        Club club = clubRepository.findByCLUB(CLUB);
        if (club != null) {
            // Update the fields of the retrieved entity
            club.setCLUB(updatedClub.getCLUB());
            club.setMember1(updatedClub.getMember1());
            club.setMember2(updatedClub.getMember2());
            club.setMember3(updatedClub.getMember3());
            club.setMember4(updatedClub.getMember4());
            club.setMember5(updatedClub.getMember5());
            club.setMember6(updatedClub.getMember6());
            club.setMember7(updatedClub.getMember7());
            club.setMember8(updatedClub.getMember8());
            club.setMember9(updatedClub.getMember9());
            club.setMember10(updatedClub.getMember10());
            club.setMember11(updatedClub.getMember11());
            club.setMember12(updatedClub.getMember12());
            club.setMember13(updatedClub.getMember13());
            club.setMember14(updatedClub.getMember14());
            club.setMember15(updatedClub.getMember15());
            club.setMember16(updatedClub.getMember16());
            club.setMember17(updatedClub.getMember17());
            club.setMember18(updatedClub.getMember18());
            club.setMember19(updatedClub.getMember19());
            club.setMember20(updatedClub.getMember20());

            // Save the updated entity to the database
            Club savedClub = clubRepository.save(club);
            return ResponseEntity.ok(savedClub);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a Club by clubname
    @DeleteMapping("/{CLUB}")
    public ResponseEntity<String> deleteClubByClubname(@PathVariable String CLUB) {
        Club club = clubRepository.findByCLUB(CLUB);
        if (club != null) {
            clubRepository.delete(club);
            return ResponseEntity.ok("Club deleted successfully.");
        } else {
            return ResponseEntity.status(200).body("Club not found.");
        }
    }
}