package com.example.booklending.controller;

import com.example.booklending.dto.UserDto;
import com.example.booklending.exception.UserAlreadyExistsException;
import com.example.booklending.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("unit")
class UserControllerTest {

    private MockMvc mockMvc;

    private AutoCloseable closeable;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void createUser_ShouldReturnCreatedStatus() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("testuser");

        when(userService.createUser(any(UserDto.class))).thenReturn(Optional.of(userDto));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"email\":\"test@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/users/1"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void createUser_ShouldReturnConflictStatus_WhenUserAlreadyExists() throws Exception {
        when(userService.createUser(any(UserDto.class))).thenThrow(new UserAlreadyExistsException("User already exists"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"email\":\"test@example.com\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().string("User already exists"));
    }

    @Test
    void getUserByUsername_ShouldReturnUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUsername("testuser");

        when(userService.getUserByUsername(anyString())).thenReturn(Optional.of(userDto));

        mockMvc.perform(get("/api/users/username/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void getUserByUsername_ShouldReturnNotFoundStatus_WhenUserNotFound() throws Exception {
        when(userService.getUserByUsername(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/username/nonexistentuser"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserByEmail_ShouldReturnUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");

        when(userService.getUserByEmail(anyString())).thenReturn(Optional.of(userDto));

        mockMvc.perform(get("/api/users/email/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getUserByEmail_ShouldReturnNotFoundStatus_WhenUserNotFound() throws Exception {
        when(userService.getUserByEmail(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/email/nonexistent@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);

        when(userService.getUserById(anyLong())).thenReturn(Optional.of(userDto));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getUserById_ShouldReturnNotFoundStatus_WhenUserNotFound() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        UserDto currentUserDto = new UserDto();
        currentUserDto.setId(1L);
        currentUserDto.setUsername("currentuser");

        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setId(1L);
        updatedUserDto.setUsername("updateduser");

        // Mocking the UserService responses
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(currentUserDto));
        when(userService.updateUser(anyLong(), any(UserDto.class))).thenReturn(Optional.of(updatedUserDto));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"updateduser\",\"email\":\"updated@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updateduser"));
    }


    @Test
    void updateUser_ShouldReturnNotFoundStatus_WhenUserNotFound() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"nonexistentuser\",\"email\":\"nonexistent@example.com\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_ShouldReturnNoContentStatus() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(new UserDto()));
        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_ShouldReturnNotFoundStatus_WhenUserNotFound() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound());
    }
}
