package com.learnings.iot_platform.unit.service;

import com.learnings.iot_platform.dto.sensordata.CreateSensorDataRequestDto;
import com.learnings.iot_platform.exception.SensorNotFoundException;
import com.learnings.iot_platform.model.SensorData;
import com.learnings.iot_platform.repository.SensorDataRepository;
import com.learnings.iot_platform.repository.SensorRepository;
import com.learnings.iot_platform.service.SensorDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void givenValidSensorDataDetails_whenSensorDataIsStored_thenCallsRepositorySave(){
        CreateSensorDataRequestDto createSensorDto = new CreateSensorDataRequestDto();
        createSensorDto.setSensorId("random-sensor-id");
        createSensorDto.setLatitude(17d);
        createSensorDto.setLongitude(18d);
        createSensorDto.setBattery(81d);
        createSensorDto.setTemperature(45d);
        when(sensorRepository.existsById(createSensorDto.getSensorId())).thenReturn(true);
        when(sensorDataRepository.save(any(SensorData.class))).thenReturn(
                new SensorData()
        );

        sensorDataService.storeSensorData(createSensorDto);

        verify(sensorDataRepository, times(1)).save(any(SensorData.class));
    }

    @Test
    void givenSensorDataDetailsWithInvalidSensorId_whenSensorDataIsStored_thenThrowSensorNotFoundException(){
        CreateSensorDataRequestDto createSensorDto = new CreateSensorDataRequestDto();
        createSensorDto.setSensorId("invalid-sensor-id");
        createSensorDto.setLatitude(17d);
        createSensorDto.setLongitude(18d);
        createSensorDto.setBattery(81d);
        createSensorDto.setTemperature(45d);

        when(sensorRepository.existsById(createSensorDto.getSensorId())).thenReturn(false);

        assertThrows(SensorNotFoundException.class,
                () -> sensorDataService.storeSensorData(createSensorDto));
    }
}
