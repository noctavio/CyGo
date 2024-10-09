package com.profile.User.profiles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class profileController {
    @Autowired
    profileRepository profileRepository;
    @GetMapping(path = "/Profile")
    List<profile> getAllPersons(){
        return profileRepository.findAll();
    }

    @GetMapping(path = "/Profile/{id}")
    profile getPersonById( @PathVariable int id){
        return profileRepository.findById(id);
    }

    @PostMapping(path = "/Profile")
    String createPerson(@RequestBody profile profile){
        System.out.println("Hello world");
        profileRepository.save(profile);
        return "";
    }

    @PostMapping(path = "/Profilesss")
    String d(){
        System.out.println("Hello world");
        return "";
    }

    /* not safe to update */
//    @PutMapping("/Persons/{id}")
//    Person updatePerson(@PathVariable int id, @RequestBody Person request){
//        Person Person = PersonRepository.findById(id);
//        if(Person == null)
//            return null;
//        PersonRepository.save(request);
//        return PersonRepository.findById(id);
//    }

    @PutMapping("/Profile/{id}")
    profile updatePerson(@PathVariable int id, @RequestBody profile request){
        profile profile = profileRepository.findById(id);

        if(profile == null) {
            throw new RuntimeException("profile id does not exist");
        }
        else if (profile.getId() != id){
            throw new RuntimeException("path variable id does not match Person request id");
        }

        profileRepository.save(request);
        return profileRepository.findById(id);
    }

    @PutMapping("/Profile")
    String assignLaptopToPerson(@PathVariable int ProfileId,@PathVariable int laptopId){
        profile profile = profileRepository.findById(ProfileId);
        profileRepository.save(profile);
        return "";
    }

    @DeleteMapping(path = "/Persons/{id}")
    String deletePerson(@PathVariable int id){
        profileRepository.deleteById(id);
        return "";
    }
}

