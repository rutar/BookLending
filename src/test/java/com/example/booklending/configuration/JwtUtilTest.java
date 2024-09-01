package com.example.booklending.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private final String secretKey = "testSecretKey";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "secretKey", secretKey);
        // 1 hour
        long expirationTime = 3600000;
        ReflectionTestUtils.setField(jwtUtil, "expirationTime", expirationTime);
    }

    @Test
    void testGenerateTokenWithRole() {
        String username = "testUser";
        Optional<String> role = Optional.of("ROLE_USER");

        String token = jwtUtil.generateToken(username, role);

        assertNotNull(token);
        assertTrue(!token.isEmpty());

        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        assertEquals(username, claims.getSubject());
        assertEquals("ROLE_USER", claims.get("roleName"));
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    void testGenerateTokenWithoutRole() {
        String username = "testUser";
        Optional<String> role = Optional.empty();

        String token = jwtUtil.generateToken(username, role);

        assertNotNull(token);
        assertTrue(!token.isEmpty());

        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        assertEquals(username, claims.getSubject());
        assertNull(claims.get("roleName"));
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    void testExtractUsername() {
        String username = "testUser";
        String token = jwtUtil.generateToken(username, Optional.empty());

        String extractedUsername = jwtUtil.extractUsername(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    void testValidateTokenWithValidToken() {
        String username = "testUser";
        String token = jwtUtil.generateToken(username, Optional.empty());

        boolean isValid = jwtUtil.validateToken(token, username);

        assertTrue(isValid);
    }

    @Test
    void testValidateTokenWithInvalidUsername() {
        String username = "testUser";
        String token = jwtUtil.generateToken(username, Optional.empty());

        boolean isValid = jwtUtil.validateToken(token, "wrongUser");

        assertFalse(isValid);
    }

    @Test
    void testValidateTokenWithExpiredToken() throws io.jsonwebtoken.ExpiredJwtException, InterruptedException {
        // Set expiration to a very short time
        ReflectionTestUtils.setField(jwtUtil, "expirationTime", 1); // 100 ms

        // Generate a token for a test user
        String username = "testUser";
        String token = jwtUtil.generateToken(username, Optional.empty());

        // Wait for the token to expire
        Thread.sleep(10); // Ensure the wait time is longer than expiration

        // Validate the token and handle the exception
        try {
            jwtUtil.validateToken(token, username);
            fail(); // should not reach here
        } catch (ExpiredJwtException e) {
            // Handle the exception if necessary, or just assert that it is thrown
            assertTrue(e.getMessage().contains("JWT expired"), "Expected expired JWT exception");
        }
    }

}