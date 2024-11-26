package com.learnings.iot_platform.service;

import com.learnings.iot_platform.dto.SensorRequestDto;
import com.learnings.iot_platform.dto.SensorResponseDto;
import com.learnings.iot_platform.model.Sensor;
import com.learnings.iot_platform.repository.SensorRepository;
import org.springframework.stereotype.Service;

@Service
public class SensorService {

    private SensorRepository sensorRepository;

    public SensorService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    public Sensor createSensor(SensorRequestDto sensorRequestDto) {
        Sensor convertedSensor = convertSensorRequestDtoToSensor(sensorRequestDto);
        Sensor savedSensor = sensorRepository.save(convertedSensor);
        return savedSensor;
    }

    public SensorResponseDto getSensorById(String sensorId) {
        Sensor sensor = sensorRepository.findById(sensorId).orElse(null);
        if (sensor == null) {
            return null;
        }
        return convertSensorToSensorResponse(sensor);
    }

    private Sensor convertSensorRequestDtoToSensor(SensorRequestDto sensorRequestDto) {
        Sensor sensor = new Sensor();
        sensor.setName(sensorRequestDto.getSensorName());
        sensor.setTemperature(sensorRequestDto.getTemperature());
        sensor.setLatitude(sensorRequestDto.getLatitude());
        sensor.setLongitude(sensorRequestDto.getLongitude());
        return sensor;
    }

    private SensorResponseDto convertSensorToSensorResponse(Sensor sensor) {
        SensorResponseDto sensorResponseDto = new SensorResponseDto();
        sensorResponseDto.setSensorId(sensor.getId());
        sensorResponseDto.setSensorName(sensor.getName());
        sensorResponseDto.setTemperature(sensor.getTemperature());
        sensorResponseDto.setLatitude(sensor.getLatitude());
        sensorResponseDto.setLongitude(sensor.getLongitude());
        return sensorResponseDto;
    }

}
