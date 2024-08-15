package com.example.booklending.repository;

import com.example.booklending.AbstractIntegrationTest;
import com.example.booklending.model.Role;
import com.example.booklending.model.User;
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
public class UserRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testSaveAndFindUserByUsername() {
        // Insert a role first
        Role role = new Role();
        role.setName("USER_ROLE");
        role = roleRepository.save(role);

        // Use the ID from the inserted role
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRoleId(role.getId()); // Ensure this ID exists in the roles table
        user.setUsername("testuser");

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsername("testuser");

        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    void testFindUserByEmail() {
        // Insert a role first
        Role role = new Role();
        role.setName("USER_ROLE");
        role = roleRepository.save(role);

        // Use the ID from the inserted role
        User user = new User();
        user.setEmail("unique@example.com");
        user.setPassword("password");
        user.setRoleId(role.getId()); // Ensure this ID exists in the roles table
        user.setUsername("uniqueuser");

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail("unique@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("uniqueuser", foundUser.get().getUsername());
    }
}
