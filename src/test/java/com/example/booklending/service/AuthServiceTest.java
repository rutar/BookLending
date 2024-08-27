package com.example.booklending.service;

import com.example.booklending.configuration.JwtUtil;
import com.example.booklending.configuration.RoleIdGrantedAuthority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void authenticateAndGenerateToken_success() {
        // Arrange
        String username = "user1";
        String password = "password1";
        String roleName = "anmin";
        String expectedToken = "jwt-token";

        // Create the granted authority
        RoleIdGrantedAuthority grantedAuthority = new RoleIdGrantedAuthority(roleName);

        // Mock the return type
        Collection<GrantedAuthority> authorities = Collections.singletonList(grantedAuthority);

        // Set up mocks
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection)authorities);
        when(jwtUtil.generateToken(username, Optional.of(roleName))).thenReturn(expectedToken);

        // Mock SecurityContextHolder
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        // Act
        String token = authService.authenticateAndGenerateToken(username, password);

        // Assert
        assertEquals(expectedToken, token);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken(username, Optional.of(roleName));
    }

    @Test
    void authenticateAndGenerateToken_failure() {
        // Arrange
        String username = "user1";
        String password = "wrong-password";

        // Simulate authentication failure
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Authentication failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.authenticateAndGenerateToken(username, password));

        assertEquals("Authentication failed", exception.getMessage());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, never()).generateToken(anyString(), any());
    }
}
