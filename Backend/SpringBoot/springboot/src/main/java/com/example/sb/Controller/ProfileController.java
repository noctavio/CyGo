package com.example.sb.Controller;

import com.example.sb.Entity.Profile;
import com.example.sb.Repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProfileController {
    @Autowired
    ProfileRepository ProfileRepository;
    @GetMapping(path = "/Profile")
    List<Profile> getAllPersons(){
        return ProfileRepository.findAll();
    }

    @GetMapping(path = "/Profile/{id}")
    Profile getPersonById( @PathVariable int id){
        return ProfileRepository.findById(id);
    }

    @PostMapping(path = "/Profile")
    String createPerson(@RequestBody Profile profile){
        System.out.println("Hello world");
        ProfileRepository.save(profile);
        return "";
    }

    @PutMapping("/Profile/{id}")
    Profile updatePerson(@PathVariable int id, @RequestBody Profile request){
        Profile profile = ProfileRepository.findById(id);

        if(profile == null) {
            throw new RuntimeException("profile id does not exist");
        }
        else if (profile.getId() != id){
            throw new RuntimeException("path variable id does not match Person request id");
        }

        ProfileRepository.save(request);
        return ProfileRepository.findById(id);
    }

    @PutMapping("/Profile")
    String assignLaptopToPerson(@PathVariable int ProfileId,@PathVariable int laptopId){
        Profile profile = ProfileRepository.findById(ProfileId);
        ProfileRepository.save(profile);
        return "";
    }

    @DeleteMapping(path = "/Profile/{id}")
    String deletePerson(@PathVariable int id){
        ProfileRepository.deleteById(id);
        return "";
    }
}

