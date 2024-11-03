package com.example.sb.Controller;

import com.example.sb.Service.GobanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/goban")
@RestController
public class GobanController {
    @Autowired
    private GobanService gobanService;

}
