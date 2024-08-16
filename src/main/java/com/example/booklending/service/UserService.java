package com.example.booklending.service;

import com.example.booklending.dto.UserDto;
import com.example.booklending.exceptions.ConflictException;
import com.example.booklending.exceptions.UserAlreadyExistsException;
import com.example.booklending.model.User;
import com.example.booklending.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService {

    private ModelMapper modelMapper;

    private final UserRepository userRepository;

    @Autowired
    public UserService(ModelMapper modelMapper, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    @Transactional
    public Optional<UserDto> createUser(UserDto userDto) {
        try {
            // Check if the user already exists by username or email
            if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
                throw new UserAlreadyExistsException("Username already exists.");
            }
            if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
                throw new UserAlreadyExistsException("Email already exists.");
            }

            User user = modelMapper.map(userDto, User.class);
            User savedUser = userRepository.save(user);
            return Optional.of(modelMapper.map(savedUser, UserDto.class));

        } catch (UserAlreadyExistsException e) {
            // Handle the specific case where the user already exists
            throw new ConflictException(e.getMessage());
        } catch (Exception e) {
            // Handle general exceptions
            return Optional.empty();
        }
    }

    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id).flatMap(this::dtoFromEntity);
    }


    public Optional<UserDto> updateUser(Long id, UserDto userDtoToUpdate) {
        return entityFromDto(userDtoToUpdate)
                .map(userToUpdate -> {
                    userToUpdate.setId(id);  // Ensure the ID remains the same
                    return userRepository.save(userToUpdate);
                })
                .flatMap(this::dtoFromEntity);
    }

    public void deleteUser(Long id) {
        Optional<User> userToDelete = userRepository.findById(id);
        if (userToDelete.isPresent()) {
            userRepository.delete(userToDelete.get());
        } else {
            throw new EntityNotFoundException("User not found with ID: " + id);
        }
    }

    public Optional<UserDto> getUserByUsername(String username) {
        return userRepository.findByUsername(username).flatMap(this::dtoFromEntity);
    }

    public Optional<UserDto> getUserByEmail(String email) {
        return userRepository.findByEmail(email).flatMap(this::dtoFromEntity);
    }

    // Method to get all users and return them as a list of UserDto objects
    public Iterable<UserDto> getAllUsers() {
        return userRepository.findAll().stream()                  // Fetch all users from the database
                .map(source -> modelMapper.map(source, UserDto.class))
                .collect(Collectors.toList());

    }

    private Optional<UserDto> dtoFromEntity(User user) {
        return Optional.of(modelMapper.map(user, UserDto.class));
    }

    private Optional<User> entityFromDto(UserDto userDto) {
        return Optional.of(modelMapper.map(userDto, User.class));
    }

}