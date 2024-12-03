package com.learnings.iot_platform.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnings.iot_platform.dto.user.CreateUserDto;
import com.learnings.iot_platform.dto.user.LoginUserDto;
import com.learnings.iot_platform.model.User;
import com.learnings.iot_platform.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("integration-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
public class UserIntegrationTestSuite {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4")
            .withExposedPorts(27017)
            .waitingFor(Wait.forListeningPort());
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void setMongoDBProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void testDatabaseConnection() {
        assertTrue(mongoDBContainer.isRunning(), "MongoDB test container should be running");
    }


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown(){
        userRepository.deleteAll();
    }

    @Test
    void testUserRegistration() throws Exception {
        CreateUserDto createUserDto = new CreateUserDto("username", "password", "user@mail.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("username registered successfully"))
        ;
    }

    @Test
    void testUserLogin() throws Exception {
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setUsername("username");
        loginUserDto.setPassword("password");

        createTemporaryUser();

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("username"))
                .andExpect(jsonPath("$.token").exists())
        ;
    }

    @Test
    void givenInvalidCredentials_whenLoggingIn_thenBadCredentialsResponseIsReturned() throws Exception {
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setUsername("username");
        loginUserDto.setPassword("wrong-password");

        createTemporaryUser();

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Bad Credentials: Invalid username or password"))
        ;
    }

    private void createTemporaryUser(){
        User user = new User();
        user.setUsername("username");
        user.setPasswordHash(passwordEncoder.encode("password"));
        user.setCreatedAt(LocalDateTime.now());
        user.setEmail("user@mail.com");
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);
        userRepository.save(user);
    }
}
