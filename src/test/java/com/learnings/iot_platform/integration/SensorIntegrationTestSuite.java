package com.learnings.iot_platform.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnings.iot_platform.auth.JwtUtil;
import com.learnings.iot_platform.dto.sensor.CreateSensorRequestDto;
import com.learnings.iot_platform.dto.sensor.UpdateSensorRequestDto;
import com.learnings.iot_platform.model.Sensor;
import com.learnings.iot_platform.model.User;
import com.learnings.iot_platform.repository.SensorRepository;
import com.learnings.iot_platform.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("integration-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
public class SensorIntegrationTestSuite {
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4")
            .withExposedPorts(27017)
            .waitingFor(Wait.forListeningPort());

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void testDatabaseConnection() {
        assertTrue(mongoDBContainer.isRunning(), "Mongodb container should be running");
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        sensorRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        sensorRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateSensor_whenAdminLoggedIn() throws Exception {
        String sensorName = "sensor1";
        double temperature = 50;
        double latitude = 17;
        double longitude = 18;
        CreateSensorRequestDto createSensorRequestDto = new CreateSensorRequestDto(sensorName, temperature, latitude, longitude);
        createTemporaryAdmin();
        String token = getAdminBearerToken();
        mockMvc.perform(post("/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createSensorRequestDto))
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(sensorName))
                .andExpect(jsonPath("$.temperature").value(temperature))
                .andExpect(jsonPath("$.latitude").value(latitude))
                .andExpect(jsonPath("$.longitude").value(longitude))
        ;
    }

    @Test
    void shouldReturnAllSensors() throws Exception {
        createOneSensor("1");
        createOneSensor("2");
        createOneSensor("3");

        createTemporaryUser();
        String token = getUserBearerToken();

        mockMvc.perform(get("/sensors")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
        ;
    }

    @Test
    void shouldDeleteASensorWithValidId() throws Exception {
        createOneSensor("1");
        String sensorId = "1";
        createTemporaryAdmin();
        String token = getAdminBearerToken();

        mockMvc.perform(
                delete("/sensors/{id}", sensorId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Sensor deleted with id: " + sensorId)
        );
    }

    @Test
    void shouldReturnSensorNotFoundWhenDeletingASensorWithInvalidId() throws Exception {
        String sensorId = "InvalidSensorId";
        createOneSensor("1");
        createTemporaryAdmin();
        String token = getAdminBearerToken();

        mockMvc.perform(delete("/sensors/{id}", sensorId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Sensor not found with id: " + sensorId));

    }

    @Test
    void shouldReturnSensor() throws Exception {
        createOneSensor("1");
        createTemporaryAdmin();
        String sensorId = "1";
        String token = getAdminBearerToken();
        mockMvc.perform(get("/sensors/{id}", sensorId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("sensor"))
                .andExpect(jsonPath("$.temperature").value(50d))
                .andExpect(jsonPath("$.latitude").value(17d))
                .andExpect(jsonPath("$.longitude").value(18d))
        ;
    }

    @Test
    void shouldReturnSensorNotFoundWhenSensorWithInvalidIdIsRetrieved() throws Exception {
        String sensorId = "InvalidSensorId";
        createOneSensor("1");
        createTemporaryAdmin();
        String token = getAdminBearerToken();

        mockMvc.perform(delete("/sensors/{id}", sensorId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Sensor not found with id: " + sensorId));

    }

    @Test
    void shouldUpdateSensor() throws Exception {
        String sensorId = "1";
        String newSensorName = "Sensor-v2.0";
        createOneSensor(sensorId);
        createTemporaryAdmin();
        UpdateSensorRequestDto updateSensorRequestDto = new UpdateSensorRequestDto(sensorId, newSensorName, 50d, 17d, 18d, LocalDateTime.now(), LocalDateTime.now());

        String token = getAdminBearerToken();
        mockMvc.perform(
                put("/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateSensorRequestDto))
                        .header("Authorization" , "Bearer " + token)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value(newSensorName))
                .andExpect(jsonPath("$.temperature").value(50d))
                .andExpect(jsonPath("$.latitude").value(17d))
                .andExpect(jsonPath("$.longitude").value(18d));
    }

    @Test
    void shouldReturnSensorNotFoundWhenUpdatingSensorWithInvalidId() throws Exception {
        String sensorId = "InvalidSensorId";
        UpdateSensorRequestDto updateSensorRequestDto = new UpdateSensorRequestDto(sensorId, "sensor", 50d, 17d, 18d, LocalDateTime.now(), LocalDateTime.now());
        createOneSensor("1");
        createTemporaryAdmin();
        String token = getAdminBearerToken();

        mockMvc.perform(put("/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateSensorRequestDto))
                        .header("Authorization" , "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Sensor not found with id: " + sensorId));
    }


    private void createTemporaryUser(){
        User user = new User();
        user.setUsername("user");
        user.setPasswordHash(passwordEncoder.encode("password"));
        user.setCreatedAt(LocalDateTime.now());
        user.setEmail("user@mail.com");
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);
        userRepository.save(user);
    }

    private void createTemporaryAdmin() {
        User user = new User();
        user.setUsername("admin");
        user.setPasswordHash(passwordEncoder.encode("password"));
        user.setCreatedAt(LocalDateTime.now());
        user.setEmail("admin@mail.com");
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_ADMIN");
        user.setRoles(roles);
        userRepository.save(user);
    }

    private void createOneSensor(String id) {
        Sensor sensor = new Sensor();
        sensor.setName("sensor");
        sensor.setCreatedAt(LocalDateTime.now());
        sensor.setUpdatedAt(LocalDateTime.now());
        sensor.setLatitude(17d);
        sensor.setLongitude(18d);
        sensor.setTemperature(50d);
        sensor.setId(id);
        sensorRepository.save(sensor);
    }

    private String getAdminBearerToken() {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("admin", "password")
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtil.generateToken(authentication);
    }

    private String getUserBearerToken() {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("user", "password")
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtil.generateToken(authentication);
    }

}
