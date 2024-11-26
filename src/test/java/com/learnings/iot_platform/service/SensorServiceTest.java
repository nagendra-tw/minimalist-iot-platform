package com.learnings.iot_platform.service;

import com.learnings.iot_platform.dto.SensorRequestDto;
import com.learnings.iot_platform.dto.SensorResponseDto;
import com.learnings.iot_platform.model.Sensor;
import com.learnings.iot_platform.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SensorServiceTest {
    private SensorService sensorService;

    @Mock
    private SensorRepository sensorRepository;

    @BeforeEach
    void setup(){
        sensorService = new SensorService(sensorRepository);
    }

    @Test
    void givenSensorDetails_whenSensorIsCreated_thenCallsRepositorySave(){
        SensorRequestDto sensorRequestDto = new SensorRequestDto("Sensor1", 25.5, 17, 78);
        when(sensorRepository.save(any(Sensor.class))).thenReturn(new Sensor());

        sensorService.createSensor(sensorRequestDto);

        verify(sensorRepository, times(1)).save(any(Sensor.class));
    }

    @Test
    void givenValidSensorId_whenGettingSensor_thenReturnSensorDto() {
        String sensorId = "1";
        Sensor sensor = new Sensor(sensorId, "sensor1", 25.5, 17d, 78d);
        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor));

        SensorResponseDto sensorResponseDto = sensorService.getSensorById(sensorId);

        assertEquals(sensorId, sensorResponseDto.getSensorId());
    }

    @Test
    void givenInvalidSensorId_whenGettingSensor_thenReturnNull() {
        String invalidSensorId = "invalidSensorId";
        when(sensorRepository.findById(invalidSensorId)).thenReturn(Optional.empty());

        SensorResponseDto sensorResponseDto = sensorService.getSensorById(invalidSensorId);

        assertNull(sensorResponseDto);
    }
}
