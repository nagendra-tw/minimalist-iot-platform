package com.learnings.iot_platform.service;

import com.learnings.iot_platform.dto.sensor.CreateSensorRequestDto;
import com.learnings.iot_platform.dto.sensor.SensorResponseDto;
import com.learnings.iot_platform.dto.sensor.UpdateSensorRequestDto;
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

    public SensorResponseDto createSensor(CreateSensorRequestDto createSensorRequestDto) {
        Sensor convertedSensor = convertSensorRequestDtoToSensor(createSensorRequestDto);
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

    public SensorResponseDto updateSensor(UpdateSensorRequestDto updateSensorRequestDto) {
        Sensor savedSensor = getSensor(updateSensorRequestDto.getSensorId());

        savedSensor.setName(updateSensorRequestDto.getSensorName());
        savedSensor.setTemperature(updateSensorRequestDto.getTemperature());
        savedSensor.setLatitude(updateSensorRequestDto.getLatitude());
        savedSensor.setLongitude(updateSensorRequestDto.getLongitude());
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

    private Sensor convertSensorRequestDtoToSensor(CreateSensorRequestDto createSensorRequestDto) {
        Sensor sensor = new Sensor();
        sensor.setName(createSensorRequestDto.getName());
        sensor.setTemperature(createSensorRequestDto.getTemperature());
        sensor.setLatitude(createSensorRequestDto.getLatitude());
        sensor.setLongitude(createSensorRequestDto.getLongitude());
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
