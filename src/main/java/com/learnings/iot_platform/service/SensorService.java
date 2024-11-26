package com.learnings.iot_platform.service;

import com.learnings.iot_platform.dto.SensorDto;
import com.learnings.iot_platform.model.Sensor;
import com.learnings.iot_platform.repository.SensorRepository;
import org.springframework.stereotype.Service;

@Service
public class SensorService {

    private SensorRepository sensorRepository;

    public SensorService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    public Sensor createSensor(SensorDto sensorDto) {
        Sensor convertedSensor = convertSensorDtoToSensorDto(sensorDto);
        Sensor savedSensor = sensorRepository.save(convertedSensor);
        System.out.println(savedSensor);
        return savedSensor;
    }

    private Sensor convertSensorDtoToSensorDto(SensorDto sensorDto) {
        Sensor sensor = new Sensor();
        sensor.setName(sensorDto.getSensorName());
        sensor.setTemperature(sensorDto.getTemperature());
        sensor.setLatitude(sensorDto.getLatitude());
        sensor.setLongitude(sensorDto.getLongitude());
        return sensor;
    }
}
