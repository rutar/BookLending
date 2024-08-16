package com.example.booklending.service;

import com.example.booklending.dto.UserDto;
import com.example.booklending.model.User;
import com.example.booklending.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Optional<UserDto> createUser(UserDto userDto) {

        try {
            return entityFromDto(userDto)
                    .map(userRepository::save)
                    .flatMap(this::dtoFromEntity);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<UserDto> getUserById(Long id) {
        Optional<User> foundUser = userRepository.findById(id);
        return foundUser.map(user -> modelMapper.map(user, UserDto.class));
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
        return Optional.of(modelMapper.map(userRepository.findByUsername(username), UserDto.class));
    }

    public Optional<UserDto> getUserByEmail(String email) {
        return Optional.of(modelMapper.map(userRepository.findByEmail(email), UserDto.class));
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