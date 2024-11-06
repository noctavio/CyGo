package com.example.sb.Service;

import com.example.sb.Model.*;
import com.example.sb.Repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GobanService {
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private GobanRepository gobanRepository;
    @Autowired
    private LobbyRepository lobbyRepository;

    //public List<Integer> getPlayerTurnlist(Integer lobbyId) {
    //    Optional<Lobby> lobbyOptional = lobbyRepository.findById(lobbyId);

    //    if (lobbyOptional.isPresent()) {
    //        Lobby lobby = lobbyOptional.get();

    //        Goban board = lobby.getBoard();
    //        return board.getPlayerIdTurnList();
    //    }
    //    return null;
    //}

    // TODO get board

    public String getBoardState(Integer lobbyId) {
        Optional<Lobby> lobbyOptional = lobbyRepository.findById(lobbyId);
        if (lobbyOptional.isPresent()) {
            Lobby lobby = lobbyOptional.get();
            Goban board = lobby.getGoban();

            return board.getBoardState();
        }
        return null;
    }
}
