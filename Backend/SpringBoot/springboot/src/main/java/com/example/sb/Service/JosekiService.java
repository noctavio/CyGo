package com.example.sb.Service;

import com.example.sb.Model.*;
import com.example.sb.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JosekiService {
    @Autowired
    private JosekiRepository josekiRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private GobanJosekiRepository gobanJosekiRepository;

    public List<Joseki> getAllMoves() {
        return josekiRepository.findAll();
    }

    public List<Joseki> getAllPlayableMoves(String currentMove, GobanJoseki goban) {
        List<Joseki> allJoseki = josekiRepository.findAll();
        List<Joseki> playableJoseki = new ArrayList<>();
        for (Joseki joseki : allJoseki) {
            if (joseki.getLastMove().equals(currentMove)) {
                playableJoseki.add(joseki);
            }
        }
        if(playableJoseki.isEmpty()){
            return null;
        }
        return playableJoseki;
    }

    public Joseki getLastMoves(String currentMove) {
        List<Joseki> allJoseki = josekiRepository.findAll();
       Joseki currentJoseki = null;

        for (Joseki joseki : allJoseki) {
            if (joseki.getMoveNumber().equals(currentMove)) {
                currentJoseki = joseki;
            }
        }
        for (Joseki joseki : allJoseki) {
            assert currentJoseki != null;
            if (joseki.getMoveNumber().equals( currentJoseki.getLastMove())) {
                return joseki;
            }

        }
        return null;

    }
    public Joseki getCurrentMove(String currentMove) {
        List<Joseki> allJoseki = josekiRepository.findAll();


        for (Joseki joseki : allJoseki) {
            if (joseki.getMoveNumber().equals(currentMove)) {
                return joseki;
            }
        }



        return null;

    }
}
