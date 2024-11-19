package com.example.sb.Service;

import com.example.sb.Model.Tutorial;
import com.example.sb.Repository.TutorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TutorialService {

    @Autowired
    private TutorialRepository tutorialRepository;

    // Get all tutorials
    public List<Tutorial> getAllTutorials() {
        return tutorialRepository.findAll();
    }

    // Get tutorial by ID
    public Tutorial getTutorialById(Integer id) {
        Optional<Tutorial> tutorial = tutorialRepository.findById(id);
        if (tutorial.isPresent()) {
            return tutorial.get();
        } else {
            throw new ResourceNotFoundException("Tutorial not found with id: " + id);
        }
    }

    // Get tutorials by gameId and moveNumber
    public Tutorial getTutorialByGameAndMove(int gameId, int moveNumber) {
        return tutorialRepository.findByGameIdAndMoveNumber(gameId, moveNumber);
    }

    // Create a new tutorial
    public void createTutorial(Tutorial tutorial) {
        tutorialRepository.save(tutorial);
    }

    // Update an existing tutorial
    public void updateTutorial(Integer id, Tutorial tutorial) {
        Optional<Tutorial> existingTutorial = tutorialRepository.findById(id);
        if (existingTutorial.isPresent()) {
            Tutorial updatedTutorial = existingTutorial.get();
            updatedTutorial.setMove_id(tutorial.getMove_id());
            updatedTutorial.setGameId(tutorial.getGameId());
            updatedTutorial.setPlayerColor(tutorial.getPlayerColor());
            updatedTutorial.setX_position(tutorial.getX_position());
            updatedTutorial.setY_position(tutorial.getY_position());
            updatedTutorial.setMoveNumber(tutorial.getMoveNumber());
            tutorialRepository.save(updatedTutorial);
        } else {
            throw new ResourceNotFoundException("Tutorial not found with id: " + id);
        }
    }

    // Delete tutorial by ID
    public boolean deleteTutorial(Integer id) {
        Optional<Tutorial> tutorial = tutorialRepository.findById(id);
        if (tutorial.isPresent()) {
            tutorialRepository.delete(tutorial.get());
            return true;
        } else {
            throw new ResourceNotFoundException("Tutorial not found with id: " + id);
        }
    }

    // Check if the provided tutorial matches the given gameId and moveNumber
    public boolean checkTutorialByGameAndMove(int gameId, int moveNumber, Tutorial inputTutorial) {
        return inputTutorial.getGameId() == gameId && inputTutorial.getMoveNumber() == moveNumber;
    }
}
