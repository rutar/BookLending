package com.example.booklending.service;

import com.example.booklending.dto.UserDto;
import com.example.booklending.exceptions.ConflictException;
import com.example.booklending.model.Role;
import com.example.booklending.model.User;
import com.example.booklending.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("test123");
        user.setEmail("test@example.com");
        Role role = new Role();
        role.setName("ADMIN");
        user.setRole(role);

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("testuser");
        userDto.setPassword("test123");
        userDto.setEmail("test@example.com");
        userDto.setRoleId(1);
    }

    @Test
    void createUser_shouldReturnCreatedUserDto() {
        when(modelMapper.map(userDto, User.class)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        Optional<UserDto> result = userService.createUser(userDto);

        assertEquals(Optional.of(userDto), result);
        verify(userRepository).save(user);
    }

    @Test
    void createUser_shouldThrowConflictExceptionWhenUsernameExists() {
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.of(user));

        assertThrows(ConflictException.class, () -> userService.createUser(userDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowConflictExceptionWhenEmailExists() {
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(user));

        assertThrows(ConflictException.class, () -> userService.createUser(userDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_shouldReturnEmptyOptionalOnGeneralException() {
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Unexpected error"));
        when(modelMapper.map(userDto, User.class)).thenReturn(user);

        Optional<UserDto> result = userService.createUser(userDto);

        assertTrue(result.isEmpty());
    }

    @Test
    void getUserById_shouldReturnUserDtoWhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        Optional<UserDto> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(userDto, result.get());
    }

    @Test
    void getUserById_shouldReturnEmptyOptionalWhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<UserDto> result = userService.getUserById(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void updateUser_shouldReturnUpdatedUserDto() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(userDto, User.class)).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        Optional<UserDto> result = userService.updateUser(1L, userDto);

        assertEquals(Optional.of(userDto), result);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_shouldReturnEmptyOptionalWhenUpdateFails() {
        when(modelMapper.map(userDto, User.class)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(null);  // Simulating a failure in save

        Optional<UserDto> result = userService.updateUser(1L, userDto);

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteUser_shouldCallDeleteWhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_shouldThrowEntityNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void getUserByUsername_shouldReturnUserDtoWhenUserExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        Optional<UserDto> result = userService.getUserByUsername("testuser");

        assertTrue(result.isPresent());
        assertEquals(userDto, result.get());
    }

    @Test
    void getUserByUsername_shouldReturnEmptyOptionalWhenUserDoesNotExist() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        Optional<UserDto> result = userService.getUserByUsername("testuser");

        assertTrue(result.isEmpty());
    }

    @Test
    void getUserByEmail_shouldReturnUserDtoWhenUserExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        Optional<UserDto> result = userService.getUserByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals(userDto, result.get());
    }

    @Test
    void getUserByEmail_shouldReturnEmptyOptionalWhenUserDoesNotExist() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        Optional<UserDto> result = userService.getUserByEmail("test@example.com");

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllUsers_shouldReturnListOfUserDtos() {
        List<User> users = new ArrayList<>();
        users.add(user);
        when(userRepository.findAll()).thenReturn(users);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        Iterable<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.iterator().hasNext());
        assertEquals(userDto, result.iterator().next());
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetailsWhenUserExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        org.springframework.security.core.userdetails.UserDetails result = userService.loadUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test123", result.getPassword());
        assertFalse(result.getAuthorities().isEmpty());
    }

    @Test
    void loadUserByUsername_shouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("nonexistent"));
    }
}
