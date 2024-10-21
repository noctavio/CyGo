package com.example.sb.Controller;

import com.example.sb.Entity.Club;
import com.example.sb.Service.ClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
@RestController
public class TheProfileController {

    @Autowired
    private ClubService clubService;

    /**
     * Returns a list of the top 10 players sorted by rank (highest to lowest)
     * @return list
     */

    @GetMapping("/clubs")
    public List<Club> getAllClubs() {
        return clubService.getAllClubs();
    }

    @GetMapping(path = "/clubs/{id}")
    public Club getClubById(@PathVariable Integer id) {
        return clubService.getClubByID(id);
    }
    

    @PutMapping("/profiles/update/{id}")
    public ResponseEntity<String> updateProfile(@PathVariable Integer id, @RequestBody TheProfile profileJSON) {
        TheProfile existingUser = TheProfileService.getProfileByID(id);

        if (existingUser == null) {
            return ResponseEntity.badRequest().body("Profile was not found");
        }

        if ((profileJSON.getWins() != null && profileJSON.getWins() < 0) || (profileJSON.getLoss() != null && profileJSON.getLoss() < 0)) {

            return ResponseEntity.badRequest().body("Wins and losses must be non-negative.");
        }

        if (profileJSON.getRank() != null) {
            if (Arrays.asList(RankConstants.RANKS).contains(profileJSON.getRank())) {
                existingUser.setRank(profileJSON.getRank());
            }
            else {
                return ResponseEntity.badRequest().body("Invalid rank input... please review Go ranks -> (30 kyu - 1 kyu) and (1 dan - 9 dan)");
            }
        }

        if (profileJSON.getClubpicture() != null) {
            existingUser.setClubpicture(profileJSON.getClubpicture());
        }

        if (profileJSON.getProfilepicture() != null) {
            existingUser.setProfilepicture(profileJSON.getProfilepicture());
        }

        if (profileJSON.getClubname() != null) {
            existingUser.setClubname(profileJSON.getClubname());
        }

        if (profileJSON.getWins() != null) {
            existingUser.setWins(profileJSON.getWins());
        }

        if (profileJSON.getLoss() != null) {
            existingUser.setLoss(profileJSON.getLoss());
        }

        existingUser.setGamesplayed();
        TheProfileService.updateProfile(existingUser);

        return ResponseEntity.ok("The user has been updated accordingly.");
    }
    // TODO READ -> this controller does not require a delete method since you are only updating/overwriting data here, only deletes in club and using hardDelete method.
    //@DeleteMapping("/{username}")
    //public ResponseEntity<String> deleteSomethingByUsername(@PathVariable String username) {
    //    return ResponseEntity.ok("HEY YOU");
    //}
}

