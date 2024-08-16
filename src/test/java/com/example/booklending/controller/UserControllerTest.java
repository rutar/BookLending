package com.example.booklending.controller;

import com.example.booklending.context.TestSecurityConfiguration;
import com.example.booklending.dto.UserDto;
import com.example.booklending.service.UserService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfiguration.class)
@Tag("unit")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void createUser_Success() throws Exception {
        UserDto userDto = new UserDto(1L, "John", "john123", "john@example.com", 1);
        Mockito.when(userService.createUser(any(UserDto.class))).thenReturn(Optional.of(userDto));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"John\", \"email\": \"john@example.com\", \"password\": \"john123\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/users/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("John"));
    }

    @Test
    void createUser_BadRequest() throws Exception {
        Mockito.when(userService.createUser(any(UserDto.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"John\", \"email\": \"john@example.com\", \"password\": \"john123\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserByUsername_Success() throws Exception {
        UserDto userDto = new UserDto(1L, "John", "john123", "john@example.com", 1);
        Mockito.when(userService.getUserByUsername("John")).thenReturn(Optional.of(userDto));

        mockMvc.perform(get("/api/users/username/John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void getUserByUsername_NotFound() throws Exception {
        Mockito.when(userService.getUserByUsername("Unknown")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/username/Unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserByEmail_Success() throws Exception {
        UserDto userDto = new UserDto(1L, "John", "john123", "john@example.com", 1);
        Mockito.when(userService.getUserByEmail("john@example.com")).thenReturn(Optional.of(userDto));

        mockMvc.perform(get("/api/users/email/john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.username").value("John"));
    }

    @Test
    void getUserByEmail_NotFound() throws Exception {
        Mockito.when(userService.getUserByEmail("unknown@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/email/unknown@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserById_Success() throws Exception {
        UserDto userDto = new UserDto(1L, "John", "john123", "john@example.com", 1);
        Mockito.when(userService.getUserById(1L)).thenReturn(Optional.of(userDto));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("John"));
    }

    @Test
    void getUserById_NotFound() throws Exception {
        Mockito.when(userService.getUserById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_Success() throws Exception {
        UserDto updatedUserDto = new UserDto(1L, "John Updated", "john123", "john_updated@example.com", 1);
        Mockito.when(userService.getUserById(1L)).thenReturn(Optional.of(updatedUserDto));
        Mockito.when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(Optional.of(updatedUserDto));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"John Updated\", \"email\": \"john_updated@example.com\", \"password\": \"john123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("John Updated"))
                .andExpect(jsonPath("$.email").value("john_updated@example.com"));
    }

    @Test
    void updateUser_NotFound() throws Exception {
        Mockito.when(userService.getUserById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"John Updated\", \"email\": \"john_updated@example.com\", \"password\": \"john123\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_Success() throws Exception {
        UserDto userDto = new UserDto(1L, "John", "john123", "john@example.com", 1);
        Mockito.when(userService.getUserById(1L)).thenReturn(Optional.of(userDto));
        Mockito.doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_NotFound() throws Exception {
        Mockito.when(userService.getUserById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNotFound());
    }
}
