package com.example.booklending.service;

import com.example.booklending.configuration.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public String authenticateAndGenerateToken(String username, String password) {
        logger.info("Attempting to authenticate user: {}", username);

        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            logger.info("User authenticated successfully: {}", username);

            // Generate and return JWT token
            String token = jwtUtil.generateToken(username);
            logger.info("Generated JWT token for user: {}", username);

            return token;
        } catch (Exception e) {
            logger.error("Authentication failed for user: {}", username, e);
            throw e;
        }
    }
}
