package com.learnings.iot_platform.service;

import com.learnings.iot_platform.dto.user.CreateUserDto;
import com.learnings.iot_platform.exception.UsernameAlreadyExistsException;
import com.learnings.iot_platform.model.User;
import com.learnings.iot_platform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        userService = new UserService(userRepository, passwordEncoder);
    }
    // todo: rename the dto classes
    @Test
    void givenUserDetails_whenUserIsCreated_thenUserIsReturned() {
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setUsername("username");
        createUserDto.setPassword("password");
        createUserDto.setEmail("email@yahoo.com");
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        User user = new User("userId", "username", "email@yahoo.com", "randomPasswordHash", roles, LocalDateTime.now());

        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.createUser(createUserDto);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void givenUserDetails_whenUsernameAlreadyExists_thenThrowUsernameAlreadyExistsException() {
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setUsername("username");
        createUserDto.setPassword("password");
        createUserDto.setEmail("email@yahoo.com");
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        User user = new User("userId", "username", "email@yahoo.com", "randomPasswordHash", roles, LocalDateTime.now());

        when(userRepository.findByUsername(createUserDto.getUsername())).thenReturn(Optional.of(user));
        assertThrows(UsernameAlreadyExistsException.class, () -> userService.createUser(createUserDto));

    }

}
