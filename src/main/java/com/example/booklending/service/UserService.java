package com.example.booklending.service;

import com.example.booklending.dto.UserDto;
import com.example.booklending.exceptions.ConflictException;
import com.example.booklending.exceptions.UserAlreadyExistsException;
import com.example.booklending.model.User;
import com.example.booklending.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j  // This annotation adds a logger instance to the class
public class UserService implements UserDetailsService {

    private ModelMapper modelMapper;

    private final UserRepository userRepository;

    @Autowired
    public UserService(ModelMapper modelMapper, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    @Transactional
    public Optional<UserDto> createUser(UserDto userDto) {
        log.info("Attempting to create a new user with username: {} and email: {}", userDto.getUsername(), userDto.getEmail());
        try {
            // Check if the user already exists by username or email
            if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
                log.warn("Username already exists: {}", userDto.getUsername());
                throw new UserAlreadyExistsException("Username already exists.");
            }
            if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
                log.warn("Email already exists: {}", userDto.getEmail());
                throw new UserAlreadyExistsException("Email already exists.");
            }

            User user = modelMapper.map(userDto, User.class);
            User savedUser = userRepository.save(user);
            log.info("User created successfully with ID: {}", savedUser.getId());
            return Optional.of(modelMapper.map(savedUser, UserDto.class));

        } catch (UserAlreadyExistsException e) {
            log.error("Conflict occurred during user creation: {}", e.getMessage());
            throw new ConflictException(e.getMessage());
        } catch (Exception e) {
            log.error("An error occurred while creating the user: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<UserDto> getUserById(Long id) {
        log.info("Fetching user with ID: {}", id);
        return userRepository.findById(id)
                .flatMap(user -> {
                    log.info("User found with ID: {}", id);
                    return dtoFromEntity(user);
                });
    }

    public Optional<UserDto> updateUser(Long id, UserDto userDtoToUpdate) {
        log.info("Updating user with ID: {}", id);
        return entityFromDto(userDtoToUpdate)
                .map(userToUpdate -> {
                    userToUpdate.setId(id);  // Ensure the ID remains the same
                    User updatedUser = userRepository.save(userToUpdate);
                    log.info("User updated successfully with ID: {}", id);
                    return updatedUser;
                })
                .flatMap(this::dtoFromEntity);
    }

    public void deleteUser(Long id) {
        log.info("Attempting to delete user with ID: {}", id);
        Optional<User> userToDelete = userRepository.findById(id);
        if (userToDelete.isPresent()) {
            userRepository.delete(userToDelete.get());
            log.info("User deleted successfully with ID: {}", id);
        } else {
            log.error("User not found with ID: {}", id);
            throw new EntityNotFoundException("User not found with ID: " + id);
        }
    }

    public Optional<UserDto> getUserByUsername(String username) {
        log.info("Fetching user with username: {}", username);
        return userRepository.findByUsername(username)
                .flatMap(user -> {
                    log.info("User found with username: {}", username);
                    return dtoFromEntity(user);
                });
    }

    public Optional<UserDto> getUserByEmail(String email) {
        log.info("Fetching user with email: {}", email);
        return userRepository.findByEmail(email)
                .flatMap(user -> {
                    log.info("User found with email: {}", email);
                    return dtoFromEntity(user);
                });
    }

    // Method to get all users and return them as a list of UserDto objects
    public Iterable<UserDto> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(source -> {
                    log.debug("Mapping user entity to DTO for user ID: {}", source.getId());
                    return modelMapper.map(source, UserDto.class);
                })
                .collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user details for username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found with username: {}", username);
                    return new UsernameNotFoundException("User not found");
                });

        log.info("User details loaded successfully for username: {}", username);
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>());  // No authorities are used in this project
    }

    private Optional<UserDto> dtoFromEntity(User user) {
        log.debug("Mapping user entity to DTO for user ID: {}", user.getId());
        return Optional.of(modelMapper.map(user, UserDto.class));
    }

    private Optional<User> entityFromDto(UserDto userDto) {
        log.debug("Mapping user DTO to entity for user ID: {}", userDto.getId());
        return Optional.of(modelMapper.map(userDto, User.class));
    }
}