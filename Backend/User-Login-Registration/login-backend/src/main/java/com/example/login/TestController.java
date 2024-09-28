package com.example.login.controller; // Make sure to match your package structure

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Test endpoint reached");
    }
}