package com.example.sb.Controller;

import com.example.sb.Model.Tutorial;
import com.example.sb.Service.TutorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tutorials") // Base URL for the controller
public class TutorialController {

    @Autowired
    private TutorialService tutorialService;

    // Get all tutorials
    @GetMapping
    public List<Tutorial> getAllTutorials() {
        return tutorialService.getAllTutorials();
    }

    // Get tutorial by ID
    @GetMapping("/{id}")
    public Tutorial getTutorialById(@PathVariable Integer id) {
        return tutorialService.getTutorialById(id);
    }

    // Get tutorials by gameId and moveNumber
    @GetMapping("/game/{gameId}/move/{moveNumber}")
    public Tutorial getTutorialByGameAndMove(@PathVariable int gameId, @PathVariable int moveNumber) {
        return tutorialService.getTutorialByGameAndMove(gameId, moveNumber);
    }

    // POST endpoint for creating a new tutorial
    @PostMapping
    public ResponseEntity<String> createTutorial(@RequestBody Tutorial tutorial) {
        System.out.println("Received Tutorial: " + tutorial);  // Log the incoming tutorial
        tutorialService.createTutorial(tutorial);
        return ResponseEntity.ok("Tutorial saved successfully.");
    }

    // Update tutorial
    @PutMapping("/{id}")
    public ResponseEntity<String> updateTutorial(@PathVariable Integer id, @RequestBody Tutorial tutorial) {
        Tutorial existingTutorial = tutorialService.getTutorialById(id);

        if (existingTutorial == null) {
            return ResponseEntity.badRequest().body("Tutorial not found.");
        }

        tutorialService.updateTutorial(id, tutorial);
        return ResponseEntity.ok("Tutorial updated successfully.");
    }

    // Delete tutorial
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTutorial(@PathVariable Integer id) {
        if (tutorialService.deleteTutorial(id)) {
            return ResponseEntity.ok("Tutorial deleted successfully.");
        }
        else {
            return ResponseEntity.badRequest().body("Tutorial not found.");
        }
    }

    // Check if tutorial's gameId and moveNumber match the provided values
    @PostMapping("/check/{gameId}/{moveNumber}")
    public ResponseEntity<String> checkTutorialByGameAndMove(
            @PathVariable int gameId,
            @PathVariable int moveNumber,
            @RequestBody Tutorial inputTutorial) {

        // Check if the provided tutorial matches the given gameId and moveNumber
        if (inputTutorial.getGame_id() == gameId && inputTutorial.getMove_number() == moveNumber) {
            return ResponseEntity.ok("Success: The tutorial matches the provided gameId and moveNumber.");
        }
        else {
            return ResponseEntity.badRequest().body("Failure: The tutorial does not match the provided gameId and moveNumber.");
        }
    }
}
