package com.learnings.iot_platform.service;

import com.learnings.iot_platform.dto.SensorDto;
import com.learnings.iot_platform.model.Sensor;
import com.learnings.iot_platform.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        SensorDto sensorDto = new SensorDto( "Sensor1", 25.5, 17, 78);
        when(sensorRepository.save(any(Sensor.class))).thenReturn(new Sensor());

        sensorService.createSensor(sensorDto);

        verify(sensorRepository, times(1)).save(any(Sensor.class));
    }
}
