package com.learnings.iot_platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnings.iot_platform.dto.user.CreateUserDto;
import com.learnings.iot_platform.dto.user.LoginUserDto;
import com.learnings.iot_platform.model.User;
import com.learnings.iot_platform.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(AuthController.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void givenCreateUserDto_whenRegistered_thenUserIsCreated() throws Exception {
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setUsername("username");
        createUserDto.setPassword("password");
        createUserDto.setEmail("username@email.com");
        User user = new User("userId", "username", "username", "passwordHash", null, null);
        when(userService.createUser(any(CreateUserDto.class))).thenReturn(user);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("username registered successfully"))
                ;
    }

    @Test
    void givenLoginUserDto_whenLoggingIn_thenAuthResponseIsReturned() throws Exception {
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setUsername("username");
        loginUserDto.setPassword("password");
        String token = "paosihfsjdsifjdsfihsdfhdfhfidhfihfdfhghhwhwheeijqiwijiieirierooew2134wsefjfe0gg";
        when(userService.login(loginUserDto)).thenReturn(token);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("username"))
                .andExpect(jsonPath("$.token").exists())
                ;
    }
}
