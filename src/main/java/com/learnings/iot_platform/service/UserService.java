package com.learnings.iot_platform.service;

import com.learnings.iot_platform.dto.user.CreateUserDto;
import com.learnings.iot_platform.exception.UsernameAlreadyExistsException;
import com.learnings.iot_platform.model.User;
import com.learnings.iot_platform.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(CreateUserDto createUserDto) {
        if(userRepository.findByUsername(createUserDto.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        User user = convertUserDtoToUser(createUserDto);
        return userRepository.save(user);
    }

    private User convertUserDtoToUser(CreateUserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);
        return user;

    }
}
