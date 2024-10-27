package com.example.sb.Controller;

import com.example.sb.Service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/lobby/team")
@RestController
public class TeamController {
    @Autowired
    private TeamService teamService;


}
