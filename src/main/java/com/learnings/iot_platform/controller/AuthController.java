package com.learnings.iot_platform.controller;

import com.learnings.iot_platform.dto.ApiResponse;
import com.learnings.iot_platform.dto.auth.AuthResponseDto;
import com.learnings.iot_platform.dto.user.CreateUserDto;
import com.learnings.iot_platform.dto.user.LoginUserDto;
import com.learnings.iot_platform.model.User;
import com.learnings.iot_platform.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody CreateUserDto createUserDto) {
        User registeredUser = userService.createUser(createUserDto);
        ApiResponse apiResponse = new ApiResponse(registeredUser.getUsername() + " registered successfully");
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginUser(@Valid @RequestBody LoginUserDto loginUserDto) {
        System.out.println(loginUserDto);
        String token = userService.login(loginUserDto);
        AuthResponseDto authResponseDto = new AuthResponseDto(token, loginUserDto.getUsername());
        return new ResponseEntity<>(authResponseDto, HttpStatus.OK);
    }
}
