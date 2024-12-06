package com.learnings.iot_platform.service;

import com.learnings.iot_platform.dto.sensordata.CreateSensorDataRequestDto;
import com.learnings.iot_platform.exception.SensorNotFoundException;
import com.learnings.iot_platform.model.SensorData;
import com.learnings.iot_platform.producer.SensorDataProducerService;
import com.learnings.iot_platform.repository.SensorDataRepository;
import com.learnings.iot_platform.repository.SensorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SensorDataService {
    private final SensorDataRepository sensorDataRepository;
    private final SensorRepository sensorRepository;
    private final SensorDataProducerService sensorDataProducerService;

    public SensorDataService(SensorDataRepository sensorDataRepository, SensorRepository sensorRepository, SensorDataProducerService sensorDataProducerService) {
        this.sensorDataRepository = sensorDataRepository;
        this.sensorRepository = sensorRepository;
        this.sensorDataProducerService = sensorDataProducerService;
    }

    public void storeSensorData(CreateSensorDataRequestDto createSensorDto) {
        boolean isSensorPresent = sensorRepository.existsById(createSensorDto.getSensorId());
        if (isSensorPresent) {
            SensorData sensorData =  sensorDataRepository.save(mapCreateSensorDtoToSensorData(createSensorDto));
            System.out.println(sensorData);
            sensorDataProducerService.produceSensorDataStoredEvent(sensorData);
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
