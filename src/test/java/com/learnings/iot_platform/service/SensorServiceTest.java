package com.learnings.iot_platform.service;

import com.learnings.iot_platform.dto.SensorCreateRequestDto;
import com.learnings.iot_platform.dto.SensorResponseDto;
import com.learnings.iot_platform.dto.SensorUpdateRequestDto;
import com.learnings.iot_platform.exception.SensorNotFoundException;
import com.learnings.iot_platform.model.Sensor;
import com.learnings.iot_platform.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        SensorCreateRequestDto sensorCreateRequestDto = new SensorCreateRequestDto("Sensor1", 25.5, 17, 78);
        when(sensorRepository.save(any(Sensor.class))).thenReturn(new Sensor("1", "Sensor1", 25.5, 17d, 78d, LocalDateTime.now(), LocalDateTime.now()));

        sensorService.createSensor(sensorCreateRequestDto);

        verify(sensorRepository, times(1)).save(any(Sensor.class));
    }

    @Test
    void shouldReturnsAllSensors() {
        List<Sensor> sensors = new ArrayList<>();
        sensors.add(new Sensor("1", "Sensor1", 25.5, 17d, 78d, LocalDateTime.now(), LocalDateTime.now()));
        sensors.add(new Sensor("2", "Sensor2", 25.5, 17d, 78d,LocalDateTime.now(), LocalDateTime.now()));
        sensors.add(new Sensor("3", "Sensor3", 25.5, 17d, 78d,LocalDateTime.now(), LocalDateTime.now()));
        when(sensorRepository.findAll()).thenReturn(sensors);

        List<SensorResponseDto> sensorsResponse = sensorService.getAllSensors();

        verify(sensorRepository, times(1)).findAll();
        assertEquals(sensors.size(), sensorsResponse.size());
    }

    @Test
    void givenValidSensorId_whenGettingSensor_thenReturnSensorDto() {
        String sensorId = "1";
        Sensor sensor = new Sensor(sensorId, "sensor1", 25.5, 17d, 78d, LocalDateTime.now(), LocalDateTime.now());
        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor));

        SensorResponseDto sensorResponseDto = sensorService.getSensorById(sensorId);

        assertEquals(sensorId, sensorResponseDto.getId());
    }


    @Test
    void givenValidSensorDetails_whenUpdatingSensor_thenReturnSensorDto() {
        String sensorId = "1";
        String newSensorName = "sensor2";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        SensorUpdateRequestDto sensorUpdateRequestDto = new SensorUpdateRequestDto(sensorId, newSensorName, 25.5, 17d, 78d, createdAt, updatedAt);
        Sensor sensor = new Sensor(sensorId, "sensor1", 25.5, 17d, 78d, createdAt, updatedAt);
        Sensor updatedSensor = new Sensor(sensorId, newSensorName, 25.5, 17d, 78d,createdAt, updatedAt);
        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor));
        when(sensorRepository.save(any(Sensor.class))).thenReturn(updatedSensor);

        SensorResponseDto updatedSensorResponseDto =  sensorService.updateSensor(sensorUpdateRequestDto);

        verify(sensorRepository, times(1)).findById(sensorId);

        ArgumentCaptor<Sensor> sensorArgumentCaptor = ArgumentCaptor.forClass(Sensor.class);
        verify(sensorRepository, times(1)).save(sensorArgumentCaptor.capture());

        Sensor savedSensor = sensorArgumentCaptor.getValue();
        assertEquals(sensorId, savedSensor.getId());
        assertEquals(newSensorName, savedSensor.getName());
        assertNotNull(savedSensor.getUpdatedAt());
    }

    @Test
    void givenValidSensorDetails_whenDeletingSensor_thenCallsRepositoryDelete(){
        String sensorId = "1";
        Sensor sensor = new Sensor(sensorId, "sensor1", 25.5, 17d, 78d, LocalDateTime.now(), LocalDateTime.now());
        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor));

        sensorService.deleteSensor(sensorId);

        verify(sensorRepository, times(1)).deleteById(sensorId);
    }

    @Test
    void givenInvalidSensorDetails_whenGettingSensor_thenThrowSensorNotFoundException() {
        String sensorId = "1";
        when(sensorRepository.findById(sensorId)).thenReturn(Optional.empty());

        assertThrows(SensorNotFoundException.class, () -> sensorService.getSensorById(sensorId));
    }

    @Test
    void givenInvalidSensorDetails_whenUpdatingSensor_thenThrowSensorNotFoundException() {
        String sensorId = "InvalidSensorId";
        SensorUpdateRequestDto sensorUpdateRequestDto = new SensorUpdateRequestDto(sensorId, "sensor2", 25.5, 17d, 78d, LocalDateTime.now(), LocalDateTime.now());
        when(sensorRepository.findById(sensorId)).thenReturn(Optional.empty());

        assertThrows(SensorNotFoundException.class, () -> sensorService.updateSensor(sensorUpdateRequestDto));
    }

    @Test
    void givenInvalidSensorDetails_whenDeletingSensor_thenThrowSensorNotFoundException() {
        String sensorId = "InvalidSensorId";

        when(sensorRepository.findById(sensorId)).thenReturn(Optional.empty());

        assertThrows(SensorNotFoundException.class, () -> sensorService.deleteSensor(sensorId));
    }

}
