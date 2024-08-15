package com.example.booklending.controller;

import com.example.booklending.dto.UserDto;
import com.example.booklending.model.User;
import com.example.booklending.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService)).build();
    }

    @Test
    @Tag("unit")
    public void testCreateUser() throws Exception {
        UserDto userDto = new UserDto("john_doe", "password123", "john.doe@example.com", 1);
        User savedUser = new User(1L, "john_doe", "password123", "john.doe@example.com", 1);

        when(userService.createUser(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    @Tag("unit")
    public void testGetUserById() throws Exception {
        User user = new User(1L, "john_doe", "password123", "john.doe@example.com", 1);

        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.password").value("password123"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    @Tag("unit")
    public void testGetAllUsers() throws Exception {

        ArrayList<User> users = new ArrayList<>();
        users.add(new User(1L, "john_doe", "password123", "john.doe@example.com", 1));
        users.add(new User(2L, "john_doe2", "password123", "2john.doe@example.com", 1));
        users.add(new User(3L, "john_doe3", "password123", "3john.doe@example.com", 2));

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].username").value("john_doe"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$[0].roleId").value(1))
                .andExpect(jsonPath("$[1].username").value("john_doe2"))
                .andExpect(jsonPath("$[1].email").value("2john.doe@example.com"))
                .andExpect(jsonPath("$[1].roleId").value(1))
                .andExpect(jsonPath("$[2].username").value("john_doe3"))
                .andExpect(jsonPath("$[2].email").value("3john.doe@example.com"))
                .andExpect(jsonPath("$[2].roleId").value(2));
    }

    @Test
    @Tag("unit")
    public void testGetUserByUsername() throws Exception {
        User user = new User(1L, "john_doe", "password123", "john.doe@example.com", 1);

        when(userService.getUserByUsername("john_doe")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/username/john_doe")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.password").value("password123"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    @Tag("unit")
    public void testGetUserByEmail() throws Exception {
        User user = new User(1L, "john_doe", "password123", "john.doe@example.com", 1);

        when(userService.getUserByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/email/john.doe@example.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.password").value("password123"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    @Tag("unit")
    public void testUpdateUser() throws Exception {
        UserDto userDto = new UserDto("john_doe_updated", "password1234", "john.doe.updated@example.com", 1);
        User updatedUser = new User(null, "john_doe_updated", "password1234", "john.doe.updated@example.com", 1);
        Optional<User> user = Optional.of(new User(1L, "john_doe_updated", "password1234", "john.doe.updated@example.com", 1));

        when(userService.getUserById(1L)).thenReturn(user);
        when(userService.updateUser(1L, updatedUser)).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe_updated"))
                .andExpect(jsonPath("$.email").value("john.doe.updated@example.com"));
    }

    @Test
    @Tag("unit")
    public void testDeleteUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(new User()));

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}
