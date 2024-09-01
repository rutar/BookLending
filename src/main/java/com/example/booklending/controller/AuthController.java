package com.example.booklending.controller;

import com.example.booklending.model.AuthRequest;
import com.example.booklending.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Operations related to user authentication")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Login and obtain a token",
            description = "Authenticate a user using their username and password, and obtain a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @PostMapping("/login")
    public String login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Authentication request containing username and password",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AuthRequest.class))
            )
            @RequestBody AuthRequest authRequest) {
        // Delegate the authentication and token generation to the service
        return authService.authenticateAndGenerateToken(authRequest.getUsername(), authRequest.getPassword());
    }
}
