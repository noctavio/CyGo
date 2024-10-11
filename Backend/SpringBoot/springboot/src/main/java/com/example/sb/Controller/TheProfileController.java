package com.example.sb.Controller;

import com.example.sb.Entity.Club;
import com.example.sb.Entity.Profile;
import com.example.sb.Repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TheProfileController {
    @Autowired
    ProfileRepository ProfileRepository;

    @GetMapping(path = "/Profile")
    List<Profile> getAllPProfiles() {
        return ProfileRepository.findAll();
    }

    @GetMapping(path = "/Profile/{username}")
    Profile getProfileByUsername(@PathVariable String username) {
        return ProfileRepository.findByUSERNAME(username);
    }

    @PostMapping(path = "/Profile")
    String createPerson(@RequestBody Profile profile) {
        ProfileRepository.save(profile);
        return "";
    }

    @PutMapping("/Profile/{USERNAME}")
    public ResponseEntity<Profile> updateClub(@PathVariable String USERNAME, @RequestBody Profile updatedProfile) {
        Profile profile = ProfileRepository.findByUSERNAME(USERNAME);
        if (profile != null) {
            // Update the fields of the retrieved entity
            profile.setUSERNAME(updatedProfile.getUSERNAME());
            profile.setCLUB(updatedProfile.getCLUB());
            profile.setPROFILE_PICTURE(updatedProfile.getPROFILE_PICTURE());

            // Save the updated entity to the database
            Profile savedProfile = ProfileRepository.save(profile);
            return ResponseEntity.ok(savedProfile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

