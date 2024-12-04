package com.learnings.iot_platform.unit.service;

import com.learnings.iot_platform.dto.sensordata.CreateSensorDataDto;
import com.learnings.iot_platform.model.SensorData;
import com.learnings.iot_platform.repository.SensorDataRepository;
import com.learnings.iot_platform.repository.SensorRepository;
import com.learnings.iot_platform.service.SensorDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SensorDataServiceTest {
    private SensorDataService sensorDataService;

    @Mock
    private SensorDataRepository sensorDataRepository;

    @Mock
    private SensorRepository sensorRepository;

    @BeforeEach
    void setup() {
        sensorDataService = new SensorDataService(sensorDataRepository, sensorRepository);
    }

    @Test
    void givenSensorDataDetaisl_whenSensorDataIsStored_thenCallsRepositorySave(){
        CreateSensorDataDto createSensorDto = new CreateSensorDataDto();
        createSensorDto.setSensorId("random-sensor-id");
        createSensorDto.setLatitude(17d);
        createSensorDto.setLongitude(18d);
        createSensorDto.setBattery(81d);
        createSensorDto.setTemperature(45d);

        when(sensorDataRepository.save(any(SensorData.class))).thenReturn(
                new SensorData()
        );

        sensorDataService.storeSensorData(createSensorDto);

        verify(sensorDataRepository, times(1)).save(any(SensorData.class));
    }
}
