package com.example.booklending.repository;

import com.example.booklending.AbstractIntegrationTest;
import com.example.booklending.model.Role;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Tag("integration")
@Transactional
public class RoleRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testSaveAndFindRoleByName() {
        // Arrange
        Role role = new Role();
        role.setName("ADMIN_ROLE");

        // Act
        Role savedRole = roleRepository.save(role);

        // Assert
        Optional<Role> foundRole = roleRepository.findById(Long.valueOf(savedRole.getId()));
        assertTrue(foundRole.isPresent());
        assertEquals("ADMIN_ROLE", foundRole.get().getName());
    }

    @Test
    void testFindRoleByName() {
        // Arrange
        Role role = new Role();
        role.setName("USER_ROLE");
        roleRepository.save(role);

        // Act
        Optional<Role> foundRole = roleRepository.findById(Long.valueOf(role.getId()));

        // Assert
        assertTrue(foundRole.isPresent());
        assertEquals("USER_ROLE", foundRole.get().getName());
    }
}
