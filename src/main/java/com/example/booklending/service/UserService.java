package com.example.booklending.service;

import com.example.booklending.model.User;
import com.example.booklending.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User updateUser(Long id, User userToUpdate) {
        userToUpdate.setId(id);  // Ensure the ID remains the same
        return userRepository.save(userToUpdate);
    }

    public void deleteUser(Long id) {
        Optional<User> userOpt = getUserById(id);
        if (userOpt.isPresent()) {
            userRepository.delete(userOpt.get());
        } else {
            throw new EntityNotFoundException("User not found with ID: " + id);
        }
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Method to get all users and return them as a list of UserDto objects
    public List<User> getAllUsers() {
        return userRepository.findAll();  // Fetch all users from the database
    }
}
