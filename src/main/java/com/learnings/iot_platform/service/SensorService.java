package com.learnings.iot_platform.service;

import com.learnings.iot_platform.dto.sensor.SensorCreateRequestDto;
import com.learnings.iot_platform.dto.sensor.SensorResponseDto;
import com.learnings.iot_platform.dto.sensor.SensorUpdateRequestDto;
import com.learnings.iot_platform.exception.SensorNotFoundException;
import com.learnings.iot_platform.model.Sensor;
import com.learnings.iot_platform.repository.SensorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SensorService {

    private SensorRepository sensorRepository;

    public SensorService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    public SensorResponseDto createSensor(SensorCreateRequestDto sensorCreateRequestDto) {
        Sensor convertedSensor = convertSensorRequestDtoToSensor(sensorCreateRequestDto);
        convertedSensor.setCreatedAt(LocalDateTime.now());
        convertedSensor.setUpdatedAt(LocalDateTime.now());
        Sensor savedSensor = sensorRepository.save(convertedSensor);
        return convertSensorToSensorResponse(savedSensor);
    }

    public List<SensorResponseDto> getAllSensors() {
        List<Sensor> sensors = sensorRepository.findAll();
        return sensors.stream().map(this::convertSensorToSensorResponse).toList();
    }

    public SensorResponseDto getSensorById(String sensorId) {
        Sensor sensor = getSensor(sensorId);

        return convertSensorToSensorResponse(sensor);
    }

    public SensorResponseDto updateSensor(SensorUpdateRequestDto sensorUpdateRequestDto) {
        Sensor savedSensor = getSensor(sensorUpdateRequestDto.getSensorId());

        savedSensor.setName(sensorUpdateRequestDto.getSensorName());
        savedSensor.setTemperature(sensorUpdateRequestDto.getTemperature());
        savedSensor.setLatitude(sensorUpdateRequestDto.getLatitude());
        savedSensor.setLongitude(sensorUpdateRequestDto.getLongitude());
        savedSensor.setUpdatedAt(LocalDateTime.now());
        Sensor updatedSensor = sensorRepository.save(savedSensor);
        return convertSensorToSensorResponse(updatedSensor);
    }

    public void deleteSensor(String sensorId) {
        Sensor existingSensor = getSensor(sensorId);
        sensorRepository.deleteById(existingSensor.getId());
    }

    private Sensor getSensor(String sensorId) {
        return sensorRepository.findById(sensorId).orElseThrow(() -> new SensorNotFoundException(sensorId));
    }

    private Sensor convertSensorRequestDtoToSensor(SensorCreateRequestDto sensorCreateRequestDto) {
        Sensor sensor = new Sensor();
        sensor.setName(sensorCreateRequestDto.getName());
        sensor.setTemperature(sensorCreateRequestDto.getTemperature());
        sensor.setLatitude(sensorCreateRequestDto.getLatitude());
        sensor.setLongitude(sensorCreateRequestDto.getLongitude());
        return sensor;
    }

    private SensorResponseDto convertSensorToSensorResponse(Sensor sensor) {
        SensorResponseDto sensorResponseDto = new SensorResponseDto();
        sensorResponseDto.setId(sensor.getId());
        sensorResponseDto.setName(sensor.getName());
        sensorResponseDto.setTemperature(sensor.getTemperature());
        sensorResponseDto.setLatitude(sensor.getLatitude());
        sensorResponseDto.setLongitude(sensor.getLongitude());
        sensorResponseDto.setCreatedAt(sensor.getCreatedAt());
        sensorResponseDto.setUpdatedAt(sensor.getUpdatedAt());
        return sensorResponseDto;
    }

}
