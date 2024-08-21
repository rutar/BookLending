package com.example.booklending.service;

import com.example.booklending.configuration.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("unit")
class AuthServiceTest {

    private AutoCloseable closeable;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        closeable =  MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void authenticateAndGenerateToken_ShouldReturnToken() {
        // Arrange
        String username = "testuser";
        String password = "testpassword";
        String expectedToken = "jwt-token";

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateToken(username)).thenReturn(expectedToken);

        // Act
        String actualToken = authService.authenticateAndGenerateToken(username, password);

        // Assert
        assertEquals(expectedToken, actualToken);
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, times(1)).generateToken(username);
        verifyNoMoreInteractions(authenticationManager, jwtUtil);
    }

    @Test
    void authenticateAndGenerateToken_ShouldSetAuthentication() {
        // Arrange
        String username = "testuser";
        String password = "testpassword";

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateToken(username)).thenReturn("jwt-token");

        // Act
        authService.authenticateAndGenerateToken(username, password);

        // Assert
        assertEquals(authentication, SecurityContextHolder.getContext().getAuthentication());
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, times(1)).generateToken(username);
    }
}
