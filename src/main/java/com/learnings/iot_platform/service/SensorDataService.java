package com.learnings.iot_platform.service;

import com.learnings.iot_platform.dto.sensordata.CreateSensorDataRequestDto;
import com.learnings.iot_platform.exception.SensorNotFoundException;
import com.learnings.iot_platform.model.SensorData;
import com.learnings.iot_platform.repository.SensorDataRepository;
import com.learnings.iot_platform.repository.SensorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SensorDataService {
    private final SensorDataRepository sensorDataRepository;
    private final SensorRepository sensorRepository;

    public SensorDataService(SensorDataRepository sensorDataRepository, SensorRepository sensorRepository) {
        this.sensorDataRepository = sensorDataRepository;
        this.sensorRepository = sensorRepository;
    }

    public void storeSensorData(CreateSensorDataRequestDto createSensorDto) {
        boolean isSensorPresent = sensorRepository.existsById(createSensorDto.getSensorId());
        System.out.println(isSensorPresent);
        if (isSensorPresent) {
            sensorDataRepository.save(mapCreateSensorDtoToSensorData(createSensorDto));
        } else {
            throw new SensorNotFoundException("Sensor not found with id: " + createSensorDto.getSensorId());
        }
    }

    private SensorData mapCreateSensorDtoToSensorData(CreateSensorDataRequestDto createSensorDto) {
        SensorData sensorData = new SensorData();
        sensorData.setSensorId(createSensorDto.getSensorId());
        sensorData.setLatitude(createSensorDto.getLatitude());
        sensorData.setLongitude(createSensorDto.getLongitude());
        sensorData.setTemperature(createSensorDto.getTemperature());
        sensorData.setBattery(createSensorDto.getBattery());
        sensorData.setTimestamp(LocalDateTime.now());
        return sensorData;
    }
}
