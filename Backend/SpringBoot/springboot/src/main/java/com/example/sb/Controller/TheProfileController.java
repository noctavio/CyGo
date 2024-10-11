package com.example.sb.Controller;

import com.example.sb.Entity.Profile;
import com.example.sb.Repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PutMapping("/Profile/{username}")
    Profile updatePerson(@PathVariable String username, @RequestBody Profile request) {
        Profile profile = ProfileRepository.findByUSERNAME(username);

        if (profile == null) {
            throw new RuntimeException("profile id does not exist");
        }

        ProfileRepository.save(request);
        return ProfileRepository.findByUSERNAME(username);
    }

    @PutMapping("/Profile/{USERNAME}")
    String assignLaptopToPerson(@PathVariable String USERNAME) {
        Profile profile = ProfileRepository.findByUSERNAME(USERNAME);
        ProfileRepository.save(profile);
        return "";
    }
}

