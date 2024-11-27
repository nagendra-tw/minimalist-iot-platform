package com.learnings.iot_platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnings.iot_platform.dto.SensorRequestDto;
import com.learnings.iot_platform.dto.SensorResponseDto;
import com.learnings.iot_platform.service.SensorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(SensorController.class)
public class SensorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SensorService sensorService;

    @Test
    void givenSensorDetails_whenSensorIsCreated_thenReturnSensorIsSaved() throws Exception {
        String sensorId = "1";
        String sensorName = "sensor1";
        double temperature = 50;
        double latitude = 17;
        double longitude = 18;
        SensorRequestDto sensorRequestDto = new SensorRequestDto(sensorName, temperature, latitude, longitude);
        SensorResponseDto sensorResponseDto = new SensorResponseDto(sensorId, sensorName, temperature, latitude, longitude);

        when(sensorService.createSensor(any(SensorRequestDto.class))).thenReturn(sensorResponseDto);

        mockMvc.perform(post("/sensors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorRequestDto)))
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
        sensorResponseDtoList.add(new SensorResponseDto("1", "sensor1", 50, 17, 18));
        sensorResponseDtoList.add(new SensorResponseDto("2", "sensor2", 50, 17, 18));
        sensorResponseDtoList.add(new SensorResponseDto("3", "sensor3", 50, 17, 18));

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

}
