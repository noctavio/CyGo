package com.example.sb.Controller;

import com.example.sb.Model.Tutorial;
import com.example.sb.Service.TutorialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tutorials")
@Tag(name = "TutorialController", description = "Controller for managing tutorials")
public class TutorialController {

    @Autowired
    private TutorialService tutorialService;

    @Operation(summary = "Get all tutorials", description = "Retrieve a list of all tutorials.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tutorial.class)))
    })
    @GetMapping
    public List<Tutorial> getAllTutorials() {
        return tutorialService.getAllTutorials();
    }

    @Operation(summary = "Get tutorial by ID", description = "Retrieve a specific tutorial by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the tutorial",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tutorial.class))),
            @ApiResponse(responseCode = "404", description = "Tutorial not found")
    })
    @GetMapping("/{id}")
    public Tutorial getTutorialById(@PathVariable Integer id) {
        return tutorialService.getTutorialById(id);
    }

    @Operation(summary = "Get tutorial by gameId and moveNumber", description = "Retrieve a tutorial by its associated gameId and moveNumber.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the tutorial",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tutorial.class))),
            @ApiResponse(responseCode = "404", description = "Tutorial not found")
    })
    @GetMapping("/game/{gameId}/move/{moveNumber}")
    public Tutorial getTutorialByGameAndMove(@PathVariable int gameId, @PathVariable int moveNumber) {
        return tutorialService.getTutorialByGameAndMove(gameId, moveNumber);
    }

    @Operation(summary = "Create a new tutorial", description = "Add a new tutorial to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tutorial created successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<String> createTutorial(
            @RequestBody(description = "Tutorial to be created", required = true,
                    content = @Content(schema = @Schema(implementation = Tutorial.class)))
            Tutorial tutorial) {

        System.out.println("Received Tutorial: " + tutorial);
        tutorialService.createTutorial(tutorial);
        return ResponseEntity.ok("Tutorial saved successfully.");
    }

    @Operation(summary = "Update a tutorial", description = "Update an existing tutorial by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tutorial updated successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Tutorial not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<String> updateTutorial(
            @PathVariable Integer id,
            @RequestBody(description = "Updated tutorial data", required = true,
                    content = @Content(schema = @Schema(implementation = Tutorial.class)))
            Tutorial tutorial) {

        Tutorial existingTutorial = tutorialService.getTutorialById(id);

        if (existingTutorial == null) {
            return ResponseEntity.badRequest().body("Tutorial not found.");
        }

        tutorialService.updateTutorial(id, tutorial);
        return ResponseEntity.ok("Tutorial updated successfully.");
    }

    @Operation(summary = "Delete a tutorial", description = "Delete a tutorial by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tutorial deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Tutorial not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTutorial(@PathVariable Integer id) {
        if (tutorialService.deleteTutorial(id)) {
            return ResponseEntity.ok("Tutorial deleted successfully.");
        } else {
            return ResponseEntity.badRequest().body("Tutorial not found.");
        }
    }

    @Operation(summary = "Check tutorial's gameId and moveNumber", description = "Validate if a tutorial matches the given gameId and moveNumber.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tutorial matches the provided values",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Tutorial does not match the provided values")
    })
    @PostMapping("/check/{gameId}/{moveNumber}")
    public ResponseEntity<String> checkTutorialByGameAndMove(
            @PathVariable int gameId,
            @PathVariable int moveNumber,
            @RequestBody(description = "Tutorial to validate", required = true,
                    content = @Content(schema = @Schema(implementation = Tutorial.class)))
            Tutorial inputTutorial) {

        if (inputTutorial.getGameId() == gameId && inputTutorial.getMoveNumber() == moveNumber) {
            return ResponseEntity.ok("Success: The tutorial matches the provided gameId and moveNumber.");
        } else {
            return ResponseEntity.badRequest().body("Failure: The tutorial does not match the provided gameId and moveNumber.");
        }
    }
}

