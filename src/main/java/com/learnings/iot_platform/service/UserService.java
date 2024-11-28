package com.learnings.iot_platform.service;

import com.learnings.iot_platform.auth.JwtUtil;
import com.learnings.iot_platform.dto.auth.AuthResponseDto;
import com.learnings.iot_platform.dto.user.CreateUserDto;
import com.learnings.iot_platform.exception.UsernameAlreadyExistsException;
import com.learnings.iot_platform.model.User;
import com.learnings.iot_platform.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public User createUser(CreateUserDto createUserDto) {
        if(userRepository.findByUsername(createUserDto.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }
        System.out.println(createUserDto);

        User user = convertUserDtoToUser(createUserDto);
        return userRepository.save(user);
    }

    public String login(String username, String password){
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return jwtUtil.generateToken(authentication);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
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
