package com.learnings.iot_platform.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnings.iot_platform.dto.sensordata.CreateSensorDataRequestDto;
import com.learnings.iot_platform.exception.SensorNotFoundException;
import com.learnings.iot_platform.repository.SensorRepository;
import com.learnings.iot_platform.service.SensorDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SensorDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private SensorRepository sensorRepository;

    @MockBean
    private SensorDataService sensorDataService;



    @BeforeEach
    public void setUp() {
        UserDetails userDetails = User.withUsername("testuser")
                .password("password")
                .roles("USER")
                .build();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void givenValidSensorDataDetails_whenSensorDataIsCreated_thenReturnStatus201() throws Exception {
        CreateSensorDataRequestDto createSensorDataRequestDto = new CreateSensorDataRequestDto();
        createSensorDataRequestDto.setSensorId("1");
        createSensorDataRequestDto.setBattery(54d);
        createSensorDataRequestDto.setTemperature(55d);
        createSensorDataRequestDto.setLongitude(16d);
        createSensorDataRequestDto.setLatitude(18d);

        when(sensorRepository.existsById("1")).thenReturn(true);
        doNothing()
                .when(sensorDataService)
                        .storeSensorData(any(CreateSensorDataRequestDto.class));

        mockMvc.perform(post("/sensors-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createSensorDataRequestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void givenInvalidSensorDataDetails_whenSensorDataIsCreated_thenReturnStatus400() throws Exception {
        CreateSensorDataRequestDto createSensorDataRequestDto = new CreateSensorDataRequestDto();
        createSensorDataRequestDto.setSensorId("1");
        createSensorDataRequestDto.setBattery(54d);
        createSensorDataRequestDto.setTemperature(55d);
        createSensorDataRequestDto.setLongitude(16d);
        createSensorDataRequestDto.setLatitude(18d);

        when(sensorRepository.existsById("1")).thenReturn(true);
        doThrow(new SensorNotFoundException("Sensor not found with id: " + "1"))
                .when(sensorDataService)
                        .storeSensorData(any(CreateSensorDataRequestDto.class));

        mockMvc.perform(post("/sensors-data")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createSensorDataRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Sensor not found with id: " + "1"));
    }


}
