package com.example.booklending.model;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("unit")
class UserEntityTest {


    @Test
    public void testUserCreation() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        Role role = new Role();
        role.setName("admin");
        user.setRole(role);
        user.setId(678L);

        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("admin", user.getRole().getName());
        assertEquals(678L, user.getId());
    }
}