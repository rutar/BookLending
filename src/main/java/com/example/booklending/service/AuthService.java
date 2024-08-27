package com.example.booklending.service;

import com.example.booklending.configuration.JwtUtil;
import com.example.booklending.configuration.RoleIdGrantedAuthority;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

            Optional<String> roleName = authentication.getAuthorities().stream()
                    .filter(authority -> authority instanceof RoleIdGrantedAuthority)  // Filter custom authority
                    .map(GrantedAuthority::getAuthority) // Extract roleId
                    .findFirst();  // Return the first matching roleId

            // Generate and return JWT token
            String token = jwtUtil.generateToken(username, roleName);
            logger.info("Generated JWT token for user: {}", username);

            return token;
        } catch (Exception e) {
            logger.error("Authentication failed for user: {}", username, e);
            throw e;
        }
    }
}
