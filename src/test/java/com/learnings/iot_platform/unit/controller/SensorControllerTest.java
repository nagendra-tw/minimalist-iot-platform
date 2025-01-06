package com.learnings.iot_platform.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnings.iot_platform.dto.sensor.CreateSensorRequestDto;
import com.learnings.iot_platform.dto.sensor.SensorResponseDto;
import com.learnings.iot_platform.dto.sensor.UpdateSensorRequestDto;
import com.learnings.iot_platform.exception.SensorNotFoundException;
import com.learnings.iot_platform.service.SensorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(SensorController.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SensorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SensorService sensorService;

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
    void givenSensorDetails_whenSensorIsCreated_thenReturnSensorIsSaved() throws Exception {
        String sensorId = "1";
        String sensorName = "sensor1";
        double temperature = 50;
        double latitude = 17;
        double longitude = 18;
        CreateSensorRequestDto createSensorRequestDto = new CreateSensorRequestDto(sensorName, temperature, latitude, longitude);
        SensorResponseDto sensorResponseDto = new SensorResponseDto(sensorId, sensorName, temperature, latitude, longitude, LocalDateTime.now(), LocalDateTime.now(), null);

        when(sensorService.createSensor(any(CreateSensorRequestDto.class))).thenReturn(sensorResponseDto);

        performAdminAuthentication();
        mockMvc.perform(post("/sensors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createSensorRequestDto)))
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
        List<SensorResponseDto> sensorResponseDtoList = new ArrayList<>();
        sensorResponseDtoList.add(new SensorResponseDto("1", "sensor1", 50, 17, 18, LocalDateTime.now(), LocalDateTime.now(), null));
        sensorResponseDtoList.add(new SensorResponseDto("2", "sensor2", 50, 17, 18, LocalDateTime.now(), LocalDateTime.now(), null));
        sensorResponseDtoList.add(new SensorResponseDto("3", "sensor3", 50, 17, 18, LocalDateTime.now(), LocalDateTime.now(), null));

        when(sensorService.getAllSensors()).thenReturn(sensorResponseDtoList);

        mockMvc.perform(get("/sensors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("sensor1"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("sensor2"))
                .andExpect(jsonPath("$[2].id").value("3"))
                .andExpect(jsonPath("$[2].name").value("sensor3"))
        ;
    }

    @Test
    void givenSensorId_whenSensorIsDeleted_thenReturnSensorIsDeleted() throws Exception {
        String sensorId = "1";

        performAdminAuthentication();
        mockMvc.perform(delete("/sensors/{id}", sensorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Sensor deleted with id: " + sensorId));

        verify(sensorService, times(1)).deleteSensor(sensorId);
    }

    @Test
    void givenInvalidSensorId_whenSensorIsDeleted_thenReturnSensorNotFound() throws Exception {
        String sensorId = "InvalidSensorId";

        doThrow(new SensorNotFoundException(sensorId)).when(sensorService).deleteSensor(sensorId);

        performAdminAuthentication();
        mockMvc.perform(delete("/sensors/{id}", sensorId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Sensor not found with id: " + sensorId));

        verify(sensorService, times(1)).deleteSensor(sensorId);
    }

    @Test
    void givenSensorId_whenSensorIsRetrieved_thenReturnSensor() throws Exception {
        String sensorId = "1";

        when(sensorService.getSensorById(sensorId)).thenReturn(new SensorResponseDto("1", "sensor1", 23d,43d,56.6d, LocalDateTime.now(), LocalDateTime.now(), null));

        mockMvc.perform(get("/sensors/{id}", sensorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("sensor1"))
                .andExpect(jsonPath("$.temperature").value(23d))
                .andExpect(jsonPath("$.latitude").value(43d))
                .andExpect(jsonPath("$.longitude").value(56.6d))
        ;
    }

    @Test
    void givenInvalidSensorId_whenSensorIsRetrieved_thenReturnSensorNotFound() throws Exception {
        String sensorId = "InvalidSensorId";

        doThrow(new SensorNotFoundException(sensorId)).when(sensorService).getSensorById(sensorId);

        mockMvc.perform(get("/sensors/{id}", sensorId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Sensor not found with id: " + sensorId));

        verify(sensorService, times(1)).getSensorById(sensorId);
    }

    @Test
    void givenUpdatedSensor_whenSensorIsUpdated_thenReturnUpdatedSensor() throws Exception {
        String sensorId = "1";
        String newSensorName = "Sensor-v2.0";
        UpdateSensorRequestDto updateSensorRequestDto = new UpdateSensorRequestDto(sensorId, newSensorName, 30,40,60, LocalDateTime.now(), LocalDateTime.now());
        SensorResponseDto sensorResponseDto = new SensorResponseDto(sensorId, newSensorName, 30, 40, 60, LocalDateTime.now(), LocalDateTime.now(), null);
        when(sensorService.updateSensor(updateSensorRequestDto)).thenReturn(sensorResponseDto);

        performAdminAuthentication();
        mockMvc.perform(put("/sensors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateSensorRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value(newSensorName))
                .andExpect(jsonPath("$.temperature").value(30))
                .andExpect(jsonPath("$.latitude").value(40))
                .andExpect(jsonPath("$.longitude").value(60))
        ;
    }

    @Test
    void givenInvalidSensorId_whenSensorIsUpdated_thenReturnSensorNotFound() throws Exception {
        String sensorId = "InvalidSensorId";
        UpdateSensorRequestDto updateSensorRequestDto = new UpdateSensorRequestDto(sensorId, "sensor1", 30,40,60, LocalDateTime.now(), LocalDateTime.now());


        doThrow(new SensorNotFoundException(sensorId)).when(sensorService).updateSensor(updateSensorRequestDto);

        performAdminAuthentication();
        mockMvc.perform(put("/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateSensorRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Sensor not found with id: " + sensorId));
        ;

        verify(sensorService, times(1)).updateSensor(updateSensorRequestDto);
    }

    private void performAdminAuthentication(){
        UserDetails userDetails = User.withUsername("testuser")
                .password("password")
                .roles("ADMIN")
                .build();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
