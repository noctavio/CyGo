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
    public ResponseEntity<String> updateClub(@PathVariable Integer id, @RequestBody Club clubJSON) {
        Club existingClub = clubService.getClubByID(id);

        if (existingClub == null) {
            return ResponseEntity.badRequest().body("Club was not found");
        }



        if (clubJSON.getclubname() == null) {
                return ResponseEntity.badRequest().body("Invalid club name input");

        }



        if (clubJSON.getProfilepicture() != null) {
            existingClub.setProfilepicture(clubJSON.getProfilepicture());
        }

        if (clubJSON.getmember1() != null) {
            existingClub.setmember1(clubJSON.getmember1());
        }

        if (clubJSON.getmember2() != null) {
            existingClub.setmember2(clubJSON.getmember2());
        }

        if (clubJSON.getmember3() != null) {
            existingClub.setmember3(clubJSON.getmember3());
        }

        if (clubJSON.getmember4() != null) {
            existingClub.setmember4(clubJSON.getmember4());
        }
        if (clubJSON.getmember5() != null) {
            existingClub.setmember5(clubJSON.getmember5());
        }

        if (clubJSON.getmember6() != null) {
            existingClub.setmember6(clubJSON.getmember6());
        }

        if (clubJSON.getmember7() != null) {
            existingClub.setmember7(clubJSON.getmember7());
        }

        if (clubJSON.getmember8() != null) {
            existingClub.setmember8(clubJSON.getmember8());
        }
        if (clubJSON.getmember9() != null) {
            existingClub.setmember9(clubJSON.getmember9());
        }

        if (clubJSON.getmember10() != null) {
            existingClub.setmember10(clubJSON.getmember10());
        }

        if (clubJSON.getmember11() != null) {
            existingClub.setmember11(clubJSON.getmember11());
        }

        if (clubJSON.getmember12() != null) {
            existingClub.setmember12(clubJSON.getmember12());
        }
        if (clubJSON.getmember13() != null) {
            existingClub.setmember13(clubJSON.getmember13());
        }

        if (clubJSON.getmember14() != null) {
            existingClub.setmember14(clubJSON.getmember14());
        }

        if (clubJSON.getmember15() != null) {
            existingClub.setmember15(clubJSON.getmember15());
        }

        if (clubJSON.getmember16() != null) {
            existingClub.setmember16(clubJSON.getmember16());
        }
        if (clubJSON.getmember17() != null) {
            existingClub.setmember17(clubJSON.getmember17());
        }

        if (clubJSON.getmember18() != null) {
            existingClub.setmember18(clubJSON.getmember18());
        }

        if (clubJSON.getmember19() != null) {
            existingClub.setmember19(clubJSON.getmember19());
        }

        if (clubJSON.getMember20() != null) {
            existingClub.setMember20(clubJSON.getMember20());
        }


        clubService.updateClub(existingClub);

        return ResponseEntity.ok("The user has been updated accordingly.");
    }
    // TODO READ -> this controller does not require a delete method since you are only updating/overwriting data here, only deletes in club and using hardDelete method.
    //@DeleteMapping("/{username}")
    //public ResponseEntity<String> deleteSomethingByUsername(@PathVariable String username) {
    //    return ResponseEntity.ok("HEY YOU");
    //}
}

