package com.learnings.iot_platform.unit.service;

import com.learnings.iot_platform.auth.JwtUtil;
import com.learnings.iot_platform.dto.user.CreateUserDto;
import com.learnings.iot_platform.dto.user.LoginUserDto;
import com.learnings.iot_platform.exception.UsernameAlreadyExistsException;
import com.learnings.iot_platform.model.User;
import com.learnings.iot_platform.repository.UserRepository;
import com.learnings.iot_platform.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() {
        userService = new UserService(userRepository, passwordEncoder, jwtUtil, authenticationManager);
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

    @Test
    void givenLoginDetails_whenLoginUser_thenTokenIsReturned() {
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setUsername("username");
        loginUserDto.setPassword("password");

        Authentication mockAuthentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);

        // Mocking the JWT generation
        String token = "valid-jwt-token";
        when(jwtUtil.generateToken(mockAuthentication)).thenReturn(token);

        // Call the method to test
        String result = userService.login(loginUserDto);

        // Verify that the authenticationManager.authenticate() was called once
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Verify that the JWT token is generated
        verify(jwtUtil, times(1)).generateToken(mockAuthentication);

        // Verify the result
        assertEquals(token, result);
    }

    @Test
    void givenIncorrectLoginDetails_whenLoginUser_thenThrowBadCredentialsException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad Credentials: Invalid username or password"));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> userService.login(new LoginUserDto()));

        assertEquals("Bad Credentials: Invalid username or password", exception.getMessage());
    }
}
