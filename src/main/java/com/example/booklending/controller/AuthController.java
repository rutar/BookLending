package com.example.booklending.controller;

import com.example.booklending.model.AuthRequest;
import com.example.booklending.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")  // Allow your Angular app's URL
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest authRequest) {
        // Delegate the authentication and token generation to the service
        return authService.authenticateAndGenerateToken(authRequest.getUsername(), authRequest.getPassword());
    }
}


