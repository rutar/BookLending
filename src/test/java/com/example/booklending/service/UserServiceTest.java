package com.example.booklending.service;

import com.example.booklending.model.User;
import com.example.booklending.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser() {
        User user = new User();
        user.setUsername("john_doe");
        when(userRepository.save(user)).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals("john_doe", createdUser.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getUserById() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserById(1L);

        assertTrue(foundUser.isPresent());
        assertEquals(1L, foundUser.get().getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void updateUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john_doe_updated");
        when(userRepository.save(user)).thenReturn(user);

        User updatedUser = userService.updateUser(1L, user);

        assertNotNull(updatedUser);
        assertEquals("john_doe_updated", updatedUser.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void deleteUser_userExists() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUser_userDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(1L));

        assertEquals("User not found with ID: 1", thrown.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(0)).delete(any(User.class));
    }

    @Test
    void getUserByUsername() {
        User user = new User();
        user.setUsername("john_doe");
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserByUsername("john_doe");

        assertTrue(foundUser.isPresent());
        assertEquals("john_doe", foundUser.get().getUsername());
        verify(userRepository, times(1)).findByUsername("john_doe");
    }

    @Test
    void getUserByEmail() {
        User user = new User();
        user.setEmail("john.doe@example.com");
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserByEmail("john.doe@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("john.doe@example.com", foundUser.get().getEmail());
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
    }

    @Test
    void getAllUsers() {
        User user1 = new User();
        user1.setUsername("john_doe");

        User user2 = new User();
        user2.setUsername("jane_doe");

        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<User> allUsers = userService.getAllUsers();

        assertNotNull(allUsers);
        assertEquals(2, allUsers.size());
        verify(userRepository, times(1)).findAll();
    }
}
