package com.example.booklending.model;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
class RoleEntityTest {

    @Test
    public void testRoleCreationWithNoArgsConstructor() {
        // Arrange
        Role role = new Role();
        role.setName("Admin");

        // Act & Assert
        assertNotNull(role);
        assertEquals("Admin", role.getName());
        assertNull(role.getId()); // ID should be null if not set
    }

    @Test
    public void testRoleCreationWithNameConstructor() {
        // Arrange
        Role role = new Role("User");

        // Act & Assert
        assertNotNull(role);
        assertEquals("User", role.getName());
        assertNull(role.getId()); // ID should be null if not set
    }

    @Test
    public void testRoleCreationWithAllArgsConstructor() {
        // Arrange
        Role role = new Role(1, "Moderator");

        // Act & Assert
        assertNotNull(role);
        assertEquals(1, role.getId());
        assertEquals("Moderator", role.getName());
    }

    @Test
    public void testRoleBuilder() {
        // Arrange
        Role role = Role.builder()
                .id(2)
                .name("Admin")
                .build();

        // Act & Assert
        assertNotNull(role);
        assertEquals(2, role.getId());
        assertEquals("Admin", role.getName());
    }
}
